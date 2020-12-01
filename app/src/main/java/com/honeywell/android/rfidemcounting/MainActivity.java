package com.honeywell.android.rfidemcounting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    private long mExitTime = 0;
    @BindView(R.id.bt_exit)
    public Button bt_exit;

    @BindView(R.id.bt_login)
    public Button bt_login;
    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_login;
    }

    @Override
    public void setListener() {
        bt_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _exit();
            }
        });

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //登录信息验证
                if (true){
                    startActivity(EMListActivity.class);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void initTitle() {
      
       tv_center_title.setVisibility(View.VISIBLE);
       tv_center_title.setText("登   录");
    }

    private void _exit() {
        if (System.currentTimeMillis() - mExitTime > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }
}