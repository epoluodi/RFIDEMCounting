package com.honeywell.android.data.internal;

import android.content.Context;

import com.honeywell.android.data.DataManager;

import java.util.concurrent.ExecutorService;

public class DataManagerFactory {
    public static DataManager createMananger(Context context, ExecutorService executorService){
        return new DataManagerImpl(context,executorService);
    }
}
