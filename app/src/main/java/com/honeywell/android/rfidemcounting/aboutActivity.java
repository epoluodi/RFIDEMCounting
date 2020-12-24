package com.honeywell.android.rfidemcounting;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.honeywell.ezservice.EzServiceManager;
import com.honeywell.rfidservice.RfidManager;
import com.honeywell.rfidservice.TriggerMode;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;

public class aboutActivity extends BaseActivity {

    @BindView(R.id.txtver)
    TextView txtver;
    @BindView(R.id.txtlcviense)
    TextView txtlcviense;
    @BindView(R.id.btnscan)
    TextView btnscan;

    private EditText editText;
    private RfidManager mRfidMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_about;
    }

    @Override
    public void setListener() {

    }

    @Override
    public void initView() {
        super.initView();

        if (Build.MODEL.contains("EDA")) {
            EzServiceManager.initService(this);
        }

        mRfidMgr = MyApplication.getInstance().rfidMgr;
        mRfidMgr.setTriggerMode(TriggerMode.BARCODE_SCAN);
        //获取软件版本号，对应AndroidManifest.xml下android:versionCode
        try {
            txtver.setText(getPackageManager().
                    getPackageInfo(getPackageName(), 0).versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnscan.setOnClickListener(onClickListenerscan);
        if (checkLicense()) {
            txtlcviense.setText("已授权");
            btnscan.setVisibility(View.INVISIBLE);
        } else {
            txtlcviense.setText("未授权");
        }
    }

    private View.OnClickListener onClickListenerscan = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            editText = new EditText(aboutActivity.this);
            AlertDialog.Builder builder = new AlertDialog.Builder(aboutActivity.this);
            builder.setTitle("扫描授权二维码");

            if (Build.MODEL.contains("EDA")) {
                Log.e("sn", EzServiceManager.getSerialNumber());
                builder.setMessage("本机SN："+ EzServiceManager.getSerialNumber());
            } else if (Build.MODEL.contains("CT40")) {
                if (Build.VERSION.SDK_INT < 27) {
                    Log.e("sn", Build.SERIAL);
                    builder.setMessage("本机SN：" + Build.SERIAL);
                }
            }
            builder.setView(editText);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String str = editText.getText().toString();
                    str = str.toLowerCase();
                    if (str.equals("")) {
                        Toast.makeText(aboutActivity.this,
                                "未知许可信息", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String md5str = "";

                    if (Build.MODEL.contains("EDA")) {
                        Log.e("sn", EzServiceManager.getSerialNumber());
                        md5str = md5(EzServiceManager.getSerialNumber() + "20201224");
                    } else if (Build.MODEL.contains("CT40")) {
                        if (Build.VERSION.SDK_INT < 27)
                            Log.e("sn", Build.SERIAL);
                            md5str = md5(Build.SERIAL + "20201224");
                    }


                    Log.e("MD5", md5str);
                    if (md5str.equals(str)) {
                        Toast.makeText(aboutActivity.this,
                                "授权成功", Toast.LENGTH_SHORT).show();
                        SharedPreferences sp = getSharedPreferences("Lciense", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean("License", true);
                        editor.commit();
                        txtlcviense.setText("已授权");
                        btnscan.setVisibility(View.INVISIBLE);
                    }else
                    {
                        Toast.makeText(aboutActivity.this,
                                "未知许可信息", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            builder.show();
            editText.requestFocus();
        }
    };


    @Override
    protected void onPause() {
        super.onPause();
        EzServiceManager.destroyService(this);
    }

    //检查License
    public static Boolean checkLicense() {
        try {

            SharedPreferences sp = MyApplication.getInstance().getSharedPreferences("Lciense", Context.MODE_PRIVATE);
            return sp.getBoolean("License", false);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static String md5(String sn) {
        try {
            MessageDigest bmd5 = MessageDigest.getInstance("MD5");
            bmd5.update(sn.getBytes());
            int i;
            StringBuffer buf = new StringBuffer();
            byte[] b = bmd5.digest();
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";

    }

    @Override
    public void initTitle() {
        super.initTitle();
        tv_center_title.setVisibility(View.VISIBLE);
        tv_center_title.setText("关于");
    }
}