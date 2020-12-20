package com.honeywell.android.rfidemcounting;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.honeywell.android.rfidemcounting.BaseActivity;
import com.honeywell.android.rfidemcounting.R;
import com.honeywell.android.rfidemcounting.adapter.FunTypeAdapter;
import com.honeywell.android.rfidemcounting.bean.FunctionType;
import com.honeywell.android.rfidemcounting.utils.CommonUtil;

import java.util.ArrayList;
import butterknife.BindView;

/**
 * Created by apple on 17/9/29.
 */

public class MainActivity extends BaseActivity {
    private long mExitTime = 0;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    private FunTypeAdapter mAdapter;
    private int imgs[] = {R.mipmap.l3,R.mipmap.l2};
    private ArrayList<FunctionType> mList = new ArrayList<>();


    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_main2;
    }



    @Override
    public void initView() {
        super.initView();
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        View v = LayoutInflater.from(this).inflate(R.layout.top_iv,null);
        FunctionType functionType=new FunctionType();
        functionType.des="盘点";
        functionType.img=R.mipmap.l2;
        FunctionType functionType1=new FunctionType();
        functionType1.des="设置";
        functionType1.img=R.mipmap.repair;
        mList.add(functionType);
        mList.add(functionType1);
        mAdapter = new FunTypeAdapter(R.layout.item_iv_tv1,mList);
        mAdapter.addHeaderView(v);
        recyclerView.setAdapter(mAdapter);
    }


    @Override
    public void initData() {

    }

    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() - mExitTime > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    public void initTitle() {
        tv_center_title.setVisibility(View.VISIBLE);
        tv_center_title.setText("首页");
    }

    @Override
    public void setListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (position) {
                    case 0:
                        Intent intent = new Intent(MainActivity.this, EMListActivity.class);
                        startActivity(intent);
                        CommonUtil.openNewActivityAnim(MainActivity.this, false);
                        break;
                    case 1:
                        Intent intent1 = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(intent1);
                        CommonUtil.openNewActivityAnim(MainActivity.this, false);
                }
            }
        });
    }
}
