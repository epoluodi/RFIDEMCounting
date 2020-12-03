package com.honeywell.android.data.model;

import com.honeywell.android.data.model.InventoryTask;

import java.util.ArrayList;

public class TaskList {
    private ArrayList<InventoryTask> tasks;

    public ArrayList<InventoryTask> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<InventoryTask> tasks) {
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "TaskList{" +
                "tasks=" + tasks +
                '}';
    }
}
