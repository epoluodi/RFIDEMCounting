package com.honeywell.android.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.honeywell.android.data.model.InventoryItem;
import com.honeywell.android.data.model.InventoryTask;
import com.honeywell.android.data.model.User;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Data Manager, the one who really do the operations for data.
 */
public abstract class DataManager {
    protected Context mContext;
    protected ExecutorService mExecutor;
    protected Handler mMainHandler = new Handler(Looper.getMainLooper());
    protected DataManager(Context context,ExecutorService executor) {
       this.mContext = context;
       this.mExecutor = executor;
    }
    protected abstract void startUp();
    protected abstract void shutdown();

    /**
     * Insert or update the user.
     * @param user
     */
    public abstract void insertOrUpdateUser(User user);

    /**
     * Insert or update the inventory item.
     * @param items
     */
    public abstract void insertOrUpdateInventoryItem(List<InventoryItem> items);

    /**
     * Insert or update the inventory task.
     * @param tasks
     */
    public abstract void insertOrUpdateInventoryTask(List<InventoryTask> tasks);

    /**
     * Delete the task which you don't want to count.
     * @param task
     */
    public abstract void deleteTask(InventoryTask task);

    /**
     * Clear the database before you import the data to system.
     */
    public abstract void clearDatabase();

    /**
     * Get the Inventory task asynchronously.
     * @param user
     * @param listener
     */
    public abstract void getInventoryTask(User user,OnDataListener listener);

    public interface OnDataListener{
        void onGetInventoryTask(List<InventoryTask> tasks);
    }
}
