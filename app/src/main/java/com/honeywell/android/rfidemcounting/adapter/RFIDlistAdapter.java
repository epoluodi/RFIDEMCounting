package com.honeywell.android.rfidemcounting.adapter;



import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

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
        int i=helper.getAdapterPosition();
        helper.setText(R.id.index, String.valueOf(i+1))
                .setText(R.id.epcid,item.getEpcid())
              /*  .setText(R.id.name, item.getIsCounted())*/
                .setText(R.id.state, item.getState());
                if (item.isIsem()){
                    helper.itemView.findViewById(R.id.index).setBackground(ContextCompat.getDrawable(mContext,R.drawable.shape_blue));
                }

    }



}
