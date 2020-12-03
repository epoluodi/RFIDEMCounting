package com.honeywell.android.rfidemcounting.adapter;



import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.honeywell.android.rfidemcounting.R;
import com.honeywell.android.rfidemcounting.bean.EmList;

import java.util.List;

public class EMlistAdapter extends BaseQuickAdapter<EmList, BaseViewHolder> {

    public EMlistAdapter(int layoutResId, @Nullable List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, EmList item) {
        helper.setText(R.id.index, item.getId())
                .setText(R.id.name, item.getName())
                .setText(R.id.state, item.getState());
    }


}
