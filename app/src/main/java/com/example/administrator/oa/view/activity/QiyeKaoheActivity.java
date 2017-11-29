package com.example.administrator.oa.view.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/7/11.
 */

public class QiyeKaoheActivity extends HeadBaseActivity {
    @BindView(R.id.bianhao)
    TextView mBianhao;
    @BindView(R.id.date_start)
    TextView mDateStart;
    @BindView(R.id.ll_start)
    LinearLayout mLlStart;
    @BindView(R.id.date_stop)
    TextView mDateStop;
    @BindView(R.id.ll_stop)
    LinearLayout mLlStop;
    @BindView(R.id.companyName)
    EditText mCompanyName;
    @BindView(R.id.address)
    EditText mAddress;
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
        return R.layout.activity_qiye_kaohe;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("企业考核表");
        initThisView();
    }

    private void initThisView() {
        mSessionId = SPUtils.getString(this, "sessionId");
        mUserName = SPUtils.getString(this, "userName");
        mUserId = SPUtils.getString(this, "userId");
        mDepartmentId = SPUtils.getString(this, "departmentId");
        mDepartmentName = SPUtils.getString(this, "departmentName");
        processDefinitionId = getIntent().getStringExtra("processDefinitionId");
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

                    for (QingjiaShenheBean bean : shenheBeen) {
                        //当有type为userpicker的时候说明是可以发起会签的节点
                        String label = bean.getLabel();
                        String value = bean.getValue();
                        switch (label) {
                            case "company":
                                mCompanyName.setText(value);
                                break;
                            case "id":
                                mBianhao.setText(value);
                                break;
                            case "address":
                                mAddress.setText(value);
                                break;
                            case "startTime":
                                mDateStart.setText(value);
                                break;
                            case "endTime":
                                mDateStop.setText(value);
                                break;
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<QingjiaShenheResponse> response) {
                Toast.makeText(QiyeKaoheActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }


    @OnClick({R.id.kaohe_bianhao,R.id.ll_start, R.id.ll_stop, R.id.btn_caogao, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.kaohe_bianhao:
                if("0".equals(mBianhao.getTag())) {
                    mBianhao.setTag("1");
                    houQuFormCode();
                }
                break;
            case R.id.ll_start:
                selectDate(mDateStart, "");
                break;
            case R.id.ll_stop:
                selectDate(mDateStop, mDateStart.getText().toString());
                break;
            case R.id.btn_caogao:
                RequestServerGoodsLingqu_Save(mCompanyName.getText().toString().trim(),
                        mDateStart.getText().toString().trim(),
                        mDateStop.getText().toString().trim(),
                        mBianhao.getText().toString().trim(),
                        mAddress.getText().toString().trim()
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
        if (TextUtils.isEmpty(mBianhao.getText().toString().trim())) {
            Toast.makeText(this, "请点击获取编号", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mDateStart.getText().toString().trim())) {
            Toast.makeText(this, "请选择开始时间", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mDateStop.getText().toString().trim())) {
            Toast.makeText(this, "请选择截止时间", Toast.LENGTH_SHORT).show();
        }  else if (TextUtils.isEmpty(mCompanyName.getText().toString().trim())) {
            Toast.makeText(this, "请填写企业名称", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mAddress.getText().toString().trim())) {
            Toast.makeText(this, "请填写租赁地址", Toast.LENGTH_SHORT).show();
        } else {
            RequestServerGoodsLingqu(mCompanyName.getText().toString().trim(),
                    mDateStart.getText().toString().trim(),
                    mDateStop.getText().toString().trim(),
                    mBianhao.getText().toString().trim(),
                    mAddress.getText().toString().trim()
            );
        }

    }

    /**
     * 请求网络，提交数据b
     */
    private void RequestServerGoodsLingqu(String companyName, String dateStart, String dateStop, String bianhao, String address) {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_STARTPROCESS,
                RequestMethod.POST, ProcessJieguoResponse.class);

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"company\":" + "\"" + companyName + "\",")
                .append("\"startTime\":" + "\"" + dateStart + "\",")
                .append("\"endTime\":" + "\"" + dateStop + "\",")
                .append("\"id\":" + "\"" + bianhao + "\",")
                .append("\"address\":" + "\"" + address + "\"")
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
                if (response != null && response.get() != null) {
                    if (response.get().getCode() == 200) {
                        Toast.makeText(QiyeKaoheActivity.this, "流程发起成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(QiyeKaoheActivity.this, "流程发起失败", Toast.LENGTH_SHORT).show();
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
        request.add("typecode", "qykh");
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
                Toast.makeText(QiyeKaoheActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
                mBianhao.setTag("0");
            }

            @Override
            public void onFinish(int what) {

            }
        });

    }

    /**
     * 保存草稿
     */
    private void RequestServerGoodsLingqu_Save(String companyName, String dateStart, String dateStop, String bianhao, String address) {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_SAVEDRAFT,
                RequestMethod.POST, ProcessJieguoResponse.class);

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"company\":" + "\"" + companyName + "\",")
                .append("\"startTime\":" + "\"" + dateStart + "\",")
                .append("\"endTime\":" + "\"" + dateStop + "\",")
                .append("\"id\":" + "\"" + bianhao + "\",")
                .append("\"address\":" + "\"" + address + "\"")
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
                if (response != null && response.get() != null) {
                    if (response.get().getCode() == 200) {
                        Toast.makeText(QiyeKaoheActivity.this, "已保存至草稿箱", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(QiyeKaoheActivity.this, "保存草稿失败", Toast.LENGTH_SHORT).show();
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
