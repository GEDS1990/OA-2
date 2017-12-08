package com.example.administrator.oa.view.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.example.administrator.oa.view.bean.ProcessJieguoResponse;
import com.example.administrator.oa.view.bean.QingjiaShenheBean;
import com.example.administrator.oa.view.bean.QingjiaShenheResponse;
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
 * Created by Administrator on 2017/7/6.
 */

public class YongcheActivity extends HeadBaseActivity {

    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.bumen)
    TextView mBumen;
    @BindView(R.id.ll_bumen)
    LinearLayout mLlBumen;
    @BindView(R.id.date_start)
    TextView mDateStart;
    @BindView(R.id.ll_start)
    LinearLayout mLlStart;
    @BindView(R.id.date_stop)
    TextView mDateStop;
    @BindView(R.id.ll_stop)
    LinearLayout mLlStop;
    @BindView(R.id.shifouyongche)
    TextView mShifouyongche;
    @BindView(R.id.ll_yongche)
    LinearLayout mLlYongche;
    @BindView(R.id.total_time)
    EditText mTotalTime;
    @BindView(R.id.car_type)
    EditText mCarType;
    @BindView(R.id.shenqing_yuanyin)
    EditText mShenqingYuanyin;
    @BindView(R.id.btn_caogao)
    Button mBtnCaogao;
    @BindView(R.id.btn_commit)
    Button mBtnCommit;
    private String mSessionId;
    private String mUserName;
    private String mDepartmentName;
    private String mDepartmentId;
    private String mUserId;
    private String processDefinitionId = "";
    private String businessKey = "";
    @Override
    protected int getChildLayoutRes() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return R.layout.activity_yongche;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("用车申请");
        initThisView();
    }

    private void initThisView() {
        mSessionId = SPUtils.getString(this, "sessionId");
        mUserId = SPUtils.getString(this, "userId");
        mUserName = SPUtils.getString(this, "userName");
        mDepartmentName = SPUtils.getString(this, "departmentName");
        mDepartmentId = SPUtils.getString(this, "departmentId");
        processDefinitionId = getIntent().getStringExtra("processDefinitionId");
        businessKey = getIntent().getStringExtra("businessKey");
        mName.setText(mUserName);
        mBumen.setText(mDepartmentName);

        mDateStart.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mDateStart.getText().toString().compareTo(mDateStop.getText().toString()) > 0) {
                    mDateStop.setText("");
                }
            }
        });
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
                    for (QingjiaShenheBean bean : shenheBeen) {
                        if(!TextUtils.isEmpty(bean.getName()) && !TextUtils.isEmpty(bean.getValue())) {
                            Log.d("Caogao", bean.getName());
                            Log.d("Caogao", bean.getValue());
                            //当有type为userpicker的时候说明是可以发起会签的节点
                            String label = bean.getName();
                            String value = bean.getValue();
                            switch (label) {
                                case "number":
                                    mTotalTime.setText(value);
                                    break;
                                case "type":
                                    mCarType.setText(value);
                                    break;
                                case "startTime":
                                    mDateStart.setText(value);
                                    break;
                                case "endTime":
                                    mDateStop.setText(value);
                                    break;
                                case "reason":
                                    mShenqingYuanyin.setText(value);
                                    break;
                                case "department":
                                    mShifouyongche.setText(value);
                                    break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<QingjiaShenheResponse> response) {
                Toast.makeText(YongcheActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }

    @OnClick({R.id.ll_start, R.id.ll_stop, R.id.ll_yongche, R.id.btn_caogao, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_start:
                selectDate(mDateStart, "");
                break;
            case R.id.ll_stop:
                selectDate(mDateStop, mDateStart.getText().toString());
                break;
            case R.id.ll_yongche:
                List<String> datas_yonche = new ArrayList();
                datas_yonche.add("是");
                datas_yonche.add("否");
                chooseDate(datas_yonche, mShifouyongche, "是否使用本部车辆？");
                break;
            case R.id.btn_caogao:
                RequestServer_Save(mDateStart.getText().toString().trim(),
                        mDateStop.getText().toString().trim(),
                        mShifouyongche.getText().toString().trim(),
                        mTotalTime.getText().toString().trim(),
                        mCarType.getText().toString().trim(),
                        mShenqingYuanyin.getText().toString().trim()
                );
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

        if (TextUtils.isEmpty(mUserId)) {
            Toast.makeText(this, "申请人缺失", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mBumen.getText().toString().trim())) {
            Toast.makeText(this, "申请部门缺失", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mDateStart.getText().toString().trim())) {
            Toast.makeText(this, "请选择开始时间", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mDateStop.getText().toString().trim())) {
            Toast.makeText(this, "请选择结束时间", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mShifouyongche.getText().toString().trim())) {
            Toast.makeText(this, "请选择是否使用本部车辆", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mTotalTime.getText().toString().trim())) {
            Toast.makeText(this, "请填写使用时间", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mCarType.getText().toString().trim())) {
            Toast.makeText(this, "请填写车辆类型", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mShenqingYuanyin.getText().toString().trim())) {
            Toast.makeText(this, "请填写申请事由", Toast.LENGTH_SHORT).show();
        } else {
            RequestServer(mDateStart.getText().toString().trim(),
                    mDateStop.getText().toString().trim(),
                    mShifouyongche.getText().toString().trim(),
                    mTotalTime.getText().toString().trim(),
                    mCarType.getText().toString().trim(),
                    mShenqingYuanyin.getText().toString().trim()
            );
        }

    }

    /**
     * 请求您网络，提交数据
     */
    private void RequestServer(String start, String stop, String isbenbu, String time, String cartype, String reason) {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_STARTPROCESS,
                RequestMethod.POST,ProcessJieguoResponse.class);

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"departments\":" + "\"" + mDepartmentName + "\",")
                .append("\"departments_id\":" + "\"" + mDepartmentId + "\",")
                .append("\"businessKey\":" + "\"" + businessKey + "\",")
                .append("\"name\":" + "\"" + mUserName + "\",")
                .append("\"startTime\":" + "\"" + start + "\",")
                .append("\"endTime\":" + "\"" + stop + "\",")
                .append("\"department\":" + "\"" + isbenbu + "\",")
                .append("\"number\":" + "\"" + time + "\",")
                .append("\"reason\":" + "\"" + reason + "\",")
                .append("\"type\":" + "\"" + cartype + "\"")
                .append("}");

        //添加url?key=value形式的参数
        request.addHeader("sessionId", mSessionId);
        request.add("processDefinitionId", processDefinitionId);
        request.add("businessKey", businessKey);
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
                        Toast.makeText(YongcheActivity.this, "流程发起成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(YongcheActivity.this, "流程发起失败", Toast.LENGTH_SHORT).show();
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
    private void RequestServer_Save(String start, String stop, String isbenbu, String time, String cartype, String reason) {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_SAVEDRAFT,
                RequestMethod.POST,ProcessJieguoResponse.class);

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"departments\":" + "\"" + mDepartmentName + "\",")
                .append("\"departments_id\":" + "\"" + mDepartmentId + "\",")
                .append("\"businessKey\":" + "\"" + businessKey + "\",")
                .append("\"name\":" + "\"" + mUserName + "\",")
                .append("\"startTime\":" + "\"" + start + "\",")
                .append("\"endTime\":" + "\"" + stop + "\",")
                .append("\"department\":" + "\"" + isbenbu + "\",")
                .append("\"number\":" + "\"" + time + "\",")
                .append("\"reason\":" + "\"" + reason + "\",")
                .append("\"type\":" + "\"" + cartype + "\"")
                .append("}");

        //添加url?key=value形式的参数
        request.addHeader("sessionId", mSessionId);
        request.add("processDefinitionId", processDefinitionId);
        request.add("businessKey", businessKey);
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
                        Toast.makeText(YongcheActivity.this, "已保存至草稿箱", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(YongcheActivity.this, "保存草稿失败", Toast.LENGTH_SHORT).show();
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
