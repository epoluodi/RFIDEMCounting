package com.honeywell.android.rfidemcounting.fragment;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.honeywell.android.rfidemcounting.R;
import com.honeywell.android.rfidemcounting.utils.SettingParam;
import com.honeywell.rfidservice.rfid.AntennaPower;
import com.honeywell.rfidservice.rfid.RfidReaderException;


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

    //???正式代码中请删除，并赋予实际的函数调用
    public boolean checkIsRFIDReady() {
        return true;
    }

    public AntennaPower[] getAntennaPower(){
        AntennaPower[] ap = new AntennaPower[1];

        SharedPreferences sp = getActivity().getSharedPreferences("SettingParam", Context.MODE_PRIVATE);
        ap[0] = new AntennaPower(1, sp.getInt("ReadPower", 3000),
                sp.getInt("WritePower", 3000));

        //ap[0] = new AntennaPower(1, SettingParam.AnteReadPower, SettingParam.AnteWritePower);

        return ap;
    }

    public void setAntennaPower(AntennaPower[] ap){
        SharedPreferences sp = getActivity().getSharedPreferences("SettingParam", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("ReadPower", ap[0].getReadPower());
        editor.putInt("WritePower", ap[0].getWritePower());
        editor.commit();

        SettingParam.AnteReadPower = ap[0].getReadPower();
        SettingParam.AnteWritePower = ap[0].getWritePower();

        //???rfidMgr.setAntennaPower(ap);
    }

    private void loadSettings() {
        if (checkIsRFIDReady()) {
            try {
                AntennaPower[] ap = getAntennaPower();

                if(ap == null){
                    return;
                }

                for (int i = 0; i < ap.length; i++) {
                    if (i == 0) {
                        mAnteReadPowerListPreference.setValue(String.valueOf(ap[i].getReadPower()));
                        mAnteReadPowerListPreference.setSummary(String.valueOf(ap[i].getReadPower()/100) + "dBm");
                        mAnteWritePowerListPreference.setValue(String.valueOf(ap[i].getWritePower()));
                        mAnteWritePowerListPreference.setSummary(String.valueOf(ap[i].getWritePower()/100) + "dBm");
                    }
                }

                Toast.makeText(getActivity(), "获取天线功率成功", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getActivity(), "获取天线功率失败" + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
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
            if (checkIsRFIDReady()) {
                try {
                    for (int i = 0; i < antNum; i++) {
                        ap[i] = new AntennaPower(i + 1, Integer.valueOf(val) * 100, Integer.valueOf(mAnteWritePowerListPreference.getValue()) * 100);
                    }
                    setAntennaPower(ap);
                    mAnteReadPowerListPreference.setSummary(val + "dBm");
                    Toast.makeText(getActivity(), "设置天线功率成功", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "设置天线功率失败" + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (preference == mAnteWritePowerListPreference) {
            String val = (String) newValue;
            int antNum = 1;
            AntennaPower[] ap = new AntennaPower[antNum];
            if (checkIsRFIDReady()) {
                try {
                    for (int i = 0; i < antNum; i++) {
                        ap[i] = new AntennaPower(i + 1, Integer.valueOf(mAnteReadPowerListPreference.getValue()) * 100, Integer.valueOf(val) * 100);
                    }
                    setAntennaPower(ap);
                    mAnteWritePowerListPreference.setSummary(val + "dBm");
                    Toast.makeText(getActivity(), "设置天线功率成功", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "设置天线功率失败" + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        return true;
    }
}
