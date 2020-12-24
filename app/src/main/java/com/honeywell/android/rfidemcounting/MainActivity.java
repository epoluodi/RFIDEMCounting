package com.honeywell.android.rfidemcounting;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.honeywell.android.data.model.User;
import com.honeywell.android.data.utils.Transform;
import com.honeywell.android.rfidemcounting.adapter.FunTypeAdapter;
import com.honeywell.android.rfidemcounting.bean.EmBean;
import com.honeywell.android.rfidemcounting.bean.FunctionType;
import com.honeywell.android.rfidemcounting.utils.CommonUtil;
import com.leon.lfilepickerlibrary.LFilePicker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import butterknife.BindView;
import io.realm.Realm;

/**
 * Created by apple on 17/9/29.
 */

public class MainActivity extends BaseActivity {
    private long mExitTime = 0;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    private FunTypeAdapter mAdapter;
    private static int REQUESTCODE_FROM_ACTIVITY = 1000;
    private List<String> pathList;
    private String user_name;
    Dialog loadingDialog;
    private Realm realm;
    private ArrayList<FunctionType> mList = new ArrayList<>();


    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_main2;
    }



    @Override
    public void initView() {
        super.initView();
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        View v = LayoutInflater.from(this).inflate(R.layout.top_iv,null);
        FunctionType functionType=new FunctionType();
        functionType.des="盘点";
        functionType.img=R.mipmap.p1;
        FunctionType functionType1=new FunctionType();
        functionType1.des="设置";
        functionType1.img=R.mipmap.p4;
        FunctionType functionType2=new FunctionType();
        functionType2.des="导入";
        functionType2.img=R.mipmap.p2;
        FunctionType functionType3=new FunctionType();
        functionType3.des="导出";
        functionType3.img=R.mipmap.p3;
        FunctionType functionType4=new FunctionType();
        functionType4.des="数据采集";
        functionType4.img=R.mipmap.p5;
        FunctionType functionType5=new FunctionType();
        functionType5.des="关于";
        functionType5.img=R.mipmap.p6;
        mList.add(functionType);
        mList.add(functionType2);
        mList.add(functionType3);
        mList.add(functionType1);
        mList.add(functionType4);
        mList.add(functionType5);
        mAdapter = new FunTypeAdapter(R.layout.item_iv_tv1,mList);
        mAdapter.addHeaderView(v);
        recyclerView.setAdapter(mAdapter);
    }


    @Override
    public void initData() {
        Realm.init(getApplicationContext());
        realm=Realm.getDefaultInstance();
        user_name=MyApplication.user.getUserName();
    }

    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() - mExitTime > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    public void initTitle() {
        tv_center_title.setVisibility(View.VISIBLE);
        tv_center_title.setText("首页");
    }

    @Override
    public void setListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (position) {
                    case 0:
                        Intent intent = new Intent(MainActivity.this, EMListActivity.class);
                        startActivity(intent);
                        CommonUtil.openNewActivityAnim(MainActivity.this, false);
                        break;
                    case 3:


                        Intent intent1 = new Intent(MainActivity.this, SettingActivity.class);
                        startActivityForResult(intent1,1);
                        CommonUtil.openNewActivityAnim(MainActivity.this, false);
                        break;
                    case 1:
                        String filePath = Environment.getExternalStorageDirectory() +"/import";
                        new LFilePicker()
                                .withActivity(MainActivity.this)
                                .withRequestCode(REQUESTCODE_FROM_ACTIVITY)
                                .withStartPath(filePath)
                                .withFileFilter( new String[]{".txt"})
                                .withBackgroundColor("#24C4E2")
                                .start();

                        break;
                    case 2:
                        if (!aboutActivity.checkLicense())
                        {
                            Toast.makeText(MainActivity.this,
                                    "未授权，不可使用导出功能", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent3 = new Intent(MainActivity.this, ExportEmActivity.class);
                        startActivity(intent3);
                        CommonUtil.openNewActivityAnim(MainActivity.this, false);
                        break;
                    case 4:
                        Intent intent4 = new Intent(MainActivity.this, InitRFIDActivity.class);
                        startActivity(intent4);
                        CommonUtil.openNewActivityAnim(MainActivity.this, false);
                        break;
                    case 5:
                        Intent intent5 = new Intent(MainActivity.this, aboutActivity.class);
                        startActivity(intent5);
                        CommonUtil.openNewActivityAnim(MainActivity.this, false);
                        break;
                }
            }
        });
    }
    //导入


    private class importFile extends AsyncTask<List<String>,Void,List<EmBean>> {
        @Override
        protected List<EmBean> doInBackground(List<String>... path) {
            try {

                pathList=path[0];
                List<EmBean> emLists=new ArrayList<>();
                for (int i=0;i<path[0].size();i++) {
                    String dirpath = path[0].get(i).toString();
                    String filename = dirpath.substring(dirpath.lastIndexOf("/") + 1, dirpath.lastIndexOf("."));
                    File file = new File(dirpath);
                    InputStream txt = new FileInputStream(file);
                    final EmBean emList = Transform.importTxtToRealm(user_name, filename, txt);
                    emLists.add(emList);
                }
                return emLists;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog=   new ProgressDialog(MainActivity.this);
            loadingDialog.setTitle("正在导入任务");
            loadingDialog.setCancelable(false);
            loadingDialog.show();

        }

        @Override
        protected void onPostExecute(List<EmBean> emList) {
            super.onPostExecute(emList);

            realm.beginTransaction();
            realm.insert(emList);
            realm.commitTransaction();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
           /* for (int i=0;i<pathList.size();i++){
                File file=new File(pathList.get(i));
                file.delete();
            }*/
            loadingDialog.dismiss();
            Intent intent = new Intent(MainActivity.this, EMListActivity.class);
            startActivity(intent);
            CommonUtil.openNewActivityAnim(MainActivity.this, true);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE_FROM_ACTIVITY) {
                List<String> list = data.getStringArrayListExtra("paths");
                // String path =list.get(0);
                new importFile().execute(list);
            }
        }else if (resultCode ==1)
        {
            //退出登录
            Intent intent=new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
