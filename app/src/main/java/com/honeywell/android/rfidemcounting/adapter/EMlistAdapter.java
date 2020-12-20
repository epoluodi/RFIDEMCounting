package com.honeywell.android.rfidemcounting.adapter;



import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.honeywell.android.data.model.InventoryTask;
import com.honeywell.android.rfidemcounting.R;
import com.honeywell.android.rfidemcounting.bean.EmList;

import java.util.List;

public class EMlistAdapter extends BaseQuickAdapter<EmList, BaseViewHolder> {
    public EMlistAdapter(int layoutResId, @Nullable List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, EmList item) {
        int i=helper.getLayoutPosition();
        helper.setText(R.id.index,String.valueOf(i+1))
                .setText(R.id.name, item.getName())
                .setText(R.id.state, String.valueOf(item.getState()));
    }


}
