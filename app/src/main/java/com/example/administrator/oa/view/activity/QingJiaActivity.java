package com.example.administrator.oa.view.activity;

import android.support.v7.widget.LinearLayoutManager;
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
import com.example.administrator.oa.view.bean.ProcessShenheHistoryBean;
import com.example.administrator.oa.view.bean.ProcessShenheHistoryRes;
import com.example.administrator.oa.view.bean.QingjiaResponse;
import com.example.administrator.oa.view.bean.QingjiaShenheBean;
import com.example.administrator.oa.view.bean.QingjiaShenheResponse;
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
 * Created by Administrator on 2017/7/3.
 */

public class QingJiaActivity extends HeadBaseActivity {

    @BindView(R.id.qingjia_name)
    TextView mQingjiaName;
    @BindView(R.id.qingjia_bumen)
    TextView mQingjiaBumen;
    @BindView(R.id.ll_bumen)
    LinearLayout mLlBumen;
    @BindView(R.id.qingjia_type)
    TextView mQingjiaType;
    @BindView(R.id.ll_leixing)
    LinearLayout mLlLeixing;
    @BindView(R.id.qingjia_start)
    TextView mQingjiaStart;
    @BindView(R.id.ll_start)
    LinearLayout mLlStart;
    @BindView(R.id.qingjia_stop)
    TextView mQingjiaStop;
    @BindView(R.id.ll_stop)
    LinearLayout mLlStop;
    @BindView(R.id.qingjia_tianshu)
    EditText mQingjiaTianshu;
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
//    @BindView(R.id.tv_buzhang)
//    TextView mTvBuzhang;
//    @BindView(R.id.ll_buzhang)
//    LinearLayout mLlBuzhang;
    private String mUserName;
    private String mDepartmentName;
    private String mDepartmentId;
    // 流程审核记录
    @BindView(R.id.txtProcess)
    TextView txtProcess;
    @BindView(R.id.xxreProcess)
    XXRecycleView mProcessXxre;
    private CommonRecyclerAdapter<ProcessShenheHistoryBean> mAdapter;
    private List<ProcessShenheHistoryBean> datas = new ArrayList<>();
    private String mSessionId;
    // 审核信息
    private String formCode = "";
    private String mTaskId = "";
    // 草稿信息
    private String processDefinitionId = "";
    private String businessKey = "";

    @Override
    protected int getChildLayoutRes() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return R.layout.activity_qingjia;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("请假申请");
        initThisView();
    }

    private void initThisView() {
        mUserName = SPUtils.getString(this, "userName");
        mDepartmentName = SPUtils.getString(this, "departmentName");
        mDepartmentId = SPUtils.getString(this,"departmentId");
        processDefinitionId = getIntent().getStringExtra("processDefinitionId");
        mQingjiaName.setText(mUserName);
        mQingjiaBumen.setText(mDepartmentName);

        mSessionId = SPUtils.getString(this, "sessionId");
        formCode = getIntent().getStringExtra("formCode");
        // 如果formcode不是空的，则进入草稿
        if(!TextUtils.isEmpty(formCode)) {
            // 草稿
            if("caogao".equals(formCode)) {
                businessKey = getIntent().getStringExtra("businessKey");
                // 获取草稿信息或者是审核信息
                RequestServerGetInfoFromCaoGao();
            }
            // 否则就是审核的重新提交
            else {
                mTaskId = getIntent().getStringExtra("taskId");
                txtProcess.setVisibility(View.VISIBLE);
                mProcessXxre.setVisibility(View.VISIBLE);
                mBtnCaogao.setVisibility(View.GONE);
                mBtnCommit.setText("提交");
                RequestServerGetInfoFromShenHe();
                // 实现流程记录的adapter
                mProcessXxre.setLayoutManager(new LinearLayoutManager(this));
                mAdapter = new CommonRecyclerAdapter<ProcessShenheHistoryBean>(this, datas, R.layout.item_myprocess_shenhejilu) {
                    @Override
                    public void convert(CommonViewHolder holder, ProcessShenheHistoryBean item, int i, boolean b) {
                        holder.setText(R.id.processNameContent, item.getName());
                        holder.setText(R.id.name, item.getAssignee());
                        holder.setText(R.id.startTimeContent, item.getCreateTime());
                        holder.setText(R.id.completeTimeContent, item.getCompleteTime());
                    }
                };
                mProcessXxre.setAdapter(mAdapter);
            }
        }

        mQingjiaStart.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mQingjiaStart.getText().toString().compareTo(mQingjiaStop.getText().toString()) > 0) {
                    mQingjiaStop.setText("");
                }
            }
        });

    }

    /**
     * 获取草稿信息
     */
    private void RequestServerGetInfoFromCaoGao() {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<QingjiaShenheResponse> request = new JavaBeanRequest<>(UrlConstance.URL_CAOGAOXIANG_INFO,
                RequestMethod.POST, QingjiaShenheResponse.class);
        //添加url?key=value形式的参数
        request.addHeader("sessionId", mSessionId);
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
                                case "type":
                                    mQingjiaType.setText(value);
                                    break;
                                case "startTime":
                                    mQingjiaStart.setText(value);
                                    break;
                                case "endTime":
                                    mQingjiaStop.setText(value);
                                    break;
                                case "number":
                                    mQingjiaTianshu.setText(value);
                                    break;
                                case "reason":
                                    mQingjiaShiyou.setText(value);
                                    break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<QingjiaShenheResponse> response) {
                Toast.makeText(QingJiaActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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
     * 请求网络接口-流程审核记录
     */
    private void RequestServerGetInfoFromShenHe() {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //1-流程审核记录
        //创建请求
        Request<ProcessShenheHistoryRes> request = new JavaBeanRequest<>(UrlConstance.URL_GET_PROCESS_HESTORY,
                RequestMethod.POST, ProcessShenheHistoryRes.class);
        //添加url?key=value形式的参数
        request.addHeader("sessionId", mSessionId);
        request.add("taskId", mTaskId);
        Queue.add(1, request, new OnResponseListener<ProcessShenheHistoryRes>() {

            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<ProcessShenheHistoryRes> response) {
                Log.w("2222", response.toString());
                if (null != response && null != response.get() && null != response.get().getData()) {
                    List<ProcessShenheHistoryBean> data = response.get().getData();
                    if (mAdapter != null) {
                        mAdapter.replaceAll(data);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessShenheHistoryRes> response) {
                Toast.makeText(QingJiaActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {

            }
        });

        //2-拉取页面数据
        //创建请求
        Request<QingjiaShenheResponse> request2 = new JavaBeanRequest<>(UrlConstance.URL_GET_PROCESS_INIT,
                RequestMethod.POST, QingjiaShenheResponse.class);
        //添加url?key=value形式的参数
        request2.add("taskId", mTaskId);
        Queue.add(0, request2, new OnResponseListener<QingjiaShenheResponse>() {

            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<QingjiaShenheResponse> response) {
                Log.w("3333", response.toString());
                if (null != response && null != response.get() && null != response.get().getData()) {
                    List<QingjiaShenheBean> shenheBeen = response.get().getData();
                    for (QingjiaShenheBean bean : shenheBeen) {
                        if(!TextUtils.isEmpty(bean.getName()) && !TextUtils.isEmpty(bean.getValue())) {
                            //当有type为userpicker的时候说明是可以发起会签的节点
                            String label = bean.getName();
                            String value = bean.getValue();
                            switch (label) {
                                case "departments":
                                    mQingjiaBumen.setText(value);
                                    break;
                                case "name":
                                    mQingjiaName.setText(value);
                                    break;
                                case "type":
                                    mQingjiaType.setText(value);
                                    break;
                                case "startTime":
                                    mQingjiaStart.setText(value);
                                    break;
                                case "endTime":
                                    mQingjiaStop.setText(value);
                                    break;
                                case "number":
                                    mQingjiaTianshu.setText(value);
                                    break;
                                case "reason":
                                    mQingjiaShiyou.setText(value);
                                    break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<QingjiaShenheResponse> response) {
                Toast.makeText(QingJiaActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {

            }
        });
    }

    @OnClick({R.id.ll_fangshi, R.id.ll_leixing, R.id.ll_start, R.id.ll_stop, R.id.btn_caogao,
            R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_leixing:
                List<String> datas_type = new ArrayList();
                datas_type.add("事假");
                datas_type.add("病假");
                datas_type.add("婚假");
                datas_type.add("其他");
                chooseDate(datas_type, mQingjiaType, "请假类型");
                break;
            case R.id.ll_start:
                selectDate("", mQingjiaStart, "");
                break;
            case R.id.ll_stop:
                selectDate("", mQingjiaStop, mQingjiaStart.getText().toString());
                break;
            case R.id.btn_caogao:
//                Toast.makeText(this, "该功能暂未启用", Toast.LENGTH_SHORT).show();
                RequestServerQingjia_Save(mQingjiaType.getText().toString().trim(),
                        mQingjiaStart.getText().toString().trim(),
                        mQingjiaStop.getText().toString().trim(),
                        mQingjiaTianshu.getText().toString().trim(),
                        mQingjiaShiyou.getText().toString().trim()
//                    mTvFenguanleader.getText().toString().trim(),
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

    private void jianYanshuju() {
        if (TextUtils.isEmpty(mQingjiaName.getText().toString().trim())) {
            Toast.makeText(this, "请假人缺失", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mQingjiaBumen.getText().toString().trim())) {
            Toast.makeText(this, "申请部门缺失", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mQingjiaType.getText().toString().trim())) {
            Toast.makeText(this, "请选择请假类型", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mQingjiaStart.getText().toString().trim())) {
            Toast.makeText(this, "请选择开始时间", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mQingjiaStop.getText().toString().trim())) {
            Toast.makeText(this, "请选择结束时间", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mQingjiaTianshu.getText().toString().trim())) {
            Toast.makeText(this, "请填写请假时长", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mQingjiaShiyou.getText().toString().trim())) {
            Toast.makeText(this, "请填写请假事由", Toast.LENGTH_SHORT).show();
        }
//        else if (TextUtils.isEmpty(mTvFenguanleader.getText().toString().trim())) {
//            Toast.makeText(this, "请选择分管领导审核", Toast.LENGTH_SHORT).show();
//        } else if (TextUtils.isEmpty(mTvBuzhang.getText().toString().trim())) {
//            Toast.makeText(this, "请选择是否需要部长审核", Toast.LENGTH_SHORT).show();
//        }
        else {
            if(!TextUtils.isEmpty(formCode) && !"caogao".equals(formCode)){
                RequestServerReCommit();
            } else {
                RequestServerQingjia2(mQingjiaType.getText().toString().trim(),
                        mQingjiaStart.getText().toString().trim(),
                        mQingjiaStop.getText().toString().trim(),
                        mQingjiaTianshu.getText().toString().trim(),
                        mQingjiaShiyou.getText().toString().trim()
//                    mTvFenguanleader.getText().toString().trim(),
//                    mTvBuzhang.getText().toString().trim()
                );
            }
        }
    }

    /**
     * 请求网络接口,发起流程
     */
    private void RequestServerQingjia2(String type, String start, String stop,
                                       String shichang, String reason) {
        String sessionId = SPUtils.getString(this, "sessionId");

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"departments\":" + "\"" + mDepartmentName + "\",")
                .append("\"departments_id\":" + "\"" + mDepartmentId + "\",")
                .append("\"businessKey\":" + "\"" + businessKey + "\",")
                .append("\"name\":" + "\"" + mUserName + "\",")
                .append("\"startTime\":" + "\"" + start + "\",")
                .append("\"endTime\":" + "\"" + stop + "\",")
                .append("\"number\":" + "\"" + shichang + "\",")
                .append("\"type\":" + "\"" + type + "\",")
                .append("\"reason\":" + "\"" + reason + "\"")
//                .append("\"manager\":" + "\"" + mZuzhiUserBean.getId() + "\",")
//                .append("\"manager_name\":" + "\"" + managerName + "\",")
//                .append("\"comment\":" + "\"" + comment + "\"")
                //.append("\"other\":" + "\"" + reason + "\"")
                .append("}");

        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_STARTPROCESS,
                RequestMethod.POST, ProcessJieguoResponse.class);
        //添加url?key=value形式的参数
        request.addHeader("sessionId", sessionId);
        request.add("processDefinitionId", processDefinitionId);
        request.add("data", json.toString());
        request.add("businessKey", businessKey);
        Queue.add(0, request, new OnResponseListener<ProcessJieguoResponse>() {

            @Override
            public void onStart(int what) {
                if (mLoadingDialog!=null) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<ProcessJieguoResponse> response) {
                Log.w("2222", response.toString());
                if (response != null && response.get() != null) {
                    if (response.get().getCode() == 200) {
                        Toast.makeText(QingJiaActivity.this, "流程发起成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(QingJiaActivity.this, "流程发起失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(QingJiaActivity.this, "流程发起失败", Toast.LENGTH_SHORT).show();
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
     * 保存草稿箱
     */
    private void RequestServerQingjia_Save(String type, String start, String stop,
                                       String shichang, String reason) {
        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"departments\":" + "\"" + mDepartmentName + "\",")
                .append("\"departments_id\":" + "\"" + mDepartmentId + "\",")
                .append("\"businessKey\":" + "\"" + businessKey + "\",")
                .append("\"name\":" + "\"" + mUserName + "\",")
                .append("\"startTime\":" + "\"" + start + "\",")
                .append("\"endTime\":" + "\"" + stop + "\",")
                .append("\"number\":" + "\"" + shichang + "\",")
                .append("\"type\":" + "\"" + type + "\",")
                .append("\"reason\":" + "\"" + reason + "\"")
//                .append("\"manager\":" + "\"" + mZuzhiUserBean.getId() + "\",")
//                .append("\"manager_name\":" + "\"" + managerName + "\",")
//                .append("\"comment\":" + "\"" + comment + "\"")
                //.append("\"other\":" + "\"" + reason + "\"")
                .append("}");

        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_SAVEDRAFT,
                RequestMethod.POST, ProcessJieguoResponse.class);
        //添加url?key=value形式的参数
        request.addHeader("sessionId", mSessionId);
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
                Log.w("2222", response.toString());
                if (null != response && null != response.get()) {
                    if (response.get().getCode() == 200) {
                        Toast.makeText(QingJiaActivity.this, "已保存至草稿箱", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(QingJiaActivity.this, "保存草稿失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(QingJiaActivity.this, "保存草稿失败", Toast.LENGTH_SHORT).show();
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
     * 提交页面数据，完成当前审核
     */
    private void RequestServerReCommit() {
        //拼接data的json
        String bumen = mQingjiaBumen.getText().toString();
        String name = mQingjiaName.getText().toString();
        String starttime = mQingjiaStart.getText().toString();
        String stoptime = mQingjiaStop.getText().toString();
        String tianshu = mQingjiaTianshu.getText().toString();
        String type = mQingjiaType.getText().toString();
        String reason = mQingjiaShiyou.getText().toString();

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"departments\":" + "\"" + bumen + "\",")
                .append("\"name\":" + "\"" + name + "\",")
                .append("\"startTime\":" + "\"" + starttime + "\",")
                .append("\"endTime\":" + "\"" + stoptime + "\",")
                .append("\"number\":" + "\"" + tianshu + "\",")
                .append("\"type\":" + "\"" + type + "\",")
                .append("\"reason\":" + "\"" + reason + "\"")
//                .append("\"comment\":" + "\"" + comment + "\"")
                .append("}");

        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();

        //1-流程审核记录
        //创建请求
        Request<ProcessJieguoResponse> requestCommit = new JavaBeanRequest<>(UrlConstance.URL_PROCESS_COMMIT,
                RequestMethod.POST, ProcessJieguoResponse.class);
        //添加url?key=value形式的参数
        requestCommit.addHeader("sessionId", mSessionId);
        requestCommit.add("taskId", mTaskId);
        requestCommit.add("data", json.toString());
        Queue.add(0, requestCommit, new OnResponseListener<ProcessJieguoResponse>() {

            @Override
            public void onStart(int what) {
                if (null != mLoadingDialog) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<ProcessJieguoResponse> response) {
                Log.w("221", response.toString());
                if (null != response && null != response.get()) {
                    if (response.get().getCode() == 200) {
                        Toast.makeText(QingJiaActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(QingJiaActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (null != mLoadingDialog) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }
}
