package com.honeywell.android.data.utils;

import com.google.gson.Gson;
import com.honeywell.android.data.model.InventoryItem;
import com.honeywell.android.data.model.InventoryItemState;
import com.honeywell.android.data.model.InventoryTask;
import com.honeywell.android.data.model.InventoryTaskState;
import com.honeywell.android.data.model.TaskList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Transform {

    public static final TaskList importTxt(String userName,String taskName,InputStream stream){
        TaskList taskList = new TaskList();
        InventoryTask task = new InventoryTask();
        task.setTaskName(taskName);
        task.setUserName(userName);
        long taskId = System.currentTimeMillis();
        task.setTaskId(taskId);
        task.setInventoryState(InventoryTaskState.NOT_STARTED);
        task.inventoryList = new ArrayList<>();
        ArrayList<InventoryTask> tasks = new ArrayList<>();
        tasks.add(task);
        taskList.setTasks(tasks);
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        StringBuffer buffer = new StringBuffer();
        String line = " ";
        try{
            while ((line = in.readLine()) != null){
                Thread.sleep(10);
                if(!line.startsWith("#")){
                    InventoryItem item = new InventoryItem();
                    item.setInventoryTime(2020);
                    item.setItemID(System.currentTimeMillis());
                    item.setEpcID(line);
                    item.setInventoryTaskId(taskId);
                    item.setIsCounted(false);
                    item.setUserName(userName);
                    item.setFailedReason(InventoryItemState.UNKNOW);
                    task.inventoryList.add(item);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return taskList;
    }

    public static final TaskList importData(String json){
        Gson gson = new Gson();
        TaskList list = gson.fromJson(json, TaskList.class);
        return list;
    }

    public static final String exportData(TaskList list){
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
