package com.honeywell.android.rfidemcounting;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.honeywell.android.rfidemcounting.utils.PermissionUtils;

import butterknife.BindView;

public class EmMainActivity extends BaseActivity {

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

                    startActivity(MainActivity.class);
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionUtils.isGrantExternalRW(this,1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //检验是否获取权限，如果获取权限，外部存储会处于开放状态，会弹出一个toast提示获得授权
                    String sdCard = Environment.getExternalStorageState();

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EmMainActivity.this, "未获取权限，请手动设置存储权限", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}