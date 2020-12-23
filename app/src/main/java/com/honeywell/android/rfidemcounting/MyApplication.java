package com.honeywell.android.rfidemcounting;

import android.app.Application;
import android.os.Environment;

import com.honeywell.android.data.model.User;
import com.honeywell.rfidservice.RfidManager;
import com.honeywell.rfidservice.rfid.RfidReader;

import java.io.File;

public class MyApplication extends Application {
    private static MyApplication mInstance;
    public RfidManager rfidMgr;
    public RfidReader mRfidReader;
    public String macAddress;
    public static User user;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        rfidMgr = RfidManager.getInstance(this);
        user=new User();

    }

    public static MyApplication getInstance() {
        return mInstance;
    }
}
