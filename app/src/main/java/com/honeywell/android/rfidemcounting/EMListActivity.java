package com.honeywell.android.rfidemcounting;

import android.content.Intent;
import android.view.View;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.honeywell.android.rfidemcounting.adapter.EMlistAdapter;
import com.honeywell.android.rfidemcounting.bean.EmList;
import com.honeywell.android.rfidemcounting.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class EMListActivity extends BaseActivity {
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    private List<EmList> mList = new ArrayList<>();
    private EMlistAdapter eMlistAdapter;
    @Override
    protected int attachLayoutRes() {
        return R.layout.comment_list;
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

    }

    @Override
    public void initData() {
        super.initData();
        EmList em1=new EmList();
        em1.setId("1");
        em1.setName("盘点任务1");
        em1.setState("未盘");
        EmList em2=new EmList();
        em2.setId("2");
        em2.setName("盘点任务2");
        em2.setState("已盘");
        EmList em3=new EmList();
        em3.setId("3");
        em3.setName("盘点任务3");
        em3.setState("正在进行");
        mList.add(em1);
        mList.add(em2);
        mList.add(em3);
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
    }
}