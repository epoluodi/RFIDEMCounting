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
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import io.realm.Realm;

public class InitRFIDActivity extends BaseActivity {
    private static final String TAG = "RFIDListActivity";
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    private  List<RFIDList> mList;
    private RFIDlistAdapter rfiDlistAdapter;
    private String task;
    private EmBean emList;
    private static EmBean em;
    private RfidManager mRfidMgr;
    private MyHandler mMyHandler;
    private MyApplication mMyApplication;
    private RfidReader mReader;
    private final static String ACTION_HONEYWLL = "com.honeywell";
    private String filePath;
    private Dialog loadingDialog;
    private EmBean emBean;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   /* private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
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
*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRfidMgr = MyApplication.getInstance().rfidMgr;
        mReader = MyApplication.getInstance().mRfidReader;

        filePath = Environment.getExternalStorageDirectory()+ "/import";
        mMyHandler = new MyHandler(this);


        if (!mRfidMgr.isSerialDevice()) {

            if (!mRfidMgr.isConnected()) {
                Toast.makeText(InitRFIDActivity.this,
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
                                loadingDialog = new ProgressDialog(InitRFIDActivity.this);
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
                                    Toast.makeText(InitRFIDActivity.this,
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
                                Toast.makeText(InitRFIDActivity.this,
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
                            loadingDialog = new ProgressDialog(InitRFIDActivity.this);
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
                            Toast.makeText(InitRFIDActivity.this,
                                    "RFID连接", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(InitRFIDActivity.this,
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
        return R.layout.init_comment_list;
    }

    private static class MyHandler extends Handler {
        private WeakReference ref;

        private MyHandler(InitRFIDActivity act) {
            ref = new WeakReference<>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (ref.get() != null) {
                ((InitRFIDActivity) ref.get()).handleMessage(msg);
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

        tv_right_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final androidx.appcompat.app.AlertDialog.Builder normalDialog = new androidx.appcompat.app.AlertDialog.Builder(InitRFIDActivity.this);
                normalDialog.setCancelable(false);
                normalDialog.setTitle("完成");
                normalDialog.setMessage("确认导出初始数据吗？");
                normalDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new InitRFIDActivity.exportfile().execute();
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

            CommonUtil.exitActivityAndBackAnim(InitRFIDActivity.this, true);
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
        emBean=new EmBean();
        emBean.setName("template"+sdf.format(new Date()));

        mList = new ArrayList<>();
       // rfiDlistAdapter.notifyDataSetChanged();

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


      //  unregisterReceiver(broadcastReceiver);
        mRfidMgr.removeEventListener(mEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //registerReceiver(broadcastReceiver, new IntentFilter(ACTION_HONEYWLL));
        mRfidMgr.addEventListener(mEventListener);

    }

    @Override
    public void initTitle() {
        super.initTitle();
        tv_center_title.setVisibility(View.VISIBLE);
        tv_center_title.setText("初始化盘点数据列表");

        iv_back.setVisibility(View.VISIBLE);

        tv_right_title.setVisibility(View.VISIBLE);
        tv_right_title.setText("导出");


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
                                break;
                            }
                        }
                    if (!isexists) {
                        RFIDList rfidList = new RFIDList();
                        rfidList.setEpcid(epc);
                        mList.add(rfidList);
                        rfiDlistAdapter.addData(rfidList);
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
        Intent intent = new Intent(InitRFIDActivity.this, EMListActivity.class);
        startActivity(intent);
        CommonUtil.openNewActivityAnim(InitRFIDActivity.this, true);

    }

    private class exportfile extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void...voids) {

            try {
               boolean transform= Transform.exportInitTxt(filePath,mList);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = new ProgressDialog(InitRFIDActivity.this);
            loadingDialog.setTitle("正在导出文本");
            loadingDialog.setCancelable(false);
            loadingDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loadingDialog.dismiss();
            Toast.makeText(getApplicationContext(),"导出完成",Toast.LENGTH_SHORT);
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

            CommonUtil.exitActivityAndBackAnim(InitRFIDActivity.this, true);

        }
    }

}