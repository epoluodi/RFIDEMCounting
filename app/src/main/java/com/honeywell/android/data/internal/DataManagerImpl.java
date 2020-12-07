package com.honeywell.android.data.internal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.honeywell.android.data.DataManager;
import com.honeywell.android.data.generate.DaoMaster;
import com.honeywell.android.data.generate.DaoSession;
import com.honeywell.android.data.generate.InventoryTaskDao;
import com.honeywell.android.data.generate.UserDao;
import com.honeywell.android.data.model.InventoryItem;
import com.honeywell.android.data.model.InventoryTask;
import com.honeywell.android.data.model.User;

import org.greenrobot.greendao.query.LazyList;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class DataManagerImpl extends DataManager {
    private static final String TAG = "DataManagerImpl";
    private static final String DatabaseName = "inventory.db";
    private DaoSession mDaoSession;
    private DaoMaster mDaoMaster;

    public DataManagerImpl(Context context, ExecutorService executor) {
        super(context,executor);
    }

    @Override
    public void startUp() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext,DatabaseName);
        SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
        mDaoMaster = new DaoMaster(sqLiteDatabase);
        mDaoSession = mDaoMaster.newSession();
    }

    @Override
    public void shutdown() {
        mDaoSession.clear();
    }

    @Override
    public void insertOrUpdateUser(final User user) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                LazyList<User> checkUserName = mDaoSession.getUserDao().queryBuilder().where(UserDao.Properties.UserName.eq(user.getUserName())).listLazy();
                if (checkUserName.size() > 0) {
                    Log.d(TAG, "insertUser: break");
                } else {
                    mDaoSession.getUserDao().insert(user);
                }
            }
        });
    }

    @Override
    public void insertOrUpdateInventoryTask(final List<InventoryTask> tasks) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDaoSession.getInventoryTaskDao().insertOrReplaceInTx(tasks);
                for (InventoryTask task:tasks) {
                    mDaoSession.getInventoryItemDao().insertInTx(task.inventoryList);
                }
            }
        });
    }

    @Override
    public void insertOrUpdateInventoryItem(final List<InventoryItem> items) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDaoSession.getInventoryItemDao().insertOrReplaceInTx(items);
            }
        });
    }

    @Override
    public void getInventoryTask(final User user,final OnDataListener listener) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final List<InventoryTask> tasks = mDaoSession.getInventoryTaskDao().queryBuilder().where(InventoryTaskDao.Properties.UserName.eq(user.getUserName())).list();
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onGetInventoryTask(tasks);
                    }
                });
            }
        });
    }

    @Override
    public void clearDatabase() {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DaoMaster.dropAllTables(mDaoMaster.getDatabase(),true);
                DaoMaster.createAllTables(mDaoMaster.getDatabase(),true);
            }
        });
    }

    @Override
    public void deleteTask(final InventoryTask task) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDaoSession.getInventoryTaskDao().delete(task);
                mDaoSession.getInventoryItemDao().deleteInTx(task.getInventoryList());
            }
        });
    }
}
