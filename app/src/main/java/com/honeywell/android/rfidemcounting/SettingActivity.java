package com.honeywell.android.rfidemcounting;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.honeywell.android.rfidemcounting.fragment.AntePowerFragment;
import com.honeywell.android.rfidemcounting.utils.CommonUtil;
import com.honeywell.rfidservice.EventListener;
import com.honeywell.rfidservice.RfidManager;
import com.honeywell.rfidservice.TriggerMode;
import com.honeywell.rfidservice.rfid.RfidReader;
import com.honeywell.rfidservice.rfid.TagAdditionData;
import com.honeywell.rfidservice.rfid.TagReadOption;

import butterknife.BindView;


public class SettingActivity extends BaseActivity{

    @BindView(R.id.btBtn)
    public Button btBtn;

    @BindView(R.id.btBtnExit)
    public Button btnExit;

//    @BindView(R.id.comBtn)
//    public Button comBtn;
    private RfidManager mRfidMgr;
//    private RfidReader mReader;
    private static String TAG="SettingActivity";
    private MyApplication mMyApplication;
    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView() {
        super.initView();
        tv_center_title.setVisibility(View.VISIBLE);
        tv_center_title.setText("设置");

        iv_back.setVisibility(View.VISIBLE);
        mRfidMgr = MyApplication.getInstance().rfidMgr;
//        mReader = MyApplication.getInstance().mRfidReader;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();   //开启Fragment事务
        transaction.add(R.id.ante_setting, new AntePowerFragment());    //将天线设置Fragment视图放置到FrameLayout布局中
        transaction.commit();       //Fragment调用生效

        if (mRfidMgr.isSerialDevice())
        {
            btBtn.setVisibility(View.INVISIBLE);
        }

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(1);
                finish();

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void setListener() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.exitActivityAndBackAnim(SettingActivity.this, true);
            }
        });

        btBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, BtActivity.class);
                startActivity(intent);
            }
        });

//        comBtn .setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mRfidMgr.addEventListener(mEventListener);
//                mRfidMgr.setDevicePower(true);
//                try {
//                    Thread.sleep(500);//add this interval to avoid the poweroff op failed
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                mRfidMgr.connect(null);
//            }
//        });
    }
//    private EventListener mEventListener = new EventListener() {
//        @Override
//        public void onDeviceConnected(Object o) {
//            mMyApplication.macAddress = (String) o;
//
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.i(TAG,"onDeviceConnected");
//                    mRfidMgr.createReader();
//                }
//            });
//        }
//
//        @Override
//        public void onDeviceDisconnected(Object o) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.i(TAG,"onDeviceDisconnected");
//
//                }
//            });
//        }
//
//        @Override
//        public void onReaderCreated(boolean b, RfidReader rfidReader) {
//            mReader=rfidReader;
//            Log.v(TAG,"onReaderCreated");
//        }
//
//        @Override
//        public void onRfidTriggered(boolean b) {
//                if (!b){
//                    mReader.stopRead();
//                }else {
//                    read();
//                }
//        }
//
//        @Override
//        public void onTriggerModeSwitched(TriggerMode triggerMode) {
//        }
//
//        private void read() {
//            if (isReaderAvailable()) {
//                mReader.read(TagAdditionData.get("None"), new TagReadOption());
//            }
//        }
//
//        private boolean isReaderAvailable() {
//            return mReader != null && mReader.available();
//        }
//    };



}
