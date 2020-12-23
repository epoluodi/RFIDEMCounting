package com.honeywell.android.rfidemcounting;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Button mBtnConnect;

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

        tv_right_title.setVisibility(View.VISIBLE);
        tv_right_title.setText("搜索");

        mMyApplication = MyApplication.getInstance();
        mRfidMgr = mMyApplication.rfidMgr;


        mBtnConnect = findViewById(R.id.btn_connect);


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

        tv_right_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();


        //搜索开始通知

        registerReceiver(BluetoothAdapter.ACTION_DISCOVERY_STARTED, broadcastReceiver);
        //搜索完成通知
        registerReceiver(BluetoothAdapter.ACTION_DISCOVERY_FINISHED, broadcastReceiver);
        //蓝牙设备找到通知
        registerReceiver(BluetoothDevice.ACTION_FOUND, broadcastReceiver);


    }
    private void registerReceiver(String action, BroadcastReceiver receiver) {
        IntentFilter intentFilter = new IntentFilter(action);
        registerReceiver(receiver, intentFilter);
    }
    @Override
    protected void onPause() {
        super.onPause();
        stopScan();


        unregisterReceiver(broadcastReceiver);
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



    private void scan() {
        if (!requestPermissions()) {
            return;
        }

        mDevices.clear();
        mSelectedIdx = -1;
        mAdapter.notifyDataSetChanged();
//        mBluetoothAdapter.startLeScan(mLeScanCallback);

        mBluetoothAdapter.startDiscovery();

        mWaitDialog = ProgressDialog.show(this, null, "正在扫描蓝牙设备...");
        mWaitDialog.setCancelable(false);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScan();
            }
        }, 10 * 1000);
    }


    private BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                Log.e("----", "搜索开始");
                mDevices.clear();

            } else if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                Log.e("-----", "搜索完成");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stopScan();
                    }
                });



            } else if (intent.getAction().equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
                Log.e("配对请求", "配对请求");
            } else if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {

                BluetoothDevice mdevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                short rssi = intent.getExtras().getShort(
                        BluetoothDevice.EXTRA_RSSI);
                Log.e("----", "Discovery Found " + mdevice.getName() +
                        " " + mdevice.getAddress());

                if (mdevice.getName() != null &&
                        (mdevice.getName().contains("IH25") )) {
                    mDevices.add(new BtDeviceInfo(mdevice,rssi));


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }

            }
        }
    };
    private void stopScan() {
        mBluetoothAdapter.cancelDiscovery();
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

                    closeWaitDialog();
                    Toast.makeText(BtActivity.this, "读写器已连接", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

        @Override
        public void onDeviceDisconnected(Object o) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    closeWaitDialog();
                }
            });
        }

        @Override
        public void onReaderCreated(boolean b, RfidReader rfidReader) {

        }

        @Override
        public void onRfidTriggered(boolean b) {
        }

        @Override
        public void onTriggerModeSwitched(TriggerMode triggerMode) {
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

        }
    };
}
