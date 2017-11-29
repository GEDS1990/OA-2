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
import com.example.administrator.oa.view.bean.ZuzhiUserBean;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/7/4.
 */

public class ChuChaiActivity extends HeadBaseActivity {
    @BindView(R.id.chuchai_name)
    TextView mChuchaiName;
    @BindView(R.id.chuchai_bumen)
    TextView mChuchaiBumen;
    @BindView(R.id.ll_bumen)
    LinearLayout mLlBumen;
    @BindView(R.id.chuchai_gongju)
    TextView mChuchaiGongju;
    @BindView(R.id.ll_gongju)
    LinearLayout mLlGongju;
    @BindView(R.id.chuchai_start)
    TextView mChuchaiStart;
    @BindView(R.id.ll_start)
    LinearLayout mLlStart;
    @BindView(R.id.chuchai_stop)
    TextView mChuchaiStop;
    @BindView(R.id.ll_stop)
    LinearLayout mLlStop;
    @BindView(R.id.chuchai_didian)
    EditText mChuchaiDidian;
    @BindView(R.id.chuchai_shiyou)
    EditText mChuchaiShiyou;
    @BindView(R.id.chuchai_beizhu)
    EditText mChuchaiBeizhu;
    @BindView(R.id.btn_caogao)
    Button mBtnCaogao;
    @BindView(R.id.btn_commit)
    Button mBtnCommit;
//    @BindView(R.id.tv_fenguanleader)
//    TextView mTvFenguanleader;
//    @BindView(R.id.ll_fenguanleader)
//    LinearLayout mLlFenguanleader;
//    @BindView(R.id.tv_buzhang)
//    TextView mTvBuzhang;
//    @BindView(R.id.ll_buzhang)
//    LinearLayout mLlBuzhang;
    private String mDepartmentName;
    private String mUserName;
    private String processDefinitionId;
    private String mDepartmentId;
    private String mUserId;

    @Override
    protected int getChildLayoutRes() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return R.layout.activity_chuchai;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("出差申请");
        initThisView();
    }

    private void initThisView() {
        mUserName = SPUtils.getString(this, "userName");
        mUserId = SPUtils.getString(this, "userId");
        mDepartmentName = SPUtils.getString(this, "departmentName");
        mDepartmentId = SPUtils.getString(this, "departmentId");
        processDefinitionId = getIntent().getStringExtra("processDefinitionId");
        mChuchaiName.setText(mUserName);
        mChuchaiBumen.setText(mDepartmentName);

        mChuchaiStart.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mChuchaiStart.getText().toString().compareTo(mChuchaiStop.getText().toString()) > 0) {
                    mChuchaiStop.setText("");
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
                        Log.d("Caogao", bean.getLabel());
                        Log.d("Caogao", bean.getValue());
                        //当有type为userpicker的时候说明是可以发起会签的节点
                        String label = bean.getLabel();
                        String value = bean.getValue();
                        switch (label) {
                            case "reason":
                                mChuchaiShiyou.setText(value);
                                break;
                            case "address":
                                mChuchaiDidian.setText(value);
                                break;
                            case "startTime":
                                mChuchaiStart.setText(value);
                                break;
                            case "endTime":
                                mChuchaiStop.setText(value);
                                break;
                            case "traffic":
                                mChuchaiGongju.setText(value);
                                break;
                            case "other":
                                mChuchaiBeizhu.setText(value);
                                break;
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<QingjiaShenheResponse> response) {
                Toast.makeText(ChuChaiActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }

    // , R.id.ll_fenguanleader, R.id.ll_buzhang
    @OnClick({R.id.ll_gongju, R.id.ll_start, R.id.ll_stop, R.id.btn_caogao, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_gongju:
                List<String> datas_gongju = new ArrayList();
                datas_gongju.add("飞机");
                datas_gongju.add("火车");
                datas_gongju.add("汽车");
                chooseDate(datas_gongju, mChuchaiGongju, "交通工具");
                break;
            case R.id.ll_start:
                selectDate(mChuchaiStart, "");
                break;
            case R.id.ll_stop:
                selectDate(mChuchaiStop, mChuchaiStart.getText().toString());
                break;
            case R.id.btn_caogao:
                RequestServerQingjia_Save(mChuchaiGongju.getText().toString().trim(),
                        mChuchaiStart.getText().toString().trim(),
                        mChuchaiStop.getText().toString().trim(),
                        mChuchaiDidian.getText().toString().trim(),
                        mChuchaiShiyou.getText().toString().trim(),
                        mChuchaiBeizhu.getText().toString().trim()
//                    mTvBuzhang.getText().toString().trim()
                );
                break;
            case R.id.btn_commit:
                jianYanshuju();
                break;
//            case R.id.ll_fenguanleader:
//                RequestServerGetZuzhi("请选择分管领导",mTvFenguanleader);
//                break;
//            case R.id.ll_buzhang:
//                List<String> datas_buzhang = new ArrayList();
//                datas_buzhang.add("是");
//                datas_buzhang.add("否");
//                chooseDate(datas_buzhang, mTvBuzhang, "是否需要部长审核");
//                break;
        }
    }

    /**
     * 表单填写校验
     */
    private void jianYanshuju() {

        if (TextUtils.isEmpty(mChuchaiName.getText().toString().trim())) {
            Toast.makeText(this, "申请人缺失", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mChuchaiBumen.getText().toString().trim())) {
            Toast.makeText(this, "申请部门缺失", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mChuchaiGongju.getText().toString().trim())) {
            Toast.makeText(this, "请选择交通工具", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mChuchaiStart.getText().toString().trim())) {
            Toast.makeText(this, "请选择开始时间", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mChuchaiStop.getText().toString().trim())) {
            Toast.makeText(this, "请选择结束时间", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mChuchaiDidian.getText().toString().trim())) {
            Toast.makeText(this, "请填写出差地点", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mChuchaiShiyou.getText().toString().trim())) {
            Toast.makeText(this, "请填写出差事由", Toast.LENGTH_SHORT).show();
//        } else if (TextUtils.isEmpty(mTvFenguanleader.getText().toString().trim())) {
//            Toast.makeText(this, "请选择领导审核", Toast.LENGTH_SHORT).show();
//        } else if (TextUtils.isEmpty(mTvBuzhang.getText().toString().trim())) {
//            Toast.makeText(this, "请选择是否需要部长审核", Toast.LENGTH_SHORT).show();
        } else {
            RequestServerQingjia(mChuchaiGongju.getText().toString().trim(),
                    mChuchaiStart.getText().toString().trim(),
                    mChuchaiStop.getText().toString().trim(),
                    mChuchaiDidian.getText().toString().trim(),
                    mChuchaiShiyou.getText().toString().trim(),
                    mChuchaiBeizhu.getText().toString().trim()
//                    mTvBuzhang.getText().toString().trim()
            );
        }
    }

    /**
     * 请求网络接口
     */
    // , String comment
    private void RequestServerQingjia(String gongju, String start, String stop, String adress, String reason,
                                      String beizhu) {

//        if (mZuzhiUserBean == null) {
//            Toast.makeText(this, "请选择分管领导审核", Toast.LENGTH_SHORT).show();
//            return;
//        }

        String sessionId = SPUtils.getString(this, "sessionId");
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_STARTPROCESS,
                RequestMethod.POST, ProcessJieguoResponse.class);
        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"departments_name\":" + "\"" + mDepartmentName + "\",")
                .append("\"departments\":" + "\"" + mDepartmentId + "\",")
                .append("\"name\":" + "\"" + mUserName + "\",")
                .append("\"traffic\":" + "\"" + gongju + "\",")
                .append("\"startTime\":" + "\"" + start + "\",")
                .append("\"endTime\":" + "\"" + stop + "\",")
                .append("\"address\":" + "\"" + adress + "\",")
                .append("\"reason\":" + "\"" + reason + "\",")
                .append("\"beizhu\":" + "\"" + beizhu + "\"")
//                .append("\"manager\":" + "\"" + mZuzhiUserBean.getId() + "\",")
//                .append("\"manager_name\":" + "\"" + mZuzhiUserBean.getName() + "\",")
//                .append("\"comment\":" + "\"" + comment + "\"")
                .append("}");

        //添加url?key=value形式的参数
        request.addHeader("sessionId", sessionId);
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
                Log.w("2222", response.toString());
                if (null != response && null != response.get() && null != response.get().getData()) {
                    if (response.get().getCode() == 200) {
                        Toast.makeText(ChuChaiActivity.this, "流程发起成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(ChuChaiActivity.this, "流程发起失败", Toast.LENGTH_SHORT).show();
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
    // , String comment
    private void RequestServerQingjia_Save(String gongju, String start, String stop, String adress, String reason,
                                      String beizhu) {

//        if (mZuzhiUserBean == null) {
//            Toast.makeText(this, "请选择分管领导审核", Toast.LENGTH_SHORT).show();
//            return;
//        }

        String sessionId = SPUtils.getString(this, "sessionId");
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_SAVEDRAFT,
                RequestMethod.POST, ProcessJieguoResponse.class);
        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"departments_name\":" + "\"" + mDepartmentName + "\",")
                .append("\"departments\":" + "\"" + mDepartmentId + "\",")
                .append("\"name\":" + "\"" + mUserName + "\",")
                .append("\"traffic\":" + "\"" + gongju + "\",")
                .append("\"startTime\":" + "\"" + start + "\",")
                .append("\"endTime\":" + "\"" + stop + "\",")
                .append("\"address\":" + "\"" + adress + "\",")
                .append("\"reason\":" + "\"" + reason + "\",")
                .append("\"beizhu\":" + "\"" + beizhu + "\"")
//                .append("\"manager\":" + "\"" + mZuzhiUserBean.getId() + "\",")
//                .append("\"manager_name\":" + "\"" + mZuzhiUserBean.getName() + "\",")
//                .append("\"comment\":" + "\"" + comment + "\"")
                .append("}");

        //添加url?key=value形式的参数
        request.addHeader("sessionId", sessionId);
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
                Log.w("2222", response.toString());
                if (null != response && null != response.get()) {
                    if (response.get().getCode() == 200) {
                        Toast.makeText(ChuChaiActivity.this, "已保存至草稿箱", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(ChuChaiActivity.this, "保存草稿失败", Toast.LENGTH_SHORT).show();
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
