package com.honeywell.android.rfidemcounting;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.honeywell.android.data.DataEngine;
import com.honeywell.android.data.DataManager;
import com.honeywell.android.data.model.InventoryItem;
import com.honeywell.android.data.model.InventoryTask;
import com.honeywell.android.data.model.TaskList;
import com.honeywell.android.data.model.User;
import com.honeywell.android.data.utils.Transform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class MainTestActivity extends AppCompatActivity implements DataEngine.DataEngineCallback{

    private static final String TAG = "MainActivity";

    private DataEngine mDataEngine;
    private DataManager mDataManager;
    private TextView mText;

    String DataString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mText = findViewById(R.id.editTextTextPersonName);
        mDataEngine = DataEngine.getInstance(this);
        mDataEngine.startUp(this);
    }

    @Override
    public void onStartUp() {
        mDataManager = mDataEngine.getDataManager();
        InputStream is = getResources().openRawResource(R.raw.inventory);
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        StringBuffer buffer = new StringBuffer();
        String line = " ";
        try{
            while ((line = in.readLine()) != null){
                buffer.append(line);
            }
        }catch (Exception e){

        }finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Add User
        User user = new User();
        user.setUserName("Jack");
//        mDataManager.insertOrUpdateUser(user);
        String json = buffer.toString();


        InputStream txt = getResources().openRawResource(R.raw.inventorytxt);
        final TaskList taskList = Transform.importTxt("JackCai","Inventory",txt);
        try {
            txt.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "onCreate: -----"+taskList.toString());


//        final TaskList taskList = Transform.importData(json);
        //Import data
//        mDataManager.insertOrUpdateInventoryTask(taskList.getTasks());
        User myUser = new User();
        myUser.setUserName("Jack");
        //Query data.
        mDataManager.getInventoryTask(myUser, new DataManager.OnDataListener() {
            @Override
            public void onGetInventoryTask(List<InventoryTask> tasks) {
                Log.d(TAG, "onCreate: "+tasks.toString());
                mText.setText(tasks.toString());
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDataEngine.shutDown(this);
    }

    @Override
    public void onShutDown() {

    }
}