package com.honeywell.android.rfidemcounting.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.honeywell.android.rfidemcounting.MyApplication;
import com.honeywell.android.rfidemcounting.R;
import com.honeywell.android.rfidemcounting.utils.SettingParam;
import com.honeywell.rfidservice.ConnectionState;
import com.honeywell.rfidservice.TriggerMode;
import com.honeywell.rfidservice.rfid.AntennaPower;


public class AntePowerFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    private ListPreference mAnteReadPowerListPreference;
    private ListPreference mAnteWritePowerListPreference;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.ante_power_settings);
        mAnteReadPowerListPreference = (ListPreference) findPreference("read_power_ante");
        mAnteWritePowerListPreference = (ListPreference) findPreference("write_power_ante");
        mAnteReadPowerListPreference.setOnPreferenceChangeListener(this);
        mAnteWritePowerListPreference.setOnPreferenceChangeListener(this);
    }

    public boolean checkIsRFIDReady(boolean showToast) {
        try {
            if (MyApplication.getInstance().rfidMgr.getConnectionState() != ConnectionState.STATE_CONNECTED) {
                if (showToast) {
                    Toast.makeText(getActivity(), "设备未连接！", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
            if (!MyApplication.getInstance().rfidMgr.readerAvailable()) {
                if (showToast) {
                    Toast.makeText(getActivity(), "读写器为空！", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
            if (MyApplication.getInstance().rfidMgr.getTriggerMode() != TriggerMode.RFID) {
                if (showToast) {
                    Toast.makeText(getActivity(), "当前模式不是RFID模式！", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "设备存在异常", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    public AntennaPower[] getAntennaPower(){
        AntennaPower[] ap = new AntennaPower[1];

        //使用轻量级数据存储SharedPreferences进行RFID配置参数的存储，从SettingParam文件中获取对应的参数，如果参数不存在，则返回默认值3000
        SharedPreferences sp = getActivity().getSharedPreferences("SettingParam", Context.MODE_PRIVATE);
        ap[0] = new AntennaPower(1, sp.getInt("ReadPower", 3000),
                sp.getInt("WritePower", 3000));

        //ap[0] = new AntennaPower(1, SettingParam.AnteReadPower, SettingParam.AnteWritePower);

        return ap;
    }

    public boolean setAntennaPower(AntennaPower[] ap){
        SharedPreferences sp = getActivity().getSharedPreferences("SettingParam", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();            //需要Editor编辑文件数据
        editor.putInt("ReadPower", ap[0].getReadPower());
        editor.putInt("WritePower", ap[0].getWritePower());
        editor.commit();                                        //同步方式提交修改，将数据写入存储文件

        SettingParam.AnteReadPower = ap[0].getReadPower();      //修改全局配置变量
        SettingParam.AnteWritePower = ap[0].getWritePower();

        try {
            MyApplication.getInstance().mRfidReader.setAntennaPower(ap);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "设置天线功率失败", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void loadSettings() {
        if (checkIsRFIDReady(true)) {
            try {
                AntennaPower[] ap = getAntennaPower();

                if(ap == null){
                    return;
                }

                for (int i = 0; i < ap.length; i++) {
                    if (i == 0) {
                        mAnteReadPowerListPreference.setValue(String.valueOf(ap[i].getReadPower()/100));
                        mAnteReadPowerListPreference.setSummary(String.valueOf(ap[i].getReadPower()/100) + "dBm");
                        mAnteWritePowerListPreference.setValue(String.valueOf(ap[i].getWritePower()/100));
                        mAnteWritePowerListPreference.setSummary(String.valueOf(ap[i].getWritePower()/100) + "dBm");
                    }
                }

                Toast.makeText(getActivity(), "获取天线功率成功", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getActivity(), "获取天线功率失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSettings();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            loadSettings();
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mAnteReadPowerListPreference) {
            String val = (String) newValue;
            int antNum = 1;
            AntennaPower[] ap = new AntennaPower[antNum];
            if (checkIsRFIDReady(true)) {
                try {
                    for (int i = 0; i < antNum; i++) {
                        ap[i] = new AntennaPower(i + 1, Integer.valueOf(val) * 100, Integer.valueOf(mAnteWritePowerListPreference.getValue()) * 100);
                    }
                    mAnteReadPowerListPreference.setSummary(val + "dBm");
                    if(setAntennaPower(ap)){
                        Toast.makeText(getActivity(), "设置天线功率成功", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "设置天线功率失败", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (preference == mAnteWritePowerListPreference) {
            String val = (String) newValue;
            int antNum = 1;
            AntennaPower[] ap = new AntennaPower[antNum];
            if (checkIsRFIDReady(true)) {
                try {
                    for (int i = 0; i < antNum; i++) {
                        ap[i] = new AntennaPower(i + 1, Integer.valueOf(mAnteReadPowerListPreference.getValue()) * 100, Integer.valueOf(val) * 100);
                    }
                    mAnteWritePowerListPreference.setSummary(val + "dBm");
                    if(setAntennaPower(ap)){
                        Toast.makeText(getActivity(), "设置天线功率成功", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "设置天线功率失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
        return true;
    }
}
