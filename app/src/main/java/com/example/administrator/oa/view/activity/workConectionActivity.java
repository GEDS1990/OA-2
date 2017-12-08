package com.example.administrator.oa.view.activity;

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
import com.example.administrator.oa.view.constance.UrlConstance;
import com.example.administrator.oa.view.net.JavaBeanRequest;
import com.example.administrator.oa.view.utils.SPUtils;
import com.luoshihai.xxdialog.XXDialog;
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
 * Created by Administrator on 2017/7/5.
 */

public class workConectionActivity extends HeadBaseActivity {
    @BindView(R.id.bumen)
    TextView mBumen;
    @BindView(R.id.bianhao)
    TextView mBianhao;
    @BindView(R.id.xiangmu_fuzeren)
    TextView mXiangmuFuzeren;
    @BindView(R.id.ll_xiangmu)
    LinearLayout mLlXiangmu;
    @BindView(R.id.bumen_fuzeren)
    TextView mBumenFuzeren;
    @BindView(R.id.ll_bumen)
    LinearLayout mLlBumen;
    @BindView(R.id.lianxiren)
    TextView mLianxiren;
    @BindView(R.id.ll_worklianxiren)
    LinearLayout mLlWorklianxiren;
    @BindView(R.id.qingjia_shiyou)
    EditText mQingjiaShiyou;
    @BindView(R.id.btn_caogao)
    Button mBtnCaogao;
    @BindView(R.id.btn_commit)
    Button mBtnCommit;
//    @BindView(R.id.tv_fenguanleader)
//    TextView mTvFenguanleader;
//    @BindView(R.id.ll_fenguanleader)
//    LinearLayout mLlFenguanleader;
    private RequestQueue mQueue;
    XXDialog mxxDialog2;
    private XXDialog mxxUsersDialog;
//    private String mProjectId;
//    private String mBumenId;
//    private String mContactId;
    private String mLeaderId;
    private String mDepartmentId;
    private String processDefinitionId = "";
    private String businessKey = "";

    @Override
    protected int getChildLayoutRes() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return R.layout.activity_workconection;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("工作联系单");
        initThisView();
    }

    private void initThisView() {
        String departmentName = SPUtils.getString(this, "departmentName");
        mDepartmentId = SPUtils.getString(this,"departmentId");
        processDefinitionId = getIntent().getStringExtra("processDefinitionId");
        businessKey = getIntent().getStringExtra("businessKey");
        mBumen.setText(departmentName);
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
                        if(!TextUtils.isEmpty(bean.getName()) && !TextUtils.isEmpty(bean.getValue())) {
                            //当有type为userpicker的时候说明是可以发起会签的节点
                            String label = bean.getName();
                            String value = bean.getValue();
                            switch (label) {
                                case "id":
                                    if(!TextUtils.isEmpty(value)) {
                                        mBianhao.setText(value);
                                    }
                                    break;
                                case "reason":
                                    mQingjiaShiyou.setText(value);
                                    break;
                                case "project":
                                    mXiangmuFuzeren.setTag(value);
                                    mXiangmuFuzeren.setText(bean.getLabel());
                                    break;
                                case "minister":
                                    mBumenFuzeren.setTag(value);
                                    mBumenFuzeren.setText(bean.getLabel());
                                    break; 
                                case "contacts":
                                    mLianxiren.setText(bean.getLabel());
                                    mLianxiren.setTag(value);
                                    break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<QingjiaShenheResponse> response) {
                Toast.makeText(workConectionActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }

    // , R.id.ll_fenguanleader
    @OnClick({R.id.gzlx_bianhao, R.id.ll_xiangmu, R.id.ll_bumen, R.id.ll_worklianxiren, R.id.btn_caogao, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.gzlx_bianhao:
                if("0".equals(mBianhao.getTag())) {
                    mBianhao.setTag("1");
                    houQuFormCode();
                }
                break;
            case R.id.ll_xiangmu:
                if("0".equals(mLlXiangmu.getTag())) {
                    mLlXiangmu.setTag("1");
                    RequestServerGetZuzhi(mLlXiangmu, mXiangmuFuzeren, "请选择项目负责人", null);
                }
                break;
            case R.id.ll_bumen:
                if("0".equals(mLlBumen.getTag())) {
                    mLlBumen.setTag("1");
                    RequestServerGetZuzhi(mLlBumen, mBumenFuzeren, "请选择部门负责人", null);
                }
                break;
            case R.id.ll_worklianxiren:
                if("0".equals(mLlWorklianxiren.getTag())) {
                    mLlWorklianxiren.setTag("1");
                    RequestServerGetZuzhi(mLlWorklianxiren, mLianxiren, "请选择工作联系人", null);
                }
                break;
            case R.id.btn_caogao:
                RequestServerWorkConnection_Save(mBumen.getText().toString().trim(),
                        mBianhao.getText().toString().trim(),
                        mXiangmuFuzeren.getText().toString().trim(),
                        mBumenFuzeren.getText().toString().trim(),
                        mLianxiren.getText().toString().trim(),
                        mQingjiaShiyou.getText().toString().trim());
                break;
            case R.id.btn_commit:
                jianYanshuju();
                break;
//            case R.id.ll_fenguanleader:
//                if("0".equals(mTvFenguanleader.getTag())) {
//                    mTvFenguanleader.setTag("1");
//                    RequestServerLogin(mTvFenguanleader, "请选择分管领导");
//                }
//                break;
        }
    }

    /**
     * 表单填写校验
     */
    private void jianYanshuju() {
        if (TextUtils.isEmpty(mBumen.getText().toString().trim())) {
            Toast.makeText(this, "发起部门缺失", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mBianhao.getText().toString().trim())) {
            Toast.makeText(this, "请点击获取编号", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mXiangmuFuzeren.getText().toString().trim())) {
            Toast.makeText(this, "请选择项目负责人", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mBumenFuzeren.getText().toString().trim())) {
            Toast.makeText(this, "请选择部门负责人", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mLianxiren.getText().toString().trim())) {
            Toast.makeText(this, "请选择工作联系人", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mQingjiaShiyou.getText().toString().trim())) {
            Toast.makeText(this, "请填写发起事由", Toast.LENGTH_SHORT).show();
//        } else if (TextUtils.isEmpty(mTvFenguanleader.getText().toString().trim())) {
//            Toast.makeText(this, "请选择分管领导审核", Toast.LENGTH_SHORT).show();
        } else {
            RequestServerWorkConnection(mBumen.getText().toString().trim(),
                    mBianhao.getText().toString().trim(),
                    mXiangmuFuzeren.getText().toString().trim(),
                    mBumenFuzeren.getText().toString().trim(),
                    mLianxiren.getText().toString().trim(),
                    mQingjiaShiyou.getText().toString().trim());
//                    mTvFenguanleader.getText().toString().trim());
        }
    }

    /**
     * 提交表单数据
     *
     * @param bumen
     * @param bianhao
     * @param projectleader
     * @param bumenleader
     * @param contacts
     * @param reason
     */
    // , String manager
    private void RequestServerWorkConnection(String bumen, String bianhao, String projectleader,
                                             String bumenleader, String contacts, String reason) {
        String sessionId = SPUtils.getString(this, "sessionId");

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"departments\":" + "\"" + bumen + "\",")
                .append("\"departments_id\":" + "\"" + mDepartmentId + "\",")
                .append("\"businessKey\":" + "\"" + businessKey + "\",")
                .append("\"id\":" + "\"" + bianhao + "\",")
                .append("\"project_name\":" + "\"" + projectleader + "\",")
                .append("\"project\":" + "\"" + mXiangmuFuzeren.getTag().toString() + "\",")
                .append("\"minister_name\":" + "\"" + bumenleader + "\",")
                .append("\"minister\":" + "\"" + mBumenFuzeren.getTag().toString() + "\",")
                .append("\"contacts_name\":" + "\"" + contacts + "\",")
                .append("\"contacts\":" + "\"" + mLianxiren.getTag().toString() + "\",")
                .append("\"reason\":" + "\"" + reason + "\"")
//                .append("\"manager_name\":" + "\"" + manager + "\",")
//                .append("\"manager\":" + "\"" + mLeaderId + "\"")
                .append("}");

        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_STARTPROCESS,
                RequestMethod.POST, ProcessJieguoResponse.class);
        //添加url?key=value形式的参数
        request.addHeader("sessionId", sessionId);
        request.add("processDefinitionId", processDefinitionId);
        request.add("businessKey", businessKey);
        request.add("data", json.toString());
        Queue.add(0, request, new OnResponseListener<ProcessJieguoResponse>() {

            @Override
            public void onStart(int what) {
                if (mLoadingDialog!=null) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<ProcessJieguoResponse> response) {
                Log.w("workConectionActivity", "response:" + response);
                if (null != response && null != response.get()) {
                    if (response.get().getCode() == 200) {
                        Toast.makeText(workConectionActivity.this, "流程发起成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(workConectionActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(workConectionActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (mLoadingDialog!=null) {
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
        mQueue = NoHttp.newRequestQueue();
        //创建请求
        Request<FormBianmaBean> request = new JavaBeanRequest<>(UrlConstance.URL_BIAODAN_CODE, RequestMethod.POST, FormBianmaBean.class);
        //添加url?key=value形式的参数
        request.add("typecode", "gongzuo");
        mQueue.add(0, request, new OnResponseListener<FormBianmaBean>() {

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
                Toast.makeText(workConectionActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
                mBianhao.setTag("0");
            }

            @Override
            public void onFinish(int what) {

            }
        });
    }

    /**
     * 保存草稿
     *
     * @param bumen
     * @param bianhao
     * @param projectleader
     * @param bumenleader
     * @param contacts
     * @param reason
     */
    // , String manager
    private void RequestServerWorkConnection_Save(String bumen, String bianhao, String projectleader,
                                             String bumenleader, String contacts, String reason) {
        String sessionId = SPUtils.getString(this, "sessionId");

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"departments\":" + "\"" + bumen + "\",")
                .append("\"departments_id\":" + "\"" + mDepartmentId + "\",")
                .append("\"businessKey\":" + "\"" + businessKey + "\",")
                .append("\"id\":" + "\"" + bianhao + "\",")
                .append("\"project_name\":" + "\"" + projectleader + "\",")
                .append("\"project\":" + "\"" + mXiangmuFuzeren.getTag().toString() + "\",")
                .append("\"minister_name\":" + "\"" + bumenleader + "\",")
                .append("\"minister\":" + "\"" + mBumenFuzeren.getTag().toString() + "\",")
                .append("\"contacts_name\":" + "\"" + contacts + "\",")
                .append("\"contacts\":" + "\"" + mLianxiren.getTag().toString() + "\",")
                .append("\"reason\":" + "\"" + reason + "\"")
//                .append("\"manager_name\":" + "\"" + manager + "\",")
//                .append("\"manager\":" + "\"" + mLeaderId + "\"")
                .append("}");

        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_SAVEDRAFT,
                RequestMethod.POST, ProcessJieguoResponse.class);
        //添加url?key=value形式的参数
        request.addHeader("sessionId", sessionId);
        request.add("processDefinitionId", processDefinitionId);
        request.add("businessKey", businessKey);
        request.add("data", json.toString());
        Queue.add(0, request, new OnResponseListener<ProcessJieguoResponse>() {

            @Override
            public void onStart(int what) {
                if (mLoadingDialog!=null) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<ProcessJieguoResponse> response) {
                Log.w("workConectionActivity", "response:" + response);
                if (null != response && null != response.get()) {
                    if (response.get().getCode() == 200) {
                        Toast.makeText(workConectionActivity.this, "已保存至草稿箱", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(workConectionActivity.this, "保存草稿失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (mLoadingDialog!=null) {
                    mLoadingDialog.dismiss();
                }
            }
        });

    }
}