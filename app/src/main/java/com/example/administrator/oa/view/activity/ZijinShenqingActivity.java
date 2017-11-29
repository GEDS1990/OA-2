package com.example.administrator.oa.view.activity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.bean.FormBianmaBean;
import com.example.administrator.oa.view.bean.ProcessJieguoResponse;
import com.example.administrator.oa.view.constance.UrlConstance;
import com.example.administrator.oa.view.net.JavaBeanRequest;
import com.example.administrator.oa.view.utils.SPUtils;
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
 * Created by Administrator on 2017/7/11.
 */

public class ZijinShenqingActivity extends HeadBaseActivity {
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.bumen)
    TextView mBumen;
    @BindView(R.id.bianhao)
    TextView mBianhao;
    @BindView(R.id.payType)
    TextView mPayType;
    @BindView(R.id.ll_payType)
    LinearLayout mLlPayType;
    @BindView(R.id.hetongname)
    TextView mHetongname;
    @BindView(R.id.ll_hetongname)
    LinearLayout mLlHetongname;
    @BindView(R.id.money)
    EditText mMoney;
    @BindView(R.id.reason)
    EditText mReason;
    @BindView(R.id.content)
    EditText mContent;
//    @BindView(R.id.tv_fenguanleader)
//    TextView mTvFenguanleader;
//    @BindView(R.id.ll_fenguanleader)
//    LinearLayout mLlFenguanleader;
    @BindView(R.id.btn_caogao)
    Button mBtnCaogao;
    @BindView(R.id.btn_commit)
    Button mBtnCommit;
    private String mSessionId;
    private String mUserName;
    private String mUserId;
    private String mDepartmentId;
    private String mDepartmentName;
    private String processDefinitionId;

    @Override
    protected int getChildLayoutRes() {
        return R.layout.activity_zijing_shenqing_hetong;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("资金申请单");
        initThisView();
    }

    private void initThisView() {
        mSessionId = SPUtils.getString(this, "sessionId");
        mUserName = SPUtils.getString(this, "userName");
        mUserId = SPUtils.getString(this, "userId");
        mDepartmentId = SPUtils.getString(this, "departmentId");
        mDepartmentName = SPUtils.getString(this, "departmentName");
        processDefinitionId = getIntent().getStringExtra("processDefinitionId");
        mBumen.setText(mDepartmentName);
        mName.setText(mUserName);

    }

    // , R.id.ll_fenguanleader
    @OnClick({R.id.zjsq_bianhao, R.id.ll_payType, R.id.ll_hetongname, R.id.btn_caogao, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.zjsq_bianhao:
                houQuFormCode();
                break;
            case R.id.ll_payType:
                List<String> datas_fanshi = new ArrayList();
                datas_fanshi.add("固定总价合同");
                datas_fanshi.add("固定单价合同");
                chooseDate(datas_fanshi, mPayType, "选择支付类别");
                break;
            case R.id.ll_hetongname:
                Toast.makeText(this, "后台数据待调整，暂时可不选择", Toast.LENGTH_SHORT).show();
                break;
//            case R.id.ll_fenguanleader:
//                RequestServerGetZuzhi("请选择分管领导",mTvFenguanleader);
//                break;
            case R.id.btn_caogao:
                break;
            case R.id.btn_commit:
                jianYanshuju();
                break;
        }
    }

    /**
     * 填写的数据进行校验
     */
    private void jianYanshuju() {
        if (TextUtils.isEmpty(mName.getText().toString().trim())) {
            Toast.makeText(this, "经办人姓名缺失", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mBumen.getText().toString().trim())) {
            Toast.makeText(this, "部门信息缺失", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mBianhao.getText().toString().trim())) {
            Toast.makeText(this, "合同编号信息缺失", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mPayType.getText().toString().trim())) {
            Toast.makeText(this, "请选择支付类型", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mMoney.getText().toString().trim())) {
            Toast.makeText(this, "请填写申请金额", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mReason.getText().toString().trim())) {
            Toast.makeText(this, "请选择申请事由", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mContent.getText().toString().trim())) {
            Toast.makeText(this, "请填写申请内容", Toast.LENGTH_SHORT).show();
//        } else if (TextUtils.isEmpty(mTvFenguanleader.getText().toString().trim())) {
//            Toast.makeText(this, "请选择分管领导审核", Toast.LENGTH_SHORT).show();
        } else {
            RequestServerGoodsLingqu(
                    mBianhao.getText().toString().trim(),
                    mPayType.getText().toString().trim(),
                    mMoney.getText().toString().trim(),
                    mReason.getText().toString().trim(),
                    mContent.getText().toString().trim(),"临时合同名称"
            );
        }

    }

    /**
     * 请求网络，提交数据b
     */
    private void RequestServerGoodsLingqu(String bianhao, String paytype, String money, String reason
            , String content,String hetongname) {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_STARTPROCESS,
                RequestMethod.POST, ProcessJieguoResponse.class);

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"payment\":" + "\"" + paytype + "\",")
                .append("\"id\":" + "\"" + bianhao + "\",")
                .append("\"departments_name\":" + "\"" + mDepartmentName + "\",")
                .append("\"departments\":" + "\"" + mDepartmentId + "\",")
                .append("\"transactor_name\":" + "\"" + mUserName + "\",")
                .append("\"transactor\":" + "\"" + mUserId + "\",")
                .append("\"contract\":" + "\"" + hetongname + "\",")
                .append("\"money\":" + "\"" + money + "\",")
                .append("\"content\":" + "\"" + content + "\",")
                .append("\"reason\":" + "\"" + reason + "\",")
//                .append("\"manager_name\":" + "\"" + mZuzhiUserBean.getName() + "\",")
//                .append("\"manager\":" + "\"" + mZuzhiUserBean.getId() + "\"")
                .append("}");
        //添加url?key=value形式的参数
        request.addHeader("sessionId", mSessionId);
        request.add("processDefinitionId", processDefinitionId);
        request.add("data", json.toString());
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
                        Toast.makeText(ZijinShenqingActivity.this, "流程发起成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(ZijinShenqingActivity.this, "流程发起失败", Toast.LENGTH_SHORT).show();
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
     * 请求网络接口 获取编号
     */
    private void houQuFormCode() {

        //创建请求队列
        RequestQueue requestQueue = NoHttp.newRequestQueue();
        //创建请求
        Request<FormBianmaBean> request = new JavaBeanRequest<>(UrlConstance.URL_BIAODAN_CODE, RequestMethod.POST, FormBianmaBean.class);
        //添加url?key=value形式的参数
        request.add("typecode", "zjsq");
        requestQueue.add(0, request, new OnResponseListener<FormBianmaBean>() {

            @Override
            public void onStart(int what) {
            }

            @Override
            public void onSucceed(int what, Response<FormBianmaBean> response) {
                Log.w("2222", response.toString());
                if (null != response && null != response.get() && null != response.get().getData()) {
                    mBianhao.setText(response.get().getData().getCode());
                }
            }

            @Override
            public void onFailed(int what, Response<FormBianmaBean> response) {
                Toast.makeText(ZijinShenqingActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {

            }
        });

    }
}
