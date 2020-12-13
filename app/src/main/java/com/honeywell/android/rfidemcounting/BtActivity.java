package com.honeywell.android.rfidemcounting;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.honeywell.android.rfidemcounting.utils.CommonUtil;
import com.honeywell.rfidservice.EventListener;
import com.honeywell.rfidservice.RfidManager;
import com.honeywell.rfidservice.TriggerMode;
import com.honeywell.rfidservice.rfid.RfidReader;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

public class BtActivity extends BaseActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private String[] mPermissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION};
    private List<String> mRequestPermissions = new ArrayList<>();

    private MyApplication mMyApplication;
    private RfidManager mRfidMgr;
    private BluetoothAdapter mBluetoothAdapter;

    private Handler mHandler = new Handler();
    private ProgressDialog mWaitDialog;
    private TextView mTvInfo;
    private Button mBtnConnect;
    private Button mBtnCreateReader;
    private ListView mLv;
    private MyAdapter mAdapter;
    private List<BtDeviceInfo> mDevices = new ArrayList();
    private int mSelectedIdx = -1;

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_bt;
    }

    @Override
    public void initView() {
        super.initView();
        tv_center_title.setVisibility(View.VISIBLE);
        tv_center_title.setText("蓝牙连接");

        iv_back.setVisibility(View.VISIBLE);

        iv_right_title.setVisibility(View.VISIBLE);
        iv_right_title.setImageResource(R.mipmap.l1);

        mMyApplication = MyApplication.getInstance();
        mRfidMgr = mMyApplication.rfidMgr;

        mTvInfo = findViewById(R.id.tv_info);
        mBtnConnect = findViewById(R.id.btn_connect);
        mBtnCreateReader = findViewById(R.id.btn_create_reader);
        showBtn();
        mLv = findViewById(R.id.lv);
        mAdapter = new MyAdapter(this, mDevices);
        mLv.setAdapter(mAdapter);
        mLv.setOnItemClickListener(mItemClickListener);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        requestPermissions();
    }

    @Override
    public void setListener() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.exitActivityAndBackAnim(BtActivity.this, true);
            }
        });

        iv_right_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRfidMgr.addEventListener(mEventListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScan();
        mHandler.removeCallbacksAndMessages(null);
        mRfidMgr.removeEventListener(mEventListener);
    }

    private boolean requestPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            for (int i = 0; i < mPermissions.length; i++) {
                if (ContextCompat.checkSelfPermission(this, mPermissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    mRequestPermissions.add(mPermissions[i]);
                }
            }

            if (mRequestPermissions.size() > 0) {
                ActivityCompat.requestPermissions(this, mPermissions, PERMISSION_REQUEST_CODE);
                return false;
            }
        }

        return true;
    }

    private void showBtn() {
        mTvInfo.setTextColor(Color.rgb(128, 128, 128));

        if (isConnected()) {
            mTvInfo.setText(mMyApplication.macAddress + " 已连接！");
            mTvInfo.setTextColor(Color.rgb(0, 128, 0));
            mBtnConnect.setEnabled(true);
            mBtnConnect.setText("断开连接");
            mBtnCreateReader.setEnabled(true);
        } else {
            mTvInfo.setText("RFID设备未连接！");
            mBtnConnect.setEnabled(mSelectedIdx != -1);
            mBtnConnect.setText("连接");
            mBtnCreateReader.setEnabled(false);
        }
    }

    private boolean isConnected() {
        if (mRfidMgr.isConnected()) {
            return true;
        }

        return false;
    }

    public void clickBtnConn(View v) {
        if (isConnected()) {
            disconnect();
        } else {
            connect();
        }
    }

    public void clickBtnCreateReader(View view) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRfidMgr.createReader();
            }
        }, 1000);

        mWaitDialog = ProgressDialog.show(this, null, "正在创建读写器...");
    }

    private void scan() {
        if (!requestPermissions()) {
            return;
        }

        mDevices.clear();
        mSelectedIdx = -1;
        mAdapter.notifyDataSetChanged();
        mBluetoothAdapter.startLeScan(mLeScanCallback);

        mWaitDialog = ProgressDialog.show(this, null, "正在扫描蓝牙设备...");
        mWaitDialog.setCancelable(false);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScan();
            }
        }, 5 * 1000);
    }

    private void stopScan() {
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        closeWaitDialog();
    }

    private void connect() {
        if (mSelectedIdx == -1 || mSelectedIdx >= mDevices.size()) {
            return;
        }

        mRfidMgr.addEventListener(mEventListener);
        mRfidMgr.connect(mDevices.get(mSelectedIdx).dev.getAddress());
        mWaitDialog = ProgressDialog.show(this, null, "正在连接...");
    }

    private void disconnect() {
        mRfidMgr.disconnect();
    }

    private void closeWaitDialog() {
        if (mWaitDialog != null) {
            mWaitDialog.dismiss();
            mWaitDialog = null;
        }
    }

    private EventListener mEventListener = new EventListener() {
        @Override
        public void onDeviceConnected(Object o) {
            mMyApplication.macAddress = (String) o;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showBtn();
                    closeWaitDialog();
                }
            });
        }

        @Override
        public void onDeviceDisconnected(Object o) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showBtn();
                    closeWaitDialog();
                }
            });
        }

        @Override
        public void onReaderCreated(boolean b, RfidReader rfidReader) {
            MyApplication.getInstance().mRfidReader = rfidReader;
            Toast.makeText(BtActivity.this, "读写器已连接", Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void onRfidTriggered(boolean b) {
        }

        @Override
        public void onTriggerModeSwitched(TriggerMode triggerMode) {
        }
    };

    private long mPrevListUpdateTime;
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (device.getName() != null && !device.getName().isEmpty()) {
                synchronized (mDevices) {
                    boolean newDevice = true;

                    for (BtDeviceInfo info : mDevices) {
                        if (device.getAddress().equals(info.dev.getAddress())) {
                            newDevice = false;
                            info.rssi = rssi;
                        }
                    }

                    if (newDevice) {
                        mDevices.add(new BtDeviceInfo(device, rssi));
                    }

                    long cur = System.currentTimeMillis();

                    if (newDevice || cur - mPrevListUpdateTime > 500) {
                        mPrevListUpdateTime = cur;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        }
    };

    private class BtDeviceInfo {
        BluetoothDevice dev;
        int rssi;

        private BtDeviceInfo(BluetoothDevice dev, int rssi) {
            this.dev = dev;
            this.rssi = rssi;
        }
    }

    private class MyAdapter extends ArrayAdapter {
        private Context ctx;

        public MyAdapter(Context context, List ls) {
            super(context, 0, ls);
            ctx = context;
        }

        public View getView(int position, @Nullable View v, @NonNull ViewGroup parent) {
            ViewHolder vh;

            if (v == null) {
                LayoutInflater inflater = LayoutInflater.from(ctx);
                v = inflater.inflate(R.layout.list_bt_device, null);
                vh = new ViewHolder();
                vh.tvName = v.findViewById(R.id.tvName);
                vh.tvAddr = v.findViewById(R.id.tvAddr);
                vh.tvRssi = v.findViewById(R.id.tvRssi);
                v.setTag(vh);
            } else {
                vh = (ViewHolder) v.getTag();
            }

            BtDeviceInfo item = mDevices.get(position);
            vh.tvName.setText(item.dev.getName());
            vh.tvAddr.setText(item.dev.getAddress());
            vh.tvRssi.setText(String.valueOf(item.rssi));

            if (position == mSelectedIdx) {
                v.setBackgroundColor(Color.rgb(220, 220, 220));
            } else {
                v.setBackgroundColor(Color.argb(0, 0, 0, 0));
            }

            return v;
        }

        class ViewHolder {
            TextView tvName;
            TextView tvAddr;
            TextView tvRssi;
        }
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            mSelectedIdx = i;
            mAdapter.notifyDataSetChanged();
            showBtn();
        }
    };
}
