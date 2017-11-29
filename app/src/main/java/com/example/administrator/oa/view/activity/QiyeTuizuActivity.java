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
 * Created by Administrator on 2017/7/7.
 */

public class QiyeTuizuActivity extends HeadBaseActivity {
    @BindView(R.id.companyName)
    TextView mCompanyName;
    @BindView(R.id.ll_bumen)
    LinearLayout mLlBumen;
    @BindView(R.id.bianhao)
    TextView mBianhao;
    @BindView(R.id.Oldaddress)
    EditText mOldaddress;
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
        return R.layout.activity_qiye_tuizu;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("企业退租申请");
        initThisView();
    }

    private void initThisView() {
        mSessionId = SPUtils.getString(this, "sessionId");
        mUserName = SPUtils.getString(this, "userName");
        mUserId = SPUtils.getString(this, "userId");
        mDepartmentId = SPUtils.getString(this, "departmentId");
        mDepartmentName = SPUtils.getString(this, "departmentName");
        processDefinitionId = getIntent().getStringExtra("processDefinitionId");

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
                                mOldaddress.setText(value);
                                break;
                            case "content":
                                mContent.setText(value);
                                break;
//                            case "investComment":
//                                mRencaibuyijian.setText(value);
//                                break;
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<QingjiaShenheResponse> response) {
                Toast.makeText(QiyeTuizuActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }


    // R.id.ll_fenguanleader,
    @OnClick({R.id.qytz_bianhao, R.id.btn_caogao, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.qytz_bianhao:
                if("0".equals(mBianhao.getTag())) {
                    mBianhao.setTag("1");
                    houQuFormCode();
                }
                break;
//            case R.id.ll_fenguanleader:
//                RequestServerGetZuzhi("请选择分管领导审核",mTvFenguanleader);
//                break;
            case R.id.btn_caogao:
                RequestServerGoodsLingqu_Save(mCompanyName.getText().toString().trim(),
                        mContent.getText().toString().trim(),
                        mBianhao.getText().toString().trim(),
                        mOldaddress.getText().toString().trim()
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
        if (TextUtils.isEmpty(mCompanyName.getText().toString().trim())) {
            Toast.makeText(this, "请填写企业名称", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mBianhao.getText().toString().trim())) {
            Toast.makeText(this, "请点击获取编号", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mOldaddress.getText().toString().trim())) {
            Toast.makeText(this, "请填写原办公地址", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(mContent.getText().toString().trim())) {
            Toast.makeText(this, "请填写内容", Toast.LENGTH_SHORT).show();
        }  else {
            RequestServerGoodsLingqu(mCompanyName.getText().toString().trim(),
                    mContent.getText().toString().trim(),
                    mBianhao.getText().toString().trim(),
                    mOldaddress.getText().toString().trim()
            );
        }

    }

    /**
     * 请求网络，提交数据b
     */
    private void RequestServerGoodsLingqu(String companyName, String content, String bianhao, String oldaddress) {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_STARTPROCESS,
                RequestMethod.POST, ProcessJieguoResponse.class);

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"company\":" + "\"" + companyName + "\",")
                .append("\"content\":" + "\"" + content + "\",")
                .append("\"id\":" + "\"" + bianhao + "\",")
                .append("\"address\":" + "\"" + oldaddress + "\"")
//                .append("\"address_name\":" + "\"" + mZuzhiUserBean.getName() + "\",")
//                .append("\"address\":" + "\"" + mZuzhiUserBean.getId() + "\"")
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
                        Toast.makeText(QiyeTuizuActivity.this, "流程发起成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(QiyeTuizuActivity.this, "流程发起失败", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(QiyeTuizuActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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
    private void RequestServerGoodsLingqu_Save(String companyName, String content, String bianhao, String oldaddress) {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_SAVEDRAFT,
                RequestMethod.POST, ProcessJieguoResponse.class);

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"company\":" + "\"" + companyName + "\",")
                .append("\"content\":" + "\"" + content + "\",")
                .append("\"id\":" + "\"" + bianhao + "\",")
                .append("\"address\":" + "\"" + oldaddress + "\"")
//                .append("\"address_name\":" + "\"" + mZuzhiUserBean.getName() + "\",")
//                .append("\"address\":" + "\"" + mZuzhiUserBean.getId() + "\"")
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
                        Toast.makeText(QiyeTuizuActivity.this, "已保存至草稿箱", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(QiyeTuizuActivity.this, "保存草稿失败", Toast.LENGTH_SHORT).show();
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
