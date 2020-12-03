package com.honeywell.android.rfidemcounting.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import com.honeywell.android.rfidemcounting.R;



/**
 * Created by apple on 17/7/5.
 */

public class DialogUtil {
    /**
     * 加载dialog
     */
    public static Dialog showLoadingDialog(Context context,
                                           Boolean cancelable, DialogInterface.OnCancelListener listener) {
        Activity activity = (Activity) context;
        final AlertDialog dialog = new AlertDialog.Builder(
                activity.isChild() ? activity.getParent() : activity, R.style.tipsDialog).create();
        dialog.setCancelable(cancelable);
        if (listener != null) {
            dialog.setOnCancelListener(listener);
        }

        dialog.show();
        View loadingPanel = LayoutInflater.from(context).inflate(
                R.layout.loading_dialog_layout, null);
        dialog.setContentView(loadingPanel);
        return dialog;
    }
}
