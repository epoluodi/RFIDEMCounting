package com.honeywell.android.rfidemcounting;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.honeywell.android.data.utils.Transform;
import com.honeywell.android.rfidemcounting.adapter.RFIDlistAdapter;
import com.honeywell.android.rfidemcounting.bean.EmList;
import com.honeywell.android.rfidemcounting.bean.RFIDList;
import com.honeywell.android.rfidemcounting.utils.CommonUtil;
import com.honeywell.rfidservice.EventListener;
import com.honeywell.rfidservice.RfidManager;
import com.honeywell.rfidservice.TriggerMode;
import com.honeywell.rfidservice.rfid.AntennaPower;
import com.honeywell.rfidservice.rfid.OnTagReadListener;
import com.honeywell.rfidservice.rfid.RfidReader;
import com.honeywell.rfidservice.rfid.TagAdditionData;
import com.honeywell.rfidservice.rfid.TagReadData;
import com.honeywell.rfidservice.rfid.TagReadOption;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import io.realm.Realm;

public class RFIDListActivity extends BaseActivity {
    private static final String TAG = "RFIDListActivity";
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    private static  List<RFIDList> mList ;
    private RFIDlistAdapter rfiDlistAdapter;
    private String task;
    private EmList emList;

    @BindView(R.id.tv_all)
    TextView tv_all;
    @BindView(R.id.tv_count)
    TextView tv_count;
    private RfidManager mRfidMgr;
    private MyHandler mMyHandler;
    private MyApplication mMyApplication;
    private RfidReader mReader;
    @BindView(R.id.tv_unknown)
    TextView tv_unknown;

    private final static String ACTION_HONEYWLL="com.honeywell";
    private String filePath ;
    private Realm realm;
    Dialog loadingDialog;
    private BroadcastReceiver broadcastReceiver=new  BroadcastReceiver() {
      //  public static final String TAG = "MyBroadcastReceiver";

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_HONEYWLL)) {
                if (intent.hasExtra("data")) {
                    final String decode=intent.getStringExtra("data");
                    Log.v(TAG,decode);
                  boolean isexists=false;
                       for (int i = 0; i < mList.size(); i++) {
                           if (mList.get(i).getEpcid().equals(decode) ) {
                               isexists=true;
                               if (mList.get(i).getState().equals("未盘")) {
                                   realm.beginTransaction();
                                   mList.get(i).setState("已盘");
                                   realm.insertOrUpdate(mList);
                                   realm.commitTransaction();
                                   rfiDlistAdapter.notifyItemChanged(i);
                                   break;
                               }
                           }
                       }
                   if (!isexists){
                       realm.beginTransaction();
                       RFIDList rfidList=new RFIDList();
                       rfidList.setState("未知");
                       rfidList.setEmlist(emList);
                       rfidList.setEpcid(decode);
                       mList.add(rfidList);
                       realm.insertOrUpdate(mList);
                       realm.commitTransaction();
                       rfiDlistAdapter.notifyDataSetChanged();
                   }
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRfidMgr = MyApplication.getInstance().rfidMgr;
        mReader = MyApplication.getInstance().mRfidReader;

        registerReceiver(broadcastReceiver,new IntentFilter(ACTION_HONEYWLL));
        filePath = getApplication().getExternalCacheDir().getPath()+"/export";
        mMyHandler = new MyHandler(this);

    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.comment_list;
    }
    private static class MyHandler extends Handler {
        private WeakReference ref;
        private MyHandler(RFIDListActivity act) {
            ref = new WeakReference<>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (ref.get() != null) {
                ((RFIDListActivity) ref.get()).handleMessage(msg);
            }
        }
    }
    public void handleMessage(Message msg) {
        switch (msg.what) {
        }
    }
    int readPower;
    @Override
    public void setListener() {
        rfiDlistAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {

                AlertDialog.Builder builder = new AlertDialog.Builder(RFIDListActivity.this);
                builder.setTitle(mList.get(position).getEpcid()+"处理");
                final String[] cities = {"盘亏","盘盈"};
                builder.setItems(cities, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        realm.beginTransaction();
                        mList.get(position).setState(cities[which]);
                        realm.insertOrUpdate(mList.get(position));
                        realm.commitTransaction();
                        rfiDlistAdapter.notifyItemChanged(position);
                    }
                });
                if (!mList.get(position).getState().equals("已盘")) {
                    builder.show();
                }
                return false;
            }
        });


        tv_right_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  EmList em= realm.copyFromRealm(emList);
                //new RFIDListActivity.exportfile().execute(em);
                mRfidMgr.addEventListener(mEventListener);
                mRfidMgr.setDevicePower(true);
                try {
                    Thread.sleep(500);//add this interval to avoid the poweroff op failed
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mRfidMgr.connect(null);
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mReader!=null) {
                    mReader.release();
                    mRfidMgr.disconnect();
                }
                mList=null;
                Intent intent = new Intent(RFIDListActivity.this, EMListActivity.class);
                startActivity(intent);
                CommonUtil.openNewActivityAnim(RFIDListActivity.this, true);
            }
        });
    }

    private AntennaPower[] getAntennaPower(){
        AntennaPower[] ap = new AntennaPower[1];

        SharedPreferences sp = getApplication().getSharedPreferences("SettingParam", Context.MODE_PRIVATE);
        ap[0] = new AntennaPower(1, sp.getInt("ReadPower", 3000),
                sp.getInt("WritePower", 3000));

        //ap[0] = new AntennaPower(1, SettingParam.AnteReadPower, SettingParam.AnteWritePower);
        return ap;
    }

    @Override
    public void initData() {
        task=getIntent().getStringExtra("task");
        Realm.init(getApplicationContext());
        realm=Realm.getDefaultInstance();
       // realm.setAutoRefresh(true);
        emList=realm.where(EmList.class).equalTo("id",task).findFirst();
        EmList em= realm.copyFromRealm(emList);
        mList=em.getRfidList();
        rfiDlistAdapter.setNewData(mList);
    }

    @Override
    public void initView() {
        super.initView();
        mMyApplication = MyApplication.getInstance();
        mRfidMgr = mMyApplication.rfidMgr;
        mRfidMgr.addEventListener(mEventListener);
        AntennaPower[] antennaPower=getAntennaPower();
        if (antennaPower.length>0){
           readPower= antennaPower[0].getReadPower();
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rfiDlistAdapter=new RFIDlistAdapter(R.layout.activity_rfid_list,mList);
        recyclerView.setAdapter(rfiDlistAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRfidMgr.addEventListener(mEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRfidMgr.removeEventListener(mEventListener);
    }

    @Override
    public void initTitle() {
        super.initTitle();
        tv_center_title.setVisibility(View.VISIBLE);
        tv_center_title.setText("盘点明细列表");

        iv_back.setVisibility(View.VISIBLE);

        tv_right_title.setVisibility(View.VISIBLE);
        tv_right_title.setText("导出");

        tv_all.setText("共3条");
        tv_count.setText("已盘2条");
        tv_unknown.setText("1条未知");
    }

    private EventListener mEventListener = new EventListener() {
        @Override
        public void onDeviceConnected(Object o) {
            mMyApplication.macAddress = (String) o;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG,"onDeviceConnected");
                    mRfidMgr.createReader();
                }
            });
        }

        @Override
        public void onDeviceDisconnected(Object o) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG,"onDeviceDisconnected");
                }
            });
        }

        @Override
        public void onReaderCreated(boolean b, RfidReader rfidReader) {
            mReader=rfidReader;
            Log.v(TAG,"onReaderCreated");
        }

        @Override
        public void onRfidTriggered(boolean b) {
            if (!b){
                if (mReader!=null&& mReader.available()) {
                    mReader.stopRead();
                    mReader.removeOnTagReadListener(dataListener);
                }
            }else {
                read();
            }
        }

        @Override
        public void onTriggerModeSwitched(TriggerMode triggerMode) {
        }

        private void read() {
            if (isReaderAvailable()) {
                mReader.setOnTagReadListener(dataListener);
                mReader.read(TagAdditionData.get("None"), new TagReadOption());
            }
        }

        private boolean isReaderAvailable() {
            return mReader != null && mReader.available();
        }
        private Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (msg.what == 0x11) {
                       Bundle bundle = msg.getData();         //从消息中获取bundle
                       String epc = bundle.getString("msg"); //从bundle中通过key获取对应信息
                     Log.v(TAG,"decode data:"+epc);
                        boolean isexists=false;
                        for (int i = 0; i < mList.size(); i++) {
                            if (mList.get(i).getEpcid().equals(epc) ) {
                                isexists=true;
                                if (mList.get(i).getState().equals("未盘")) {
                                    realm.beginTransaction();
                                    mList.get(i).setState("已盘");
                                    realm.insertOrUpdate(mList);
                                    realm.commitTransaction();
                                    rfiDlistAdapter.notifyItemChanged(i);
                                    break;
                                }
                            }
                        }
                        if (!isexists){
                            realm.beginTransaction();
                            RFIDList rfidList=new RFIDList();
                            rfidList.setState("未知");
                            rfidList.setEmlist(emList);
                            rfidList.setEpcid(epc);
                            mList.add(rfidList);
                            realm.insertOrUpdate(mList);
                            realm.commitTransaction();
                            rfiDlistAdapter.setNewData(mList);
                        }
                }
            }
        };
        private OnTagReadListener dataListener = new OnTagReadListener() {
            @Override
            public void onTagRead(final TagReadData[] t) {
                    for (TagReadData trd : t) {
                        String epc = trd.getEpcHexStr();
                        Message message = Message.obtain(); //消息实例
                        Bundle bundle = new Bundle();//bundle实例
                        bundle.putString("msg",epc);//给bundle添加信息
                        message.setData(bundle);// 将bundle存入message
                        message.what = 0x11;    //设置标记（自定义）
                        mHandler.sendMessage(message);	//发送消息
                    }
            }
        };
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mReader!=null) {
            mReader.release();
            mRfidMgr.disconnect();
        }
        Intent intent = new Intent(RFIDListActivity.this, EMListActivity.class);
        startActivity(intent);
        CommonUtil.openNewActivityAnim(RFIDListActivity.this, true);

    }

    private class exportfile extends AsyncTask<EmList, Void, Void> {
        @Override
        protected Void doInBackground(EmList... emLists) {
            EmList exportEm=emLists[0];
            try {
                boolean isexport=Transform.exportTxtfrom(filePath,exportEm.getName(),exportEm);
                if (isexport){
                    Log.v(TAG,"export success");
                }
            } catch (IOException e) {
                Log.v(TAG,"export failed");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog=   new ProgressDialog(RFIDListActivity.this);
            loadingDialog.setTitle("正在导出文本");
            loadingDialog.setCancelable(false);
            loadingDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loadingDialog.dismiss();
        }
    }

}