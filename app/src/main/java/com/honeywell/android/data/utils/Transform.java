package com.honeywell.android.data.utils;

import com.google.gson.Gson;
import com.honeywell.android.data.model.TaskList;

public class Transform {
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
