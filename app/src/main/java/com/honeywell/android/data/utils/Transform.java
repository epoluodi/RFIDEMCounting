package com.honeywell.android.data.utils;

import com.google.gson.Gson;
import com.honeywell.android.data.model.InventoryItem;
import com.honeywell.android.data.model.InventoryItemState;
import com.honeywell.android.data.model.InventoryTask;
import com.honeywell.android.data.model.InventoryTaskState;
import com.honeywell.android.data.model.TaskList;
import com.honeywell.android.rfidemcounting.bean.EmList;
import com.honeywell.android.rfidemcounting.bean.RFIDList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.RealmList;

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
                    item.setFailedReason("未盘");
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


    public static EmList importTxtToRealm(String userName, String taskName, InputStream stream){
         EmList emList=new EmList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        emList.setTime( sdf.format(new Date()));
        emList.setUsername(userName);
        emList.setName(taskName);
        emList.setState("未完成");
        RealmList<RFIDList> tasks = new RealmList<>();
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        StringBuffer buffer = new StringBuffer();
        String line = " ";
        try{
            while ((line = in.readLine()) != null){
                Thread.sleep(10);
                if(!line.startsWith("#")){
                    RFIDList rfid=new RFIDList();

                    rfid.setEmlist(emList);
                    rfid.setEpcid(line);
                    rfid.setState("未盘");
                    rfid.setName("name");
                    tasks.add(rfid);
                }
            }
            emList.setRfidList(tasks);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return emList;
    }

    public static boolean exportTxtfrom(String path, String taskName, EmList em) throws IOException {
        String filename = path + "/" + taskName + ".txt";
        String temp = "";

        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        FileOutputStream fos = null;
        PrintWriter pw = null;
        try {
            // 文件路径
            File file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();
            }
            // 将文件读入输入流
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            StringBuffer buf = new StringBuffer();
            buf = new StringBuffer("epcid state\n");
            // 保存该文件原有的内容
            for (int i = 0; i < em.getRfidList().size(); i++) {
                buf = buf.append(em.getRfidList().get(i).getEpcid() + " " + em.getRfidList().get(i).getState() + "\n");
            }


            fos = new FileOutputStream(file);
            pw = new PrintWriter(fos);
            pw.write(buf.toString().toCharArray());
            pw.flush();
        } catch (Exception e1) {
            e1.printStackTrace();
            return false;
        } finally {
            if (pw != null) {
                pw.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (br != null) {
                br.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (fis != null) {
                fis.close();
            }
        }

        return true;
    }
}
