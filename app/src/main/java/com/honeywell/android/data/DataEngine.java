package com.honeywell.android.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.honeywell.android.data.internal.DataManagerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Entry point to do the data operation.
 * <br>1. Call {@linkplain DataEngine#getInstance(Context)} first.
 * <br>2. Call {@linkplain DataEngine#startUp(DataEngineCallback)} to start up the engine before do any operation.
 * <br>3. Call {@linkplain DataEngine#getInstance(Context)} to obtain the DataManager instance.
 * <br>4. If you don't want to do any operation any more ,please do call the {@linkplain DataEngine#shutDown(DataEngineCallback)} to release resource.
 */
public class DataEngine {

    private static DataEngine sDataEngine;

    private DataManager mDataManager;

    private ExecutorService mExecutor;

    private Context mContext;

    private boolean mHasStartedUp = false;

    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    private DataEngine(Context context){
        mContext = context.getApplicationContext();
    }

    public static DataEngine getInstance(Context context){
        if(sDataEngine==null){
            sDataEngine = new DataEngine(context);
        }
        return sDataEngine;
    }

    public void startUp(final DataEngineCallback callback){
        if(mHasStartedUp){
            callback.onStartUp();
            return;
        }
        mExecutor = Executors.newSingleThreadExecutor();
        mDataManager = DataManagerFactory.createMananger(mContext,mExecutor);
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDataManager.startUp();
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(callback!=null){
                            callback.onStartUp();
                            mHasStartedUp = true;
                        }
                    }
                });
            }
        });

    }

    public void shutDown(DataEngineCallback callback){
        mDataManager.shutdown();
        mExecutor.shutdown();
        mExecutor = null;
        mDataManager = null;
        if(callback!=null){
            callback.onShutDown();
            mHasStartedUp = false;
        }
    }

    public DataManager getDataManager(){
        return mDataManager;
    }

    public interface DataEngineCallback{
        void onStartUp();
        void onShutDown();
    }

}
