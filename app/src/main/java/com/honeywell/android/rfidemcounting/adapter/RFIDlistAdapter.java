package com.honeywell.android.rfidemcounting.adapter;



import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.honeywell.android.rfidemcounting.R;
import com.honeywell.android.rfidemcounting.bean.RFIDList;

import java.util.List;

public class RFIDlistAdapter extends BaseQuickAdapter<RFIDList, BaseViewHolder> {

    public RFIDlistAdapter(int layoutResId, @Nullable List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RFIDList item) {
        int i=helper.getLayoutPosition();
        helper.setText(R.id.index, String.valueOf(i+1))
                .setText(R.id.epcid,item.getEpcid())
              /*  .setText(R.id.name, item.getIsCounted())*/
                .setText(R.id.state, item.getState());
    }



}
