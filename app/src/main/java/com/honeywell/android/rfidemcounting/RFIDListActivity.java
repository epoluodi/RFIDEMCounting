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
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dou361.dialogui.DialogUIUtils;
import com.honeywell.android.data.utils.Transform;
import com.honeywell.android.rfidemcounting.adapter.RFIDlistAdapter;
import com.honeywell.android.rfidemcounting.bean.EmBean;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import io.realm.Realm;

public class RFIDListActivity extends BaseActivity {
    private static final String TAG = "RFIDListActivity";
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    private static List<RFIDList> mList;
    private RFIDlistAdapter rfiDlistAdapter;
    private String task;
    private EmBean emList;
    private static EmBean em;
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
    private final static String ACTION_HONEYWLL = "com.honeywell";
    private String filePath;
    private int precount=0;
    private int unknown=0;
    private int counted=0;
    private Realm realm;
    private Dialog loadingDialog;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        //  public static final String TAG = "MyBroadcastReceiver";

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_HONEYWLL)) {
                if (intent.hasExtra("data")) {
                    final String decode = intent.getStringExtra("data");
                    Log.v(TAG, decode);
                    boolean isexists = false;
                    for (int i = 0; i < mList.size(); i++) {
                        if (mList.get(i).getEpcid().equals(decode)) {
                            isexists = true;
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
                    if (!isexists) {
                        realm.beginTransaction();
                        RFIDList rfidList = new RFIDList();
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

        filePath = getApplication().getExternalCacheDir().getPath() + "/export";
        mMyHandler = new MyHandler(this);


        if (!mRfidMgr.isSerialDevice()) {

            if (!mRfidMgr.isConnected()) {
                Toast.makeText(RFIDListActivity.this,
                        "请进入设置，选择IH25设备进行连接", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500);//add this interval to avoid the poweroff op failed
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingDialog = new ProgressDialog(RFIDListActivity.this);
                                loadingDialog.setTitle("正在连接RFID读写器");
                                loadingDialog.setCancelable(false);
                                loadingDialog.show();
                            }
                        });

                        mRfidMgr.addEventListener(mEventListener);
                        mRfidMgr.setDevicePower(true);
                        mRfidMgr.setTriggerMode(TriggerMode.RFID);
                        try {
                            mRfidMgr.createReader();
                            try {
                                Thread.sleep(1000);//add this interval to avoid the poweroff op failed
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RFIDListActivity.this,
                                            "RFID连接", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RFIDListActivity.this,
                                        "RFID连接失败", Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();
                            }
                        });
                    }
                }
            }).start();

            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);//add this interval to avoid the poweroff op failed
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingDialog = new ProgressDialog(RFIDListActivity.this);
                            loadingDialog.setTitle("正在连接RFID读写器");
                            loadingDialog.setCancelable(false);
                            loadingDialog.show();
                        }
                    });
                    try {
                        Thread.sleep(2000);//add this interval to avoid the poweroff op failed
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mRfidMgr.addEventListener(mEventListener);
                    mRfidMgr.setDevicePower(true);
                    mRfidMgr.setTriggerMode(TriggerMode.RFID);
                    try {
                        Thread.sleep(1000);//add this interval to avoid the poweroff op failed
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mRfidMgr.connect(null);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RFIDListActivity.this,
                                    "RFID连接", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RFIDListActivity.this,
                                    "RFID连接失败", Toast.LENGTH_SHORT).show();
                            loadingDialog.dismiss();
                        }
                    });
                }
            }
        }).start();
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

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyCode == 4)
            onClickListenerback.onClick(iv_back);
        return true;

    }

    int readPower;

    @Override
    public void setListener() {
        rfiDlistAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                if (!mList.get(position).getState().equals("已盘")) {
                    String type;
                    if (mList.get(position).getState().equals("未知") ||mList.get(position).getState().equals("盘盈")){
                        type="盘盈";
                    }else {
                        type="盘亏";
                    }
                    final EditText inputServer = new EditText(RFIDListActivity.this);
                    AlertDialog.Builder builder = new AlertDialog.Builder(RFIDListActivity.this);
                    builder.setCancelable(false);//
                    builder.setTitle(type+"原因").setView(inputServer)
                            .setNegativeButton("取消", null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String reason = inputServer.getText().toString();
                            //Log.v(TAG,reason);
                            realm.beginTransaction();
                            mList.get(position).setState(type);
                            mList.get(position).setEmname(MyApplication.user.getUserName());
                            mList.get(position).setEmtime(sdf.format(new Date()));
                            mList.get(position).setReason(reason);
                            mList.get(position).setIsem(true);
                            realm.insertOrUpdate(mList);
                            realm.commitTransaction();
                            rfiDlistAdapter.notifyItemChanged(position);
                            setTop();

                          /*  if (mList.get(position).getState().equals("盘盈")){
                                unknown--;
                                counted++;
                            }else {
                                counted++;
                            }
                           // tv_all.setText("总条数:"+String.valueOf(precount));
                            tv_count.setText("已盘:"+String.valueOf(counted));
                            tv_unknown.setText("未知:"+String.valueOf(unknown));*/
                        }
                    });
                    builder.show();
                }
                return false;
            }
        });


        tv_right_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final androidx.appcompat.app.AlertDialog.Builder normalDialog = new androidx.appcompat.app.AlertDialog.Builder(RFIDListActivity.this);
                normalDialog.setCancelable(false);
                normalDialog.setTitle("完成");
                normalDialog.setMessage("确认完成盘点任务吗？");
                normalDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                realm.beginTransaction();
                                em.setState("已完成");
                                realm.insertOrUpdate(em);
                                realm.commitTransaction();
                                Intent intent = new Intent(RFIDListActivity.this, ExportEmActivity.class);
                                startActivity(intent);
                                CommonUtil.openNewActivityAnim(RFIDListActivity.this, true);
                            }
                        });
                normalDialog.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                normalDialog.show();

            }
        });

        iv_back.setOnClickListener(onClickListenerback);
    }

    private View.OnClickListener onClickListenerback=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mReader != null) {
                if (!mRfidMgr.isSerialDevice()) {
                    if (mRfidMgr.isConnected()){
                        mReader.release();
                        Log.e("RFID设备","IH25 释放");
                    }


                } else {
                    mRfidMgr.disconnect();
                    Log.e("RFID设备","读写器 释放");
                }
            }
            mList = null;
            Intent intent = new Intent(RFIDListActivity.this, EMListActivity.class);
            startActivity(intent);
            CommonUtil.openNewActivityAnim(RFIDListActivity.this, true);
        }
    };


    private AntennaPower[] getAntennaPower() {
        AntennaPower[] ap = new AntennaPower[1];

        SharedPreferences sp = getApplication().getSharedPreferences("SettingParam", Context.MODE_PRIVATE);
        ap[0] = new AntennaPower(1, sp.getInt("ReadPower", 3000),
                sp.getInt("WritePower", 3000));

        //ap[0] = new AntennaPower(1, SettingParam.AnteReadPower, SettingParam.AnteWritePower);
        return ap;
    }

    @Override
    public void initData() {
        task = getIntent().getStringExtra("task");
        Realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();
        emList = realm.where(EmBean.class).equalTo("id", task).findFirst();
        em = realm.copyFromRealm(emList);
        mList = em.getRfidList();
       setTop();
        rfiDlistAdapter.addData(mList);

    }
    private void setTop(){
        precount=0;
        unknown=0;
        counted=0;
        for (int i=0;i<mList.size();i++){
            precount++;
            if (mList.get(i).getState().equals("已盘")||mList.get(i).getState().equals("盘盈")||mList.get(i).getState().equals("盘亏")){
                counted++;
            }else if (mList.get(i).getState().equals("未知")){
                unknown++;
            }
        }
        tv_all.setText("总条数:"+String.valueOf(precount));
        tv_count.setText("已盘:"+String.valueOf(counted));
        tv_unknown.setText("未知:"+String.valueOf(unknown));
      //  rfiDlistAdapter.addData(mList);
    }
    @Override
    public void initView() {
        super.initView();
        mMyApplication = MyApplication.getInstance();
        mRfidMgr = mMyApplication.rfidMgr;
        mRfidMgr.addEventListener(mEventListener);

        AntennaPower[] antennaPower = getAntennaPower();
        if (antennaPower.length > 0) {
            readPower = antennaPower[0].getReadPower();
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rfiDlistAdapter = new RFIDlistAdapter(R.layout.activity_rfid_list, mList);
        rfiDlistAdapter.setHasStableIds(true);
        recyclerView.setAdapter(rfiDlistAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();


        unregisterReceiver(broadcastReceiver);
        mRfidMgr.removeEventListener(mEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(ACTION_HONEYWLL));
        mRfidMgr.addEventListener(mEventListener);

    }

    @Override
    public void initTitle() {
        super.initTitle();
        tv_center_title.setVisibility(View.VISIBLE);
        tv_center_title.setText("盘点明细列表");

        iv_back.setVisibility(View.VISIBLE);

        tv_right_title.setVisibility(View.VISIBLE);
        tv_right_title.setText("完成");


    }

    private EventListener mEventListener = new EventListener() {
        @Override
        public void onDeviceConnected(Object o) {
            mMyApplication.macAddress = (String) o;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "onDeviceConnected");
                    mRfidMgr.createReader();

                }
            });
        }

        @Override
        public void onDeviceDisconnected(Object o) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "onDeviceDisconnected");
                }
            });
        }

        @Override
        public void onReaderCreated(boolean b, RfidReader rfidReader) {
            mReader = rfidReader;
            Log.v(TAG, "onReaderCreated");
            AntennaPower[] ap = new AntennaPower[1];

            SharedPreferences sp = getSharedPreferences("SettingParam", Context.MODE_PRIVATE);
            ap[0] = new AntennaPower(1, sp.getInt("ReadPower", 3000),
                    sp.getInt("WritePower", 3000));
            try {
                mReader.setAntennaPower(ap);
            } catch (Exception e) {
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (loadingDialog != null)
                        loadingDialog.dismiss();
                }
            });
        }

        @Override
        public void onRfidTriggered(boolean b) {
            if (!b) {
                if (mReader != null && mReader.available()) {
                    mReader.stopRead();
                    mReader.removeOnTagReadListener(dataListener);
                }
            } else {
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
                    Log.v(TAG, "decode data:" + epc);
                    boolean isexists = false;
                    for (int i = 0; i < mList.size(); i++) {
                        if (mList.get(i).getEpcid().equals(epc)) {
                            isexists = true;
                            if (mList.get(i).getState().equals("未盘")) {
                                realm.beginTransaction();
                                mList.get(i).setState("已盘");
                                mList.get(i).setEmname(MyApplication.user.getUserName());
                                mList.get(i).setEmtime(sdf.format(new Date()));
                                mList.get(i).setIsem(true);
                                realm.insertOrUpdate(mList);
                                realm.commitTransaction();
                                rfiDlistAdapter.notifyItemChanged(i);
                             /*  counted++;
                                // tv_all.setText("总条数:"+String.valueOf(precount));
                                tv_count.setText("已盘:"+String.valueOf(counted));
                               // tv_unknown.setText("未知:"+String.valueOf(unknown));*/
                                setTop();
                                break;
                            }
                        }
                    }
                    if (!isexists) {
                        realm.beginTransaction();
                        RFIDList rfidList = new RFIDList();
                        rfidList.setState("未知");
                        rfidList.setEmlist(emList);
                        rfidList.setEpcid(epc);
                        rfidList.setEmname(MyApplication.user.getUserName());
                        rfidList.setEmtime(sdf.format(new Date()));
                        rfidList.setReason("无");
                        mList.add(rfidList);
                        realm.insertOrUpdate(mList);
                        realm.commitTransaction();
                        rfiDlistAdapter.addData(rfidList);
                       /* precount++;
                        unknown++;
                         tv_all.setText("总条数:"+String.valueOf(precount));
                        //tv_count.setText("已盘:"+String.valueOf(counted));
                         tv_unknown.setText("未知:"+String.valueOf(unknown));*/
                        setTop();
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
                    bundle.putString("msg", epc);//给bundle添加信息
                    message.setData(bundle);// 将bundle存入message
                    message.what = 0x11;    //设置标记（自定义）
                    mHandler.sendMessage(message);    //发送消息
                }
            }
        };
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mReader != null) {
            mReader.release();
            mRfidMgr.disconnect();
        }
        Intent intent = new Intent(RFIDListActivity.this, EMListActivity.class);
        startActivity(intent);
        CommonUtil.openNewActivityAnim(RFIDListActivity.this, true);

    }

    private class exportfile extends AsyncTask<EmBean, Void, Void> {
        @Override
        protected Void doInBackground(EmBean... emLists) {
            EmBean exportEm = emLists[0];
           /* try {
               // boolean isexport = Transform.exportTxtfrom(filePath, exportEm.getName(), exportEm.getId());
                *//*if (isexport) {
                    Log.v(TAG, "export success");
                }*//*
            } catch (IOException e) {
                Log.v(TAG, "export failed");
                e.printStackTrace();
            }*/
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = new ProgressDialog(RFIDListActivity.this);
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