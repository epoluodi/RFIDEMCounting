package com.honeywell.android.rfidemcounting;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.honeywell.android.rfidemcounting.adapter.RFIDlistAdapter;
import com.honeywell.android.rfidemcounting.bean.EmBean;
import com.honeywell.android.rfidemcounting.bean.RFIDList;
import com.honeywell.android.rfidemcounting.utils.CommonUtil;
import com.honeywell.rfidservice.EventListener;
import com.honeywell.rfidservice.RfidManager;
import com.honeywell.rfidservice.TriggerMode;
import com.honeywell.rfidservice.rfid.AntennaPower;
import com.honeywell.rfidservice.rfid.OnTagReadListener;
import com.honeywell.rfidservice.rfid.RfidReader;
import com.honeywell.rfidservice.rfid.TagAdditionData;
import com.honeywell.rfidservice.rfid.TagReadData;
import com.honeywell.rfidservice.rfid.TagReadOption;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import io.realm.Realm;

public class RFIDDetailsActivity extends BaseActivity {
    private static final String TAG = "RFIDDetailsActivity";
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    private static List<RFIDList> mList;
    private RFIDlistAdapter rfiDlistAdapter;
    private String task;
    private EmBean emList;
    private static EmBean em;
    @BindView(R.id.tv_all)
    TextView tv_all;
    @BindView(R.id.tv_count)
    TextView tv_count;
    //private RfidManager mRfidMgr;
    //private MyHandler mMyHandler;
    private MyApplication mMyApplication;
   // private RfidReader mReader;
    @BindView(R.id.tv_unknown)
    TextView tv_unknown;
    private final static String ACTION_HONEYWLL = "com.honeywell";
    private String filePath;
    private Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        filePath = getApplication().getExternalCacheDir().getPath() + "/export";
        //mMyHandler = new MyHandler(this);


    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.comment_list;
    }

    private static class MyHandler extends Handler {
        private WeakReference ref;

        private MyHandler(RFIDDetailsActivity act) {
            ref = new WeakReference<>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (ref.get() != null) {
                ((RFIDDetailsActivity) ref.get()).handleMessage(msg);
            }
        }
    }

    public void handleMessage(Message msg) {
        switch (msg.what) {
        }
    }

    int readPower;

    @Override
    public void setListener() {




        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                CommonUtil.exitActivityAndBackAnim(RFIDDetailsActivity.this, true);
            }
        });
    }


    @Override
    public void initData() {
        task = getIntent().getStringExtra("task");
        Realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();
        emList = realm.where(EmBean.class).equalTo("id", task).findFirst();
        em = realm.copyFromRealm(emList);
        mList = em.getRfidList();
        rfiDlistAdapter.setNewData(mList);
    }

    @Override
    public void initView() {
        super.initView();
        mMyApplication = MyApplication.getInstance();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rfiDlistAdapter = new RFIDlistAdapter(R.layout.activity_rfid_list, mList);
        recyclerView.setAdapter(rfiDlistAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void initTitle() {
        super.initTitle();
        tv_center_title.setVisibility(View.VISIBLE);
        tv_center_title.setText("盘点明细列表");

        iv_back.setVisibility(View.VISIBLE);

        tv_all.setText("共3条");
        tv_count.setText("已盘2条");
        tv_unknown.setText("1条未知");
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();

        CommonUtil.exitActivityAndBackAnim(RFIDDetailsActivity.this, true);
    }



}