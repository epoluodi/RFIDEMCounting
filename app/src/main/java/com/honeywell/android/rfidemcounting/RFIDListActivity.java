package com.honeywell.android.rfidemcounting;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.honeywell.android.rfidemcounting.BaseActivity;
import com.honeywell.android.rfidemcounting.R;
import com.honeywell.android.rfidemcounting.adapter.EMlistAdapter;
import com.honeywell.android.rfidemcounting.adapter.RFIDlistAdapter;
import com.honeywell.android.rfidemcounting.bean.EmList;
import com.honeywell.android.rfidemcounting.bean.RFIDList;
import com.honeywell.android.rfidemcounting.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class RFIDListActivity extends BaseActivity {
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    private List<RFIDList> mList = new ArrayList<>();
    private RFIDlistAdapter rfiDlistAdapter;


    @BindView(R.id.tv_all)
    TextView tv_all;

    @BindView(R.id.tv_count)
    TextView tv_count;

    @BindView(R.id.tv_unknown)
    TextView tv_unknown;
    @Override
    protected int attachLayoutRes() {
        return R.layout.comment_list;
    }

    @Override
    public void setListener() {
        tv_right_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast=Toast.makeText(getApplicationContext(),"导出任务",Toast.LENGTH_SHORT);
                toast.show();

                int missionCount=exportMissionList();
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.exitActivityAndBackAnim(RFIDListActivity.this, false);
            }
        });
    }
    /*方法：导出任务
      参数：无
      返回值：导出任务RFID条数
      */
    private   int exportMissionList(){
        return 0;
    }
    @Override
    public void initData() {
        super.initData();
        RFIDList rfid1=new RFIDList();
        rfid1.setId("1");
        rfid1.setName("物品1");
        rfid1.setEpcid("epcid12345");
        rfid1.setState("未盘");
        RFIDList rfid2=new RFIDList();
        rfid2.setId("2");
        rfid2.setEpcid("epcid12346");
        rfid2.setName("物品2");
        rfid2.setState("已盘");
        RFIDList rfid3=new RFIDList();
        rfid3.setId("3");
        rfid3.setEpcid("epcid12347");
        rfid3.setName("物品3");
        rfid3.setState("未知");
        mList.add(rfid1);
        mList.add(rfid2);
        mList.add(rfid3);
    }

    @Override
    public void initView() {
        super.initView();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rfiDlistAdapter=new RFIDlistAdapter(R.layout.activity_rfid_list,mList);
        recyclerView.setAdapter(rfiDlistAdapter);
    }

    @Override
    public void initTitle() {
        super.initTitle();
        tv_center_title.setVisibility(View.VISIBLE);
        tv_center_title.setText("盘点明细列表");

        iv_back.setVisibility(View.VISIBLE);

        tv_right_title.setVisibility(View.VISIBLE);
        tv_right_title.setText("导出");

        tv_all.setText("共3条");
        tv_count.setText("已盘2条");
        tv_unknown.setText("1条未知");
    }
}