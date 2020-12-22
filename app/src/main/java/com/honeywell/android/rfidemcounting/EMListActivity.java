package com.honeywell.android.rfidemcounting;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.honeywell.android.rfidemcounting.utils.DialogUtil;
import com.honeywell.rfidservice.EventListener;
import com.honeywell.rfidservice.RfidManager;
import com.honeywell.rfidservice.TriggerMode;
import com.honeywell.rfidservice.rfid.RfidReader;
import com.honeywell.rfidservice.rfid.TagAdditionData;
import com.honeywell.rfidservice.rfid.TagReadOption;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.suke.widget.SwitchButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class EMListActivity extends BaseActivity {
    private static final String TAG = "EMListActivity";
    private static int REQUESTCODE_FROM_ACTIVITY = 1000;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    Dialog loadingDialog;
    private SwitchButton unfinishied;
    private SwitchButton finished;
    private Realm realm;
    User hyh;
    private List<EmList> mList ;
    private EMlistAdapter eMlistAdapter;
    @Override
    protected int attachLayoutRes() {
        return R.layout.comment_list_rfid;
    }
    private static class MyHandler extends Handler {
        private WeakReference ref;

        private MyHandler(EMListActivity act) {
            ref = new WeakReference<>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (ref.get() != null) {
                ((EMListActivity) ref.get()).handleMessage(msg);
            }
        }
    }
    public void handleMessage(Message msg) {
        switch (msg.what) {
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


        @Override
    public void setListener() {
        eMlistAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                    Intent intent = new Intent(EMListActivity.this, RFIDListActivity.class);
                    intent.putExtra("task",  mList.get(position).getId());
                    startActivity(intent);
                    CommonUtil.openNewActivityAnim(EMListActivity.this, false);

            }
        });

        eMlistAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                final androidx.appcompat.app.AlertDialog.Builder normalDialog = new AlertDialog.Builder(EMListActivity.this);
                normalDialog.setCancelable(false);
                normalDialog.setTitle("删除");
                normalDialog.setMessage("确认删除任务吗？");
                normalDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                    realm.beginTransaction();
                                    mList.get(position).deleteFromRealm();
                                    realm.commitTransaction();
                                    eMlistAdapter.notifyDataSetChanged();

                            }
                        });
                normalDialog.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                normalDialog.show();

                return false;
            }

        });
        tv_right_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String filePath = getApplication().getExternalCacheDir().getPath()+"/import";

                new LFilePicker()
                        .withActivity(EMListActivity.this)
                        .withRequestCode(REQUESTCODE_FROM_ACTIVITY)
                        .withStartPath(filePath)
                        .withFileFilter( new String[]{".txt"})
                        .start();
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EMListActivity.this, MainActivity.class);
                startActivity(intent);
                CommonUtil.openNewActivityAnim(EMListActivity.this, true);
            }
        });

        unfinishied.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                Toast.makeText(getApplicationContext(), "开关被单击", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /*方法：导入任务列表
    参数：无
    返回值：导入任务条数
    */
    int i=1;
    private void importMissionList(){
        InputStream txt = getResources().openRawResource(R.raw.inventorytxt);

        final EmList emList = Transform.importTxtToRealm("HYH","Inventory"+String.valueOf(i++),txt);
        try {
            txt.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        realm.beginTransaction();
        realm.insert(emList);
        realm.commitTransaction();

        eMlistAdapter.notifyDataSetChanged();
    }
    @Override
    public void initData() {
        super.initData();
        Realm.init(getApplicationContext());
        realm=Realm.getDefaultInstance();
        realm.refresh();

        mList=realm.where(EmList.class).findAll().sort("time",Sort.ASCENDING);
        eMlistAdapter.setNewData(mList);
    }


    @Override
    public void initView() {
        super.initView();
        unfinishied=(SwitchButton)findViewById(R.id.unfinished);
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
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(EMListActivity.this, MainActivity.class);
        startActivity(intent);
        CommonUtil.openNewActivityAnim(EMListActivity.this, true);
    }



    private class importFile extends AsyncTask<String,Void,EmList>{
        @Override
        protected EmList doInBackground(String... path) {
            String dirpath=path[0];
            String filename=dirpath.substring(dirpath.lastIndexOf("/")+1,dirpath.lastIndexOf("."));
            File file=new File(dirpath);
            try {
                InputStream txt=new FileInputStream(file);
                final EmList   emList = Transform.importTxtToRealm("HYH",filename,txt);
                return emList;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loadingDialog=   new ProgressDialog(EMListActivity.this);
            loadingDialog.setTitle("正在导入任务");
            loadingDialog.setCancelable(false);
            loadingDialog.show();

        }

        @Override
        protected void onPostExecute(EmList emList) {
            super.onPostExecute(emList);

            realm.beginTransaction();
            realm.insert(emList);
            realm.commitTransaction();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            eMlistAdapter.notifyDataSetChanged();
            loadingDialog.dismiss();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE_FROM_ACTIVITY) {
                List<String> list = data.getStringArrayListExtra("paths");
                String path =list.get(0);
                new importFile().execute(path);
            }
        }
    }


}