package com.honeywell.android.rfidemcounting;

import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import butterknife.BindView;


public class SettingActivity extends BaseActivity{

    private Button bt_set;
    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView() {
        super.initView();
        bt_set=(Button)findViewById(R.id.bt_set);
        tv_center_title.setVisibility(View.VISIBLE);
        tv_center_title.setText("设置");
    }

    @Override
    public void setListener() {
        bt_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast=Toast.makeText(getApplicationContext(),"设置",Toast.LENGTH_SHORT);
                toast.show();
                boolean setup=setRFID();
            }
        });
    }
    /*
    方法功能：设置RFID
    参数：void，可以按照情况自定义
    返回值：设置是否生效
    *
     */
    private boolean setRFID(){
        return  true;
    }
}
