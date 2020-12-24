package com.honeywell.android.rfidemcounting;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.honeywell.android.data.model.User;
import com.honeywell.android.data.utils.Transform;
import com.honeywell.android.rfidemcounting.adapter.EMlistAdapter;
import com.honeywell.android.rfidemcounting.adapter.ExportEmAdapter;
import com.honeywell.android.rfidemcounting.bean.EmBean;
import com.honeywell.android.rfidemcounting.utils.CommonUtil;
import com.leon.lfilepickerlibrary.LFilePicker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.realm.Realm;
import io.realm.Sort;

public class ExportEmActivity extends BaseActivity {
    private static final String TAG = "ExportEmActivity";
    private static int REQUESTCODE_FROM_ACTIVITY = 1000;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    Dialog loadingDialog;
    private Realm realm;
    private String user_name;
    private String filePath;
    private List<String> ids;
    User hyh;
    private List<EmBean> mList ;
    private ExportEmAdapter eMlistAdapter;
    @Override
    protected int attachLayoutRes() {
        return R.layout.comment_list_rfid;
    }
    private static class MyHandler extends Handler {
        private WeakReference ref;

        private MyHandler(ExportEmActivity act) {
            ref = new WeakReference<>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (ref.get() != null) {
                ((ExportEmActivity) ref.get()).handleMessage(msg);
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

                    Intent intent = new Intent(ExportEmActivity.this, RFIDDetailsActivity.class);
                    intent.putExtra("task",  mList.get(position).getId());
                    startActivity(intent);
                    CommonUtil.openNewActivityAnim(ExportEmActivity.this, true);

            }
        });

        eMlistAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                final AlertDialog.Builder normalDialog = new AlertDialog.Builder(ExportEmActivity.this);
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
                ids=new ArrayList<>();
                for (int i=0;i<mList.size();i++) {
                    if (mList.get(i).isSelected()) {
                        ids.add(mList.get(i).getId());
                    }

                }
                new ExportEmActivity.exportfile().execute(ids);
              //  Log.v(TAG, String.valueOf(mList.get(0).isSelected()));
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommonUtil.exitActivityAndBackAnim(ExportEmActivity.this, true);
            }
        });
    }
    @Override
    public void initData() {
        super.initData();
        Realm.init(getApplicationContext());
        realm=Realm.getDefaultInstance();
        hyh=new User();
        user_name=MyApplication.user.getUserName();
        hyh.setUserName(user_name);
        mList=realm.where(EmBean.class).equalTo("username",user_name).equalTo("state","已完成").or().equalTo("state","已导出").findAll().sort("time",Sort.ASCENDING);
        eMlistAdapter.setNewData(mList);
        filePath = Environment.getExternalStorageDirectory()+"/export";
    }


    @Override
    public void initView() {
        super.initView();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eMlistAdapter=new ExportEmAdapter(R.layout.activity_export_list,mList);
        recyclerView.setAdapter(eMlistAdapter);

    }

    @Override
    public void initTitle() {
        super.initTitle();
        tv_center_title.setVisibility(View.VISIBLE);
        tv_center_title.setText("导出任务列表");

        tv_right_title.setVisibility(View.VISIBLE);
        tv_right_title.setText("导出");

        iv_back.setVisibility(View.VISIBLE);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        CommonUtil.exitActivityAndBackAnim(ExportEmActivity.this, true);
    }



    private class exportfile extends AsyncTask<List<String>, Void, Void> {
        @Override
        protected Void doInBackground(List<String>... ids) {
           // List<String> exportEm=emLists[0];
         //   List<EmBean> embeans=new ArrayList<>();
                try {

                    boolean isexport = Transform.exportTxtfrom(filePath, ids[0]);
                    if (isexport) {
                        Log.v(TAG, "export success");
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    Log.v(TAG, "export failed");
                    e.printStackTrace();
                }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog=   new ProgressDialog(ExportEmActivity.this);
            loadingDialog.setTitle("正在导出文本");
            loadingDialog.setCancelable(false);
            loadingDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loadingDialog.dismiss();
            eMlistAdapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(),"导出成功!",Toast.LENGTH_SHORT);

        }
    }


}