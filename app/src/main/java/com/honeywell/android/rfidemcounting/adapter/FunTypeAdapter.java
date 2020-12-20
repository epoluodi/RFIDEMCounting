package com.honeywell.android.rfidemcounting.adapter;



import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.honeywell.android.rfidemcounting.R;
import com.honeywell.android.rfidemcounting.bean.EmList;
import com.honeywell.android.rfidemcounting.bean.FunctionType;

import java.util.List;

public class FunTypeAdapter extends BaseQuickAdapter<FunctionType, BaseViewHolder> {

    public FunTypeAdapter(int layoutResId, @Nullable List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FunctionType item) {
        helper.setImageResource(R.id.iv, item.img)
                .setText(R.id.tv, item.des);
    }


}
