package com.honeywell.android.rfidemcounting;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.honeywell.android.rfidemcounting.utils.CommonUtil;
import com.honeywell.android.rfidemcounting.utils.DialogUtil;
import com.honeywell.android.rfidemcounting.utils.StringUtils;
import com.honeywell.android.rfidemcounting.utils.ToastUtils;
import com.honeywell.android.rfidemcounting.views.IBaseView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by apple on 17/9/26.
 */

public abstract class BaseActivity extends AppCompatActivity implements IBaseView {
    @Nullable
    @BindView(R.id.iv_back)
    public ImageView iv_back;
    @Nullable
    @BindView(R.id.tv_center_title)
    public TextView tv_center_title;
//    @Nullable
//    @BindView(R.id.iv_back2)
//    public ImageView iv_back2;
    @Nullable
    @BindView(R.id.iv_right_title)
    public ImageView iv_right_title;
    @Nullable
    @BindView(R.id.tv_right_title)
    public TextView tv_right_title;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(attachLayoutRes());
        ButterKnife.bind(this);

        initView();
        initData();

        setListener();

    }
    public void initData() {

    }
    public void initView() {
        initTitle();
    }

    public void initTitle() {

    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        boolean screen = (boolean) SPUtils.get(Constants.screen,true);
//        if (screen){
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }else{
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        }
//    }

    /**
     * 绑定布局文件
     *
     * @return 布局文件ID
     */
    @LayoutRes
    protected abstract int attachLayoutRes();

    /**
     * [设置监听]
     */
    public abstract void setListener();

    /**
     * [简化Toast]
     *
     * @param msg
     */
    public void showToast(String msg) {
        ToastUtils.showToast(msg);
    }
    public void showToast2(String msg) {
        ToastUtils.showToast2(msg);
    }
    /**
     * [页面跳转]
     */
    public void startActivity(Class<?> clz) {
        startActivity(new Intent(BaseActivity.this, clz));
    }
    /**
     * dialog
     */
    private Dialog loadingDialog = null;

    public void showLoadingPanel(boolean cancelable) {
        if (loadingDialog != null) {
            if (!loadingDialog.isShowing()) {
                loadingDialog.show();
            }
            return;
        }
        loadingDialog = DialogUtil.showLoadingDialog(this, cancelable, null);
    }

    public boolean isDialogShowing() {
        return loadingDialog != null && loadingDialog.isShowing();
    }

    public void hideLoadingPanel() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
    @Override
    public void showLoading() {
        showLoadingPanel(false);
    }

    @Override
    public void hideLoading() {
        hideLoadingPanel();
    }
    @Override
    public void onBackPressed() {
        CommonUtil.exitActivityAndBackAnim(this,true);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // land do nothing is ok
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // port do nothing is ok
        }
    }
    protected<T> ArrayAdapter<T> getArrayAdapter(T[] param){
        ArrayAdapter<T> adapter=new ArrayAdapter<T>(this,android.R.layout.simple_spinner_dropdown_item,param);
        adapter.setDropDownViewResource(R.layout.spinner_item_view);
        return adapter;
    }
    protected<T> ArrayAdapter<T> getArrayAdapter(List<T> param){
        ArrayAdapter<T> adapter=new ArrayAdapter<T>(this,android.R.layout.simple_spinner_dropdown_item,param);
        adapter.setDropDownViewResource(R.layout.spinner_item_view);
        return adapter;
    }
    protected<T> void selectedItem(Spinner spi, T selectItem) {
        if(spi==null||selectItem==null||selectItem.equals(""))
            return;
        SpinnerAdapter adapter=spi.getAdapter();
        for(int i=0;i<adapter.getCount();i++){
            if(adapter.getItem(i).toString().equals(selectItem)){
                spi.setSelection(i);
                break;
            }
        }
    }
    protected int getIntegerValue(EditText editText){
        int result=-1;

        String str=editText.getText().toString();
        if(!StringUtils.isEmpty(str))
            result= Integer.valueOf(str);
        return result;
    }
    protected void hiddenInputKey() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null)
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

    }
}
