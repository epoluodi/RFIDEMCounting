package com.honeywell.android.rfidemcounting;

import android.os.Handler;




public class LaunchActivity extends BaseActivity{

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_launch;
    }

    @Override
    public void initData() {
        super.initData();

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    startActivity(MainActivity.class);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }, 1000 * 2);
    }

    @Override
    public void setListener() {

    }
}
