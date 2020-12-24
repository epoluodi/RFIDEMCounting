package com.honeywell.android.rfidemcounting;

import android.os.Environment;
import android.os.Handler;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

import butterknife.BindView;


public class LaunchActivity extends BaseActivity{

    @BindView(R.id.txtver)
    TextView txtver;


    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_launch;
    }

    @Override
    public void initData() {
        super.initData();


        File file =new File(Environment.getExternalStorageDirectory() +"/import");
        if (!file.exists())
        {
            file.mkdir();
        }

        file =new File(Environment.getExternalStorageDirectory() +"/export");
        if (!file.exists())
        {
            file.mkdir();
        }
      /* String filePath = this.getExternalCacheDir().getPath()+"/import";
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
        }*/
        new Handler().postDelayed(new Runnable() {
            public void run() {

                startActivity(LoginActivity.class);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }

        }, 1000 * 2);
    }

    @Override
    public void initView() {
        super.initView();
        try {
            txtver.setText(getPackageManager().
                    getPackageInfo(getPackageName(), 0).versionName);
        }catch (Exception e)
        {e.printStackTrace();}
    }

    @Override
    public void setListener() {

    }

}
