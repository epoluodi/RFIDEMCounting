package com.honeywell.android.rfidemcounting;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.honeywell.android.data.DataEngine;
import com.honeywell.android.data.DataManager;
import com.honeywell.android.data.model.InventoryTask;
import com.honeywell.android.data.model.TaskList;
import com.honeywell.android.data.model.User;
import com.honeywell.android.data.utils.Transform;
import com.honeywell.android.rfidemcounting.adapter.EMlistAdapter;
import com.honeywell.android.rfidemcounting.bean.EmList;
import com.honeywell.android.rfidemcounting.utils.CommonUtil;
import com.honeywell.android.rfidemcounting.utils.ToastUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class EMListActivity extends BaseActivity implements DataEngine.DataEngineCallback{
    private static final String TAG = "EMListActivity";
    private DataEngine mDataEngine;
    private DataManager mDataManager;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    User hyh;
    private List<InventoryTask> mList = new ArrayList<>();
    private EMlistAdapter eMlistAdapter;
    @Override
    protected int attachLayoutRes() {
        return R.layout.comment_list;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataEngine = DataEngine.getInstance(this);
        mDataEngine.startUp(this);
    }

    @Override
    public void setListener() {
        eMlistAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(EMListActivity.this, RFIDListActivity.class);
                startActivity(intent);
                CommonUtil.openNewActivityAnim(EMListActivity.this, false);
            }
        });


        tv_right_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                importMissionList();
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.exitActivityAndBackAnim(EMListActivity.this, true);
            }
        });
    }
    /*方法：导入任务列表
    参数：无
    返回值：导入任务条数
    */
    private void importMissionList(){
        InputStream txt = getResources().openRawResource(R.raw.inventorytxt);
        final TaskList taskList = Transform.importTxt("HYH","Inventory2",txt);
        try {
            txt.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mDataManager.insertOrUpdateInventoryTask(taskList.getTasks());
        Log.d(TAG, "onCreate: -----"+taskList.toString());
        eMlistAdapter.addData(taskList.getTasks());
    }
    @Override
    public void initData() {
        super.initData();
    }


    @Override
    public void initView() {
        super.initView();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eMlistAdapter=new EMlistAdapter(R.layout.activity_em_list,mList);
        recyclerView.setAdapter(eMlistAdapter);

    }

    @Override
    public void initTitle() {
        super.initTitle();
        tv_center_title.setVisibility(View.VISIBLE);
        tv_center_title.setText("盘点任务列表");

        tv_right_title.setVisibility(View.VISIBLE);
        tv_right_title.setText("导入");

        iv_back.setVisibility(View.VISIBLE);
    }


    @Override
    public void onStartUp() {
        mDataManager = mDataEngine.getDataManager();
        mDataManager.clearDatabase();
        hyh=new User();
        hyh.setUserName("HYH");
        hyh.setPassword("123456");
        mDataManager.insertOrUpdateUser(hyh);

        InputStream txt = getResources().openRawResource(R.raw.inventorytxt);
        final TaskList taskList = Transform.importTxt("HYH","Inventory",txt);
        try {
            txt.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "onCreate: -----"+taskList.toString());


        final TaskList taskList1 = Transform.importTxt("HYH","Inventory1",txt);
        try {
            txt.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "onCreate: -----"+taskList.toString());
        mDataManager.insertOrUpdateInventoryTask(taskList.getTasks());
        mDataManager.insertOrUpdateInventoryTask(taskList1.getTasks());
        mDataManager.getInventoryTask(hyh, new DataManager.OnDataListener() {
            @Override
            public void onGetInventoryTask(List<InventoryTask> tasks) {
                Log.d(TAG, "onCreate: "+tasks.toString());

                eMlistAdapter.addData(tasks);
            }
        });

    }

    @Override
    public void onShutDown() {

    }
}