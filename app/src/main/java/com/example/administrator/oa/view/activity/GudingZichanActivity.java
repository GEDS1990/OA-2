package com.example.administrator.oa.view.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.bean.GoodsRegistrationBean;
import com.example.administrator.oa.view.bean.ProcessJieguoResponse;
import com.example.administrator.oa.view.bean.QingjiaShenheBean;
import com.example.administrator.oa.view.bean.QingjiaShenheResponse;
import com.example.administrator.oa.view.bean.ZuzhiUserBean;
import com.example.administrator.oa.view.bean.ZuzhiUserListResponse;
import com.example.administrator.oa.view.bean.organization_structure.ChildrenBean;
import com.example.administrator.oa.view.bean.organization_structure.OrganizationResponse;
import com.example.administrator.oa.view.constance.UrlConstance;
import com.example.administrator.oa.view.net.JavaBeanRequest;
import com.example.administrator.oa.view.utils.SPUtils;
import com.lsh.XXRecyclerview.CommonRecyclerAdapter;
import com.lsh.XXRecyclerview.CommonViewHolder;
import com.lsh.XXRecyclerview.XXRecycleView;
import com.luoshihai.xxdialog.DialogViewHolder;
import com.luoshihai.xxdialog.XXDialog;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/7/6.
 */

public class GudingZichanActivity extends HeadBaseActivity {

    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.bumen)
    TextView mBumen;
    @BindView(R.id.date)
    TextView mDate;
    @BindView(R.id.ll_start)
    LinearLayout mLlStart;
    @BindView(R.id.xxre)
    XXRecycleView mXxre;
    @BindView(R.id.btn_caogao)
    Button mBtnCaogao;
    @BindView(R.id.btn_commit)
    Button mBtnCommit;
    @BindView(R.id.add)
    TextView mAdd;
    @BindView(R.id.goodsName)
    EditText mGoodsName;
    @BindView(R.id.wuping_guige)
    EditText mWupingGuige;
    @BindView(R.id.wuping_count)
    EditText mWupingCount;
    @BindView(R.id.beizhu)
    EditText mBeizhu;
//    @BindView(R.id.tv_buzhang)
//    TextView mTvBuzhang;
//    @BindView(R.id.ll_buzhang)
//    LinearLayout mLlBuzhang;
//    @BindView(R.id.tv_fenguanleader)
//    TextView mTvFenguanleader;
//    @BindView(R.id.ll_fenguanleader)
//    LinearLayout mLlFenguanleader;
    @BindView(R.id.tv_bumenleader)
    TextView mTvBumenleader;
    @BindView(R.id.ll_bumenleader)
    LinearLayout mLlBumenleader;
    private CommonRecyclerAdapter<GoodsRegistrationBean> mAdapter;
    private List<GoodsRegistrationBean> datas = new ArrayList<>();
    private String mSessionId;
    private String mUserName;
    private String mDepartmentName;
    private String mUserId;
    private String mDepartmentId;

    private String processDefinitionId = "";
    private String businessKey = "";

    @Override
    protected int getChildLayoutRes() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return R.layout.activity_gudingzichan;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("固定资产领用");
        initThisView();
    }

    private void initThisView() {
        mSessionId = SPUtils.getString(this, "sessionId");
        mUserName = SPUtils.getString(this, "userName");
        mUserId = SPUtils.getString(this, "userId");
        mDepartmentId = SPUtils.getString(this, "departmentId");
        mDepartmentName = SPUtils.getString(this, "departmentName");
        processDefinitionId = getIntent().getStringExtra("processDefinitionId");
        businessKey = getIntent().getStringExtra("businessKey");
        mName.setText(mUserName);
        mBumen.setText(mDepartmentName);

        Log.w("5555", "mDepartmentId=" + mDepartmentId);

        mXxre.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CommonRecyclerAdapter<GoodsRegistrationBean>(this, datas, R.layout.item_wupin_lingqu) {
            @Override
            public void convert(CommonViewHolder holder, final GoodsRegistrationBean item, int position, boolean b) {

                holder.setText(R.id.number, "( " + (position + 1) + " )");
                holder.setText(R.id.name, item.getGoods());
                holder.setText(R.id.wuping_guige, item.getFormat());
                holder.setText(R.id.wuping_count, item.getNum());
                holder.setText(R.id.beizhu, item.getRemarks());

                holder.getView(R.id.delet).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAdapter.remove(item);
                    }
                });
            }
        };
        mXxre.setAdapter(mAdapter);

        checkFormCaoGao();
    }

    /**
     * 检测是否是从草稿箱界面跳转过来
     */
    private void checkFormCaoGao(){
        if(!TextUtils.isEmpty(businessKey)){
            // 获取草稿信息
            RequestServerGetInfo(businessKey);
        }
    }

    /**
     * 获取草稿信息
     */
    private void RequestServerGetInfo(String businessKey) {
        String sessionId = SPUtils.getString(this, "sessionId");
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<QingjiaShenheResponse> request = new JavaBeanRequest<>(UrlConstance.URL_CAOGAOXIANG_INFO,
                RequestMethod.POST, QingjiaShenheResponse.class);
        //添加url?key=value形式的参数
        request.addHeader("sessionId", sessionId);
        request.add("processDefinitionId", processDefinitionId);
        request.add("businessKey", businessKey);
        Queue.add(0, request, new OnResponseListener<QingjiaShenheResponse>() {

            @Override
            public void onStart(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<QingjiaShenheResponse> response) {
                if (null != response && null != response.get() && null != response.get().getData()) {
                    List<QingjiaShenheBean> shenheBeen = response.get().getData();
                    ArrayList<String> goods = new ArrayList<>();
                    ArrayList<String> format = new ArrayList<>();
                    ArrayList<String> num = new ArrayList<>();
                    ArrayList<String> remarks = new ArrayList<>();
                    for (QingjiaShenheBean bean : shenheBeen) {
                        if(!TextUtils.isEmpty(bean.getName()) && !TextUtils.isEmpty(bean.getValue())) {
                            Log.d("Caogao", bean.getName());
                            Log.d("Caogao", bean.getValue());
                            //当有type为userpicker的时候说明是可以发起会签的节点
                            String label = bean.getName();
                            String value = bean.getValue();
                            switch (label) {
                                // TODO 负责人
                                case "minister":
                                    mTvBumenleader.setTag(value);
                                    mTvBumenleader.setText(bean.getLabel());
                                    break;
                                case "date":
                                    mDate.setText(value);
                                    break;
                            }
                        }
                        // 处理物品明细
//                        if(!TextUtils.isEmpty(bean.getLabel()) && !TextUtils.isEmpty(bean.getValue())) {
//                            if (bean.getLabel().startsWith("goods") ) {
//                                goods.add((Integer.parseInt(bean.getLabel().replace("goods",""))-1), bean.getValue());
//                            }
//                            if (bean.getLabel().startsWith("format")) {
//                                format.add((Integer.parseInt(bean.getLabel().replace("format",""))-1), bean.getValue());
//                            }
//                            if (bean.getLabel().startsWith("num")) {
//                                num.add((Integer.parseInt(bean.getLabel().replace("num",""))-1), bean.getValue());
//                            }
//                            if (bean.getLabel().startsWith("remarks")) {
//                                remarks.add((Integer.parseInt(bean.getLabel().replace("remarks",""))-1), bean.getValue());
//                            }
//                        }
                        if(!TextUtils.isEmpty(bean.getLabel()) && !TextUtils.isEmpty(bean.getValue())) {
                            if (bean.getLabel().startsWith("goods") ) {
                                goods.add(bean.getValue());
                            }
                            if (bean.getLabel().startsWith("format")) {
                                format.add(bean.getValue());
                            }
                            if (bean.getLabel().startsWith("num")) {
                                num.add(bean.getValue());
                            }
                            if (bean.getLabel().startsWith("remarks")) {
                                remarks.add(bean.getValue());
                            }
                        }
                    }
                    // 把放入list里的物品明细添加进adapter里，展示出来
                    for (int i = 0;i< goods.size();i++) {
                        if(remarks.size() < goods.size()){
                            remarks.add(i, "");
                        }
                        mAdapter.add(new GoodsRegistrationBean(goods.get(i), format.get(i), num.get(i), remarks.get(i)));
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<QingjiaShenheResponse> response) {
                Toast.makeText(GudingZichanActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }


    // R.id.ll_buzhang, R.id.ll_fenguanleader,
    @OnClick({R.id.ll_start, R.id.btn_caogao, R.id.btn_commit, R.id.add, R.id.ll_bumenleader})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_start:
                selectDate(mDate, "");
                break;
            case R.id.btn_caogao:
                RequestServerGoodsLingqu_Save(mDate.getText().toString().trim(),
//                    mTvFenguanleader.getText().toString().trim(),
//                    mTvBuzhang.getText().toString().trim(),
                        mTvBumenleader.getText().toString().trim()
                );
                break;
            case R.id.btn_commit:
                jianYanshuju();
                break;
            case R.id.add:
                String name = mGoodsName.getText().toString().trim();
                String wuping_guige = mWupingGuige.getText().toString().trim();
                String wuping_count = mWupingCount.getText().toString().trim();
                String beizhu = mBeizhu.getText().toString().trim();
                if (mAdapter != null && mAdapter.getDatas().size() <= 10) {
                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(this, "请输入物品名称", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(wuping_guige)) {
                        Toast.makeText(this, "请输入物品规格", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(wuping_count)) {
                        Toast.makeText(this, "请输入物品数量", Toast.LENGTH_SHORT).show();
                    } else {
                        mAdapter.add(new GoodsRegistrationBean(name, wuping_guige, wuping_count, beizhu));
                        mXxre.scrollToPosition(mAdapter.getItemCount() - 1);
                        mGoodsName.setText("");
                        mWupingGuige.setText("");
                        mWupingCount.setText("");
                        mBeizhu.setText("");
                    }
                } else {
                    Toast.makeText(this, "最多只能添加10种物品", Toast.LENGTH_SHORT).show();
                }
                break;
//            case R.id.ll_buzhang:
//                List<String> datas_buzhang = new ArrayList();
//                datas_buzhang.add("是");
//                datas_buzhang.add("否");
//                chooseDate(datas_buzhang, mTvBuzhang, "是否需要部长审核");
//                break;
//            case R.id.ll_fenguanleader:
//                RequestServerLogin(mTvFenguanleader, "请选择分管领导");
//                break;
            case R.id.ll_bumenleader:
//                RequestServerLogin(mTvBumenleader, "请选择部门负责人");
                if("0".equals(mLlBumenleader.getTag())) {
                    mLlBumenleader.setTag("1");
                    RequestServerGetZuzhi(mLlBumenleader, mTvBumenleader, "请选择部门负责人", null);
                }
                break;
        }
    }

    /**
     * 填写的数据进行校验
     */
    private void jianYanshuju() {

        if (TextUtils.isEmpty(mUserId)) {
            Toast.makeText(this, "领用人缺失", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mBumen.getText().toString().trim())) {
            Toast.makeText(this, "部门缺失", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mDate.getText().toString().trim())) {
            Toast.makeText(this, "请选择领用时间", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mTvBumenleader.getText().toString().trim())) {
            Toast.makeText(this, "请选择部门负责人", Toast.LENGTH_SHORT).show();
        } else if (mAdapter.getDatas().size() < 1) {
            Toast.makeText(this, "请填写物品明细单，并确认添加", Toast.LENGTH_SHORT).show();
//        } else if (TextUtils.isEmpty(mTvBuzhang.getText().toString().trim())) {
//            Toast.makeText(this, "请选择是否需要部长审核", Toast.LENGTH_SHORT).show();
//        } else if (TextUtils.isEmpty(mTvFenguanleader.getText().toString().trim())) {
//            Toast.makeText(this, "请选择分管领导审核", Toast.LENGTH_SHORT).show();

        } else {
            RequestServerGoodsLingqu(mDate.getText().toString().trim(),
//                    mTvFenguanleader.getText().toString().trim(),
//                    mTvBuzhang.getText().toString().trim(),
                    mTvBumenleader.getText().toString().trim()
            );
        }

    }

    /**
     * 请求您网络，提交数据
     */
    //  String leadername,String comment,
    private void RequestServerGoodsLingqu(String time, String bumenLeadername) {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_STARTPROCESS,
                RequestMethod.POST, ProcessJieguoResponse.class);

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"departments\":" + "\"" + mDepartmentName + "\",")
                .append("\"departments_id\":" + "\"" + mDepartmentId + "\",")
                .append("\"businessKey\":" + "\"" + businessKey + "\",")
                .append("\"name\":" + "\"" + mUserName + "\",")
                .append("\"date\":" + "\"" + time + "\",")
                .append("\"minister_name\":" + "\"" + mTvBumenleader.getText().toString().trim() + "\",")
                .append("\"minister\":" + "\"" + mTvBumenleader.getTag().toString() + "\",");
//                .append("\"comment\":" + "\"" + comment + "\",");

        for (int i = 0; i < 10; i++) {
            if (i <= mAdapter.getDatas().size() - 1) {
                GoodsRegistrationBean bean = mAdapter.getDatas().get(i);
                json.append("\"goods" + (i + 1) + "\":" + "\"" + bean.getGoods() + "\",")
                        .append("\"format" + (i + 1) + "\":" + "\"" + bean.getFormat() + "\",")
                        .append("\"num" + (i + 1) + "\":" + "\"" + bean.getNum() + "\",")
                        .append("\"remarks" + (i + 1) + "\":" + "\"" + bean.getRemarks() + "\",");
            } else {
                json.append("\"goods" + (i + 1) + "\":" + "\"\",")
                        .append("\"format" + (i + 1) + "\":" + "\"\",")
                        .append("\"num" + (i + 1) + "\":" + "\"\",")
                        .append("\"remarks" + (i + 1) + "\":" + "\"\",");
            }
        }

        json.deleteCharAt(json.length() - 1);
        json.append("}");

        //添加url?key=value形式的参数
        request.addHeader("sessionId", mSessionId);
        request.add("processDefinitionId", processDefinitionId);
        request.add("businessKey", businessKey);
        request.add("data", json.toString());
        Log.w("99999", json.toString());
        Queue.add(0, request, new OnResponseListener<ProcessJieguoResponse>() {

            @Override
            public void onStart(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<ProcessJieguoResponse> response) {
                if (null != response && null != response.get()) {
                    if (response.get().getCode() == 200) {
                        Toast.makeText(GudingZichanActivity.this, "流程发起成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(GudingZichanActivity.this, "流程发起失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }

    /**
     * 保存草稿
     */
    //  String leadername,String comment,
    private void RequestServerGoodsLingqu_Save(String time, String bumenLeadername) {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_SAVEDRAFT,
                RequestMethod.POST, ProcessJieguoResponse.class);

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"departments\":" + "\"" + mDepartmentName + "\",")
                .append("\"departments_id\":" + "\"" + mDepartmentId + "\",")
                .append("\"businessKey\":" + "\"" + businessKey + "\",")
                .append("\"name\":" + "\"" + mUserName + "\",")
                .append("\"date\":" + "\"" + time + "\",")
                .append("\"minister_name\":" + "\"" + mTvBumenleader.getText().toString().trim() + "\",")
                .append("\"minister\":" + "\"" + mTvBumenleader.getTag().toString() + "\",");
//                .append("\"comment\":" + "\"" + comment + "\",");

        for (int i = 0; i < 10; i++) {
            if (i <= mAdapter.getDatas().size() - 1) {
                GoodsRegistrationBean bean = mAdapter.getDatas().get(i);
                json.append("\"goods" + (i + 1) + "\":" + "\"" + bean.getGoods() + "\",")
                        .append("\"format" + (i + 1) + "\":" + "\"" + bean.getFormat() + "\",")
                        .append("\"num" + (i + 1) + "\":" + "\"" + bean.getNum() + "\",")
                        .append("\"remarks" + (i + 1) + "\":" + "\"" + bean.getRemarks() + "\",");
            } else {
                json.append("\"goods" + (i + 1) + "\":" + "\"\",")
                        .append("\"format" + (i + 1) + "\":" + "\"\",")
                        .append("\"num" + (i + 1) + "\":" + "\"\",")
                        .append("\"remarks" + (i + 1) + "\":" + "\"\",");
            }
        }

        json.deleteCharAt(json.length() - 1);
        json.append("}");

        //添加url?key=value形式的参数
        request.addHeader("sessionId", mSessionId);
        request.add("processDefinitionId", processDefinitionId);
        request.add("businessKey", businessKey);
        request.add("data", json.toString());
        Log.w("99999", json.toString());
        Queue.add(0, request, new OnResponseListener<ProcessJieguoResponse>() {

            @Override
            public void onStart(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<ProcessJieguoResponse> response) {
                if (null != response && null != response.get()) {
                    if (response.get().getCode() == 200) {
                        Toast.makeText(GudingZichanActivity.this, "已保存至草稿箱", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(GudingZichanActivity.this, "保存草稿失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }
}
