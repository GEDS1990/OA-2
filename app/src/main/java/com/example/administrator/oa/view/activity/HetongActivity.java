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

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/7/6.
 */

public class HetongActivity extends HeadBaseActivity {
    @BindView(R.id.hetongName)
    EditText mHetongName;
    @BindView(R.id.bumen)
    TextView mBumen;
    @BindView(R.id.bianhao)
    TextView mBianhao;
    @BindView(R.id.zhuti_jia)
    EditText mZhutiJia;
    @BindView(R.id.zhuti_yi)
    EditText mZhutiYi;
    @BindView(R.id.zhuti_bin)
    EditText mZhutiBin;
    @BindView(R.id.zhuti_ding)
    EditText mZhutiDing;
    @BindView(R.id.canyu_jia)
    EditText mCanyuJia;
    @BindView(R.id.canyu_yi)
    EditText mCanyuYi;
    @BindView(R.id.canyu_bin)
    EditText mCanyuBin;
    @BindView(R.id.canyu_ding)
    EditText mCanyuDing;
    @BindView(R.id.textView2)
    TextView mTextView2;
    @BindView(R.id.date)
    TextView mDate;
    @BindView(R.id.ll_start)
    LinearLayout mLlStart;
    @BindView(R.id.address)
    EditText mAddress;
    @BindView(R.id.qiatan_jinzhan)
    EditText mQiatanJinzhan;
    @BindView(R.id.main_content)
    EditText mMainContent;
    @BindView(R.id.btn_caogao)
    Button mBtnCaogao;
    @BindView(R.id.btn_commit)
    Button mBtnCommit;
//    @BindView(R.id.tv_fenguanleader)
//    TextView mTvFenguanleader;
//    @BindView(R.id.ll_fenguanleader)
//    LinearLayout mLlFenguanleader;
    private String mSessionId;
    private String mUserName;
    private String mUserId;
    private String mDepartmentId;
    private String mDepartmentName;
    private String processDefinitionId;

    @Override
    protected int getChildLayoutRes() {
        return R.layout.activity_hetong;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("合同流程");
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
        houQuFormCode();
    }

    // , R.id.ll_fenguanleader
    @OnClick({R.id.ll_start, R.id.btn_caogao, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_start:
                selectDate("", mDate, "");
                break;
            case R.id.btn_caogao:
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
        if (TextUtils.isEmpty(mBumen.getText().toString().trim())) {
            Toast.makeText(this, "主办部门信息缺失", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mDate.getText().toString().trim())) {
            Toast.makeText(this, "请选择合同日期", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mHetongName.getText().toString().trim())) {
            Toast.makeText(this, "请填写合同名称", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mBianhao.getText().toString().trim())) {
            Toast.makeText(this, "合同编号缺失", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mAddress.getText().toString().trim())) {
            Toast.makeText(this, "请填写合同地点", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mQiatanJinzhan.getText().toString().trim())) {
            Toast.makeText(this, "请填写合同进展", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mZhutiJia.getText().toString().trim()) || TextUtils.isEmpty(mZhutiYi.getText().toString().trim())) {
            Toast.makeText(this, "请填写合同主体人员", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mCanyuJia.getText().toString().trim()) || TextUtils.isEmpty(mCanyuYi.getText().toString().trim())) {
            Toast.makeText(this, "请填写参与洽谈人员", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mMainContent.getText().toString().trim())) {
            Toast.makeText(this, "请填合同主要内容", Toast.LENGTH_SHORT).show();
        } else {
            RequestServerGoodsLingqu(mBumen.getText().toString().trim(),
                    mDate.getText().toString().trim(),
                    mHetongName.getText().toString().trim(),
                    mBianhao.getText().toString().trim(),
                    mAddress.getText().toString().trim(),
                    mQiatanJinzhan.getText().toString().trim(),

                    mZhutiJia.getText().toString().trim(),
                    mZhutiYi.getText().toString().trim(),
                    mZhutiBin.getText().toString().trim(),
                    mZhutiDing.getText().toString().trim(),

                    mCanyuJia.getText().toString().trim(),
                    mCanyuYi.getText().toString().trim(),
                    mCanyuBin.getText().toString().trim(),
                    mCanyuDing.getText().toString().trim(),

                    mMainContent.getText().toString().trim()
            );
        }

    }

    /**
     * 请求网络，提交数据
     */
    private void RequestServerGoodsLingqu(String bumen, String date, String HetongName,
                                          String Bianhao, String Address, String QiatanJinzhan,
                                          String zhutiJia,
                                          String zhutiYi,
                                          String zhutiBin,
                                          String zhutiDing,
                                          String canyuJia,
                                          String vanyuYi,
                                          String canyuBin,
                                          String canyuDing,
                                          String mainContent
    ) {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_STARTPROCESS,
                RequestMethod.POST, ProcessJieguoResponse.class);

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"departments_name\":" + "\"" + bumen + "\",")
                .append("\"departments\":" + "\"" + mDepartmentId + "\",")
                .append("\"title\":" + "\"" + HetongName + "\",")
                .append("\"id\":" + "\"" + Bianhao + "\",")
                .append("\"name1\":" + "\"" + zhutiJia + "\",")
                .append("\"name2\":" + "\"" + zhutiYi + "\",")
                .append("\"name3\":" + "\"" + zhutiBin + "\",")
                .append("\"name4\":" + "\"" + zhutiDing + "\",")

                .append("\"people1\":" + "\"" + canyuJia + "\",")
                .append("\"people2\":" + "\"" + vanyuYi + "\",")
                .append("\"people3\":" + "\"" + canyuBin + "\",")
                .append("\"people4\":" + "\"" + canyuDing + "\",")

                .append("\"situation\":" + "\"" + QiatanJinzhan + "\",")
                .append("\"content\":" + "\"" + mainContent + "\",")
                .append("\"address\":" + "\"" + Address + "\",")
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
                        Toast.makeText(HetongActivity.this, "流程发起成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(HetongActivity.this, "流程发起失败", Toast.LENGTH_SHORT).show();
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
                }
            }

            @Override
            public void onFailed(int what, Response<FormBianmaBean> response) {
                Toast.makeText(HetongActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {

            }
        });

    }
}
