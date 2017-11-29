package com.example.administrator.oa.view.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.bean.FormBianmaBean;
import com.example.administrator.oa.view.bean.ProcessJieguoResponse;
import com.example.administrator.oa.view.bean.QingjiaShenheBean;
import com.example.administrator.oa.view.bean.QingjiaShenheResponse;
import com.example.administrator.oa.view.bean.ZijinDiaoboBean;
import com.example.administrator.oa.view.constance.UrlConstance;
import com.example.administrator.oa.view.net.JavaBeanRequest;
import com.example.administrator.oa.view.utils.SPUtils;
import com.lsh.XXRecyclerview.CommonRecyclerAdapter;
import com.lsh.XXRecyclerview.CommonViewHolder;
import com.lsh.XXRecyclerview.XXRecycleView;
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

public class ZijinActivity extends HeadBaseActivity {


    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.bumen)
    TextView mBumen;
    @BindView(R.id.bianhao)
    TextView mBianhao;
//    @BindView(R.id.tv_fenguanleader)
//    TextView mTvFenguanleader;
//    @BindView(R.id.ll_fenguanleader)
//    LinearLayout mLlFenguanleader;
    @BindView(R.id.payAccount)
    EditText mPayAccount;
    @BindView(R.id.shouKuanAccount)
    EditText mShouKuanAccount;
    @BindView(R.id.money)
    EditText mMoney;
    @BindView(R.id.add)
    TextView mAdd;
    @BindView(R.id.xxre)
    XXRecycleView mXxre;
    @BindView(R.id.btn_caogao)
    Button mBtnCaogao;
    @BindView(R.id.btn_commit)
    Button mBtnCommit;
    private CommonRecyclerAdapter<ZijinDiaoboBean> mAdapter;
    private List<ZijinDiaoboBean> datas = new ArrayList<>();
    private String mSessionId;
    private String mUserName;
    private String mUserId;
    private String mDepartmentId;
    private String mDepartmentName;
    private String processDefinitionId;

    @Override
    protected int getChildLayoutRes() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return R.layout.activity_zijingdiaofa;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("资金调发");
        initThisView();
    }

    private void initThisView() {
        mSessionId = SPUtils.getString(this, "sessionId");
        mUserName = SPUtils.getString(this, "userName");
        mUserId = SPUtils.getString(this, "userId");
        mDepartmentId = SPUtils.getString(this, "departmentId");
        mDepartmentName = SPUtils.getString(this, "departmentName");
        processDefinitionId = getIntent().getStringExtra("processDefinitionId");
        mName.setText(mUserName);
        mBumen.setText(mDepartmentName);


        mXxre.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CommonRecyclerAdapter<ZijinDiaoboBean>(this, datas, R.layout.item_zijing_diaobo) {
            @Override
            public void convert(CommonViewHolder holder, final ZijinDiaoboBean item, int position, boolean b) {

                holder.setText(R.id.number, "( " + (position + 1) + " )");
                holder.setText(R.id.payAccount, item.getFukuanAccount());
                holder.setText(R.id.shouKuanAccount, item.getShoukuanAccount());
                holder.setText(R.id.money, item.getMoney());

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
        String businessKey = getIntent().getStringExtra("businessKey");
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

                    //按顺序填写数据
//                    mName.setText(shenheBeen.get(0).getValue());
//                    mBumen.setText(shenheBeen.get(1).getValue());
                    for (QingjiaShenheBean bean : shenheBeen) {
                        Log.d("Caogao", bean.getLabel());
                        Log.d("Caogao", bean.getValue());
                        //当有type为userpicker的时候说明是可以发起会签的节点
                        String label = bean.getLabel();
                        String value = bean.getValue();
                        switch (label) {
                            case "id":
                                mBianhao.setText(value);
                                break;
//                            case "address":
//                                mAddress.setText(value);
//                                break;
//                            case "startTime":
//                                mDateStart.setText(value);
//                                break;
//                            case "endTime":
//                                mDateStop.setText(value);
//                                break;
                        }
                    }

                    //pay，income，money
                    ArrayList<QingjiaShenheBean> pay = new ArrayList<>();
                    ArrayList<QingjiaShenheBean> income = new ArrayList<>();
                    ArrayList<QingjiaShenheBean> money = new ArrayList<>();

                    for (QingjiaShenheBean bean : shenheBeen) {
                        if (bean.getLabel().startsWith("pay")) {
                            pay.add(bean);
                        }
                        if (bean.getLabel().startsWith("income")) {
                            income.add(bean);
                        }
                        if (bean.getLabel().startsWith("money")) {
                            money.add(bean);
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<QingjiaShenheResponse> response) {
                Toast.makeText(ZijinActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }

    // , R.id.ll_fenguanleader,
    @OnClick({R.id.zjdb_bianhao, R.id.add, R.id.btn_caogao, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.zjdb_bianhao:
                if("0".equals(mBianhao.getTag())) {
                    mBianhao.setTag("1");
                    houQuFormCode();
                }
                break;
            case R.id.add:
                String pay = mPayAccount.getText().toString().trim();
                String income = mShouKuanAccount.getText().toString().trim();
                String money = mMoney.getText().toString().trim();
                if (TextUtils.isEmpty(pay)) {
                    Toast.makeText(this, "请输入付款账号", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(income)) {
                    Toast.makeText(this, "请输入到账账号", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(money)) {
                    Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
                } else {
                    mAdapter.add(new ZijinDiaoboBean(pay, income, money));
                    mXxre.scrollToPosition(mAdapter.getItemCount() - 1);
                    mPayAccount.setText("");
                    mShouKuanAccount.setText("");
                    mMoney.setText("");
                }
                break;
            case R.id.btn_caogao:
                RequestServerGoodsLingqu_Save(mBianhao.getText().toString().trim());
                break;
            case R.id.btn_commit:
                jianYanshuju();
                break;
//            case R.id.ll_fenguanleader:
//                RequestServerGetZuzhi("请选择分管领导", mTvFenguanleader);
//                break;
        }
    }

    /**
     * 填写的数据进行校验
     */
    private void jianYanshuju() {

        if (TextUtils.isEmpty(mUserId)) {
            Toast.makeText(this, "经办人缺失", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mBumen.getText().toString().trim())) {
            Toast.makeText(this, "申请部门缺失", Toast.LENGTH_SHORT).show();
//        } else if (TextUtils.isEmpty(mTvFenguanleader.getText().toString().trim())) {
//            Toast.makeText(this, "请选择分管领导", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mBianhao.getText().toString().trim())) {
            Toast.makeText(this, "请点击获取编号", Toast.LENGTH_SHORT).show();
        } else if (mAdapter.getDatas().size() < 1) {
            Toast.makeText(this, "请填写资金调拨明细单，并确认添加", Toast.LENGTH_SHORT).show();
        } else {
            RequestServerGoodsLingqu(mBianhao.getText().toString().trim());
        }

    }

    /**
     * 请求您网络，提交数据
     */
    private void RequestServerGoodsLingqu(String bianhao) {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_STARTPROCESS,
                RequestMethod.POST, ProcessJieguoResponse.class);

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"departments_name\":" + "\"" + mDepartmentName + "\",")
                .append("\"departments\":" + "\"" + mDepartmentId + "\",")
                .append("\"transactor\":" + "\"" + mUserName + "\",")
                .append("\"id\":" + "\"" + bianhao + "\",");
//                .append("\"manager_name\":" + "\"" + mZuzhiUserBean.getName() + "\",")
//                .append("\"manager\":" + "\"" + mZuzhiUserBean.getId() + "\",");

        for (int i = 0; i < 10; i++) {
            if (i <= mAdapter.getDatas().size() - 1) {
                ZijinDiaoboBean bean = mAdapter.getDatas().get(i);
                json.append("\"pay" + (i + 1) + "\":" + "\"" + bean.getFukuanAccount() + "\",")
                        .append("\"income" + (i + 1) + "\":" + "\"" + bean.getShoukuanAccount() + "\",")
                        .append("\"money" + (i + 1) + "\":" + "\"" + bean.getMoney() + "\",");
            } else {
                json.append("\"pay" + (i + 1) + "\":" + "\"\",")
                        .append("\"income" + (i + 1) + "\":" + "\"\",")
                        .append("\"money" + (i + 1) + "\":" + "\"\",");
            }
        }

        json.deleteCharAt(json.length() - 1);
        json.append("}");

        //添加url?key=value形式的参数
        request.addHeader("sessionId", mSessionId);
        request.add("processDefinitionId", processDefinitionId);
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
                        Toast.makeText(ZijinActivity.this, "流程发起成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(ZijinActivity.this, "流程发起失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.show();
                }
            }
        });
    }

    /**
     * 请求网络接口 获取编号
     */
    private void houQuFormCode() {

        //创建请求队列
        RequestQueue requestQueue = NoHttp.newRequestQueue();
        //创建请求
        Request<FormBianmaBean> request = new JavaBeanRequest<>(UrlConstance.URL_BIAODAN_CODE, RequestMethod.POST, FormBianmaBean.class);
        //添加url?key=value形式的参数
        request.add("typecode", "htbh");
        requestQueue.add(0, request, new OnResponseListener<FormBianmaBean>() {

            @Override
            public void onStart(int what) {
            }

            @Override
            public void onSucceed(int what, Response<FormBianmaBean> response) {
                Log.w("2222", response.toString());
                if (null != response && null != response.get() && null != response.get().getData()) {
                    mBianhao.setText(response.get().getData().getCode());
                } else {
                    mBianhao.setTag("0");
                }
            }

            @Override
            public void onFailed(int what, Response<FormBianmaBean> response) {
                Toast.makeText(ZijinActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
                mBianhao.setTag("0");
            }

            @Override
            public void onFinish(int what) {

            }
        });

    }

    /**
     * 请求您网络，提交数据
     */
    private void RequestServerGoodsLingqu_Save(String bianhao) {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_SAVEDRAFT,
                RequestMethod.POST, ProcessJieguoResponse.class);

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"departments_name\":" + "\"" + mDepartmentName + "\",")
                .append("\"departments\":" + "\"" + mDepartmentId + "\",")
                .append("\"transactor\":" + "\"" + mUserName + "\",")
                .append("\"id\":" + "\"" + bianhao + "\",");
//                .append("\"manager_name\":" + "\"" + mZuzhiUserBean.getName() + "\",")
//                .append("\"manager\":" + "\"" + mZuzhiUserBean.getId() + "\",");

        for (int i = 0; i < 10; i++) {
            if (i <= mAdapter.getDatas().size() - 1) {
                ZijinDiaoboBean bean = mAdapter.getDatas().get(i);
                json.append("\"pay" + (i + 1) + "\":" + "\"" + bean.getFukuanAccount() + "\",")
                        .append("\"income" + (i + 1) + "\":" + "\"" + bean.getShoukuanAccount() + "\",")
                        .append("\"money" + (i + 1) + "\":" + "\"" + bean.getMoney() + "\",");
            } else {
                json.append("\"pay" + (i + 1) + "\":" + "\"\",")
                        .append("\"income" + (i + 1) + "\":" + "\"\",")
                        .append("\"money" + (i + 1) + "\":" + "\"\",");
            }
        }

        json.deleteCharAt(json.length() - 1);
        json.append("}");

        //添加url?key=value形式的参数
        request.addHeader("sessionId", mSessionId);
        request.add("processDefinitionId", processDefinitionId);
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
                        Toast.makeText(ZijinActivity.this, "已保存至草稿箱", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(ZijinActivity.this, "保存草稿失败", Toast.LENGTH_SHORT).show();
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
