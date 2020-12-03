package com.honeywell.android.rfidemcounting.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.honeywell.android.rfidemcounting.R;
import com.honeywell.android.rfidemcounting.utils.DialogUtil;
import com.honeywell.android.rfidemcounting.utils.ToastUtils;
import com.honeywell.android.rfidemcounting.views.IBaseView;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by apple on 17/9/26.
 */

public abstract class BaseFragment extends Fragment implements IBaseView {
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
    protected Activity mActivity;
    private View mRootView;

    /**
     * 获得全局的，防止使用getActivity()为空
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = LayoutInflater.from(mActivity).inflate(getLayoutId(), container, false);
            ButterKnife.bind(this, mRootView);
            initView();
            initData();
            initListener();
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }
    /**
     * 该抽象方法就是 onCreateView中需要的layoutID
     *
     * @return
     */
    protected abstract int getLayoutId();
    /**
     * 该抽象方法就是 初始化view
     *
     */
    protected  void initView(){
        initTitle();
    }

    protected void initTitle() {

    }

    /**
     * 执行数据的加载
     */
    protected abstract void initData();
    /**
     * 执行listener
     */
    protected abstract void initListener();
    /**
     * [简化Toast]
     * @param msg
     */
    protected void showToast(String msg){
        ToastUtils.showToast(msg);
    }
    /**
     * [页面跳转]
     */
    public void startActivity(Class<?> clz) {
        startActivity(new Intent(mActivity, clz));
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
        loadingDialog = DialogUtil.showLoadingDialog(getActivity(),  cancelable, null);
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
}

