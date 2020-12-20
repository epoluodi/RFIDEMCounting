package com.honeywell.android.rfidemcounting;

import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.honeywell.android.rfidemcounting.utils.PermissionUtils;

import java.io.File;


public class LaunchActivity extends BaseActivity{

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_launch;
    }

    @Override
    public void initData() {
        super.initData();
       String filePath = this.getExternalCacheDir().getPath()+"/import";
        File file = new File(filePath);
        if (!file.exists()) {
            // 创建文件夹
            file.mkdirs();
        }
        filePath = this.getExternalCacheDir().getPath()+"/export";
      file = new File(filePath);
        if (!file.exists()) {
            // 创建文件夹
            file.mkdirs();
        }
        new Handler().postDelayed(new Runnable() {
            public void run() {

                startActivity(EmMainActivity.class);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }

        }, 1000 * 2);
    }

    @Override
    public void setListener() {

    }

}
