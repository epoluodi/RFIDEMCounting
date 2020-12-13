package com.honeywell.android.rfidemcounting;

import android.app.Application;

import com.honeywell.rfidservice.RfidManager;
import com.honeywell.rfidservice.rfid.RfidReader;

public class MyApplication extends Application {
    private static MyApplication mInstance;
    public RfidManager rfidMgr;
    public RfidReader mRfidReader;
    public String macAddress;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        rfidMgr = RfidManager.getInstance(this);
    }

    public static MyApplication getInstance() {
        return mInstance;
    }
}
