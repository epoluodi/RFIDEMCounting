package com.honeywell.android.rfidemcounting.utils;

import android.animation.TimeAnimator;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.TextView;

import com.honeywell.android.rfidemcounting.R;
import com.honeywell.android.rfidemcounting.litsener.TimeListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;



/**
 * Created by apple on 17/9/27.
 */

public class CommonUtil {
    /**
     * 隐藏软键盘
     */
    public static void hiddenKeyBoard(Activity activity) {

        try {
            if (activity == null) return;
            // 取消弹出的对话框
            InputMethodManager manager = (InputMethodManager) activity.getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (manager.isActive()) { // 只有在键盘正处于弹出状态时再去隐藏  by:KNothing
                if (activity.getCurrentFocus() == null) {
                    return;
                }
                manager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * B-->A页面,B从屏幕中间向右测出，A从左至右滑到屏幕中间,
     * 向后退出动画
     */
    public static void exitActivityAndBackAnim(Activity act, boolean finish) {
        if (act != null) {
            act.finish();
            act.overridePendingTransition(R.anim.push_left_to_middle_in, R.anim.push_middle_to_right_out);
        }
    }
    /**
     * A-->B页面,A向左滑出屏幕,B从屏幕右侧滑动屏幕中间
     */
    public static void openNewActivityAnim(Activity act, boolean finish) {
        if (act != null) {
            act.overridePendingTransition(R.anim.push_right_to_middle_in, R.anim.push_middle_to_left_out);
            if (finish) {
                act.finish();
            }
        }
    }
    public static String getCurrentDate(){
        SimpleDateFormat formatter =   new SimpleDateFormat("yyyy-MM-dd");
        Date curDate    =   new Date(System.currentTimeMillis());//获取当前时间
        String str    =    formatter.format(curDate);
        return str;
    }

    public static void showDatePickDlg(Context context, final TimeListener listener) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                listener.getDate(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }

    public static void showTimePickDlg(Context context, final TimeListener listener) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                listener.getDate(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }
    public static String clanderTodatetime(Calendar calendar, String style) {
        SimpleDateFormat formatter = new SimpleDateFormat(style);
        return formatter.format(calendar.getTime());
    }

    /**
         * 重写datePicker 1.只显示 年-月 2.title 只显示 年-月
         * @author lmw
         */

    public static void showDatePickMonthDlg(Context context, final TimeListener listener) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new YearPickerDialog(context, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                listener.getDate(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }
    public static class YearPickerDialog extends DatePickerDialog {
        public YearPickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
            super(context, callBack, year, monthOfYear, dayOfMonth);
            this.setTitle(year + "年" + (monthOfYear + 1) + "月");

            ((ViewGroup) ((ViewGroup) this.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
        }

        @Override
        public void onDateChanged(DatePicker view, int year, int month, int day) {
            super.onDateChanged(view, year, month, day);
            this.setTitle(year + "年" + (month + 1) + "月");
        }



    }

    // 字符串类型日期转化成date类型
    public static Date strToDate(String style, String date) {
        SimpleDateFormat formatter = new SimpleDateFormat(style);
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public static String dateToStr(String style, Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(style);
        return formatter.format(date);
    }
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    public static String getUUID(){
        return  UUID.randomUUID().toString();
    }
    public static String getText(TextView tv){
        return tv.getText().toString().trim();
    }

}
