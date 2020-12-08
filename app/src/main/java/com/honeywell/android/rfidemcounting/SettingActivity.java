package com.honeywell.android.rfidemcounting;

import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.honeywell.android.rfidemcounting.fragment.AntePowerFragment;
import com.honeywell.android.rfidemcounting.utils.CommonUtil;

import butterknife.BindView;


public class SettingActivity extends BaseActivity{

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView() {
        super.initView();
        tv_center_title.setVisibility(View.VISIBLE);
        tv_center_title.setText("设置");

        iv_back.setVisibility(View.VISIBLE);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();   //开启Fragment事务
        transaction.add(R.id.ante_setting, new AntePowerFragment());    //将天线设置Fragment视图放置到FrameLayout布局中
        transaction.commit();       //Fragment调用生效
    }

    @Override
    public void setListener() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.exitActivityAndBackAnim(SettingActivity.this, true);
            }
        });
    }

}
