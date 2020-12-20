package com.honeywell.android.rfidemcounting;

import android.view.View;

import com.leon.lfilepickerlibrary.LFilePicker;

import java.io.File;

public class ImportActivity extends BaseActivity {
    private String [] args={".txt"};
    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_import_list;
    }

    @Override
    public void setListener() {
        tv_right_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filePath = getApplication().getExternalCacheDir().getPath()+"/export";
                int REQUESTCODE_FROM_ACTIVITY = 1000;
                new LFilePicker()
                        .withActivity(ImportActivity.this)
                        .withRequestCode(REQUESTCODE_FROM_ACTIVITY)
                        .withStartPath(filePath)
                        .withFileFilter(args)
                        .start();
            }
        });
    }

    @Override
    public void initTitle() {
        super.initTitle();
        tv_center_title.setVisibility(View.VISIBLE);
        tv_center_title.setText("导入任务");

        tv_right_title.setVisibility(View.VISIBLE);
        tv_right_title.setText("选择文件");
    }

    @Override
    public void initView() {
        super.initView();
    }
}
