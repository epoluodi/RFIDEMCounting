package com.honeywell.android.data.utils;

import com.google.gson.Gson;
import com.honeywell.android.data.model.InventoryItem;
import com.honeywell.android.data.model.InventoryTask;
import com.honeywell.android.data.model.InventoryTaskState;
import com.honeywell.android.data.model.TaskList;
import com.honeywell.android.rfidemcounting.bean.EmBean;
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
import java.util.List;

import io.realm.Realm;
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


    public static EmBean importTxtToRealm(String userName, String taskName, InputStream stream){
         EmBean emList=new EmBean();
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
                    rfid.setReason("无");
                    rfid.setEmlist(emList);
                    rfid.setEpcid(line);
                    rfid.setState("未盘");
                    rfid.setName("name");
                    rfid.setEmname(userName);
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

    public static boolean exportTxtfrom(String path, List<String> ids) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int j=0;j<ids.size();j++) {
            Realm.init(Realm.getApplicationContext());
            Realm realm = Realm.getDefaultInstance();
            EmBean emBean = realm.where(EmBean.class).equalTo("id", ids.get(j)).findFirst();

            String filename = path + "/" + emBean.getName() +sdf.format(new Date())+ ".txt";
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
                buf = new StringBuffer("epcid,state,name,reason,time\n");
                // 保存该文件原有的内容
                for (int i = 0; i < emBean.getRfidList().size(); i++) {
                    buf = buf.append(emBean.getRfidList().get(i).getEpcid() + ","
                            + emBean.getRfidList().get(i).getState() + ","+
                                    emBean.getRfidList().get(i).getEmname() + ","+
                                    emBean.getRfidList().get(i).getReason()+","+
                                    emBean.getRfidList().get(i).getEmtime()+"\n"
                            );
                }


                fos = new FileOutputStream(file);
                pw = new PrintWriter(fos);
                pw.write(buf.toString().toCharArray());
                pw.flush();
                realm.beginTransaction();;
                emBean.setState("已导出");
                realm.commitTransaction();
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
        }
        return true;
    }

    public static boolean exportInitTxt(String path,List<RFIDList> emBean) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


            String filename = path + "/template"  +sdf.format(new Date())+ ".txt";
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
                buf = new StringBuffer("#epcid\n");
                // 保存该文件原有的内容
                for (int i = 0; i < emBean.size(); i++) {
                    buf = buf.append(emBean.get(i).getEpcid() +"\n"
                    );
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
