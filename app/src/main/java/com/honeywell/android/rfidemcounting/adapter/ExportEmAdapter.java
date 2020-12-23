package com.honeywell.android.rfidemcounting.adapter;



import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.honeywell.android.rfidemcounting.R;
import com.honeywell.android.rfidemcounting.bean.EmBean;

import java.util.List;

import io.realm.Realm;

public class ExportEmAdapter extends BaseQuickAdapter<EmBean, BaseViewHolder> {
    private Realm realm;
    public ExportEmAdapter(int layoutResId, @Nullable List data) {
        super(layoutResId, data);
        Realm.init(Realm.getApplicationContext());
        realm=Realm.getDefaultInstance();
    }
    @Override
    protected void convert(BaseViewHolder helper, EmBean item) {
        int i=helper.getLayoutPosition();
        realm.beginTransaction();
        item.setSelected(false);
        realm.commitTransaction();
        helper.setText(R.id.index,String.valueOf(i+1))
                .setText(R.id.name, item.getName())
                .setText(R.id.state, String.valueOf(item.getState()));
       // Realm.getDefaultInstance().beginTransaction();
        CheckBox checkBox=(CheckBox)helper.getView(R.id.sel);
        checkBox.setChecked(item.isSelected());
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm.beginTransaction();
                item.setSelected(!item.isSelected());
                realm.commitTransaction();
               // notifyDataSetChanged();
               // Realm.getDefaultInstance().commitTransaction();
            }
        });
    }


}
