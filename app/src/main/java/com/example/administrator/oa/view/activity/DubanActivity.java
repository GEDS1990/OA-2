package com.example.administrator.oa.view.activity;

import android.support.v7.widget.LinearLayoutManager;
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
import com.example.administrator.oa.view.bean.ProcessShenheHistoryBean;
import com.example.administrator.oa.view.bean.ProcessShenheHistoryRes;
import com.example.administrator.oa.view.bean.QingjiaShenheBean;
import com.example.administrator.oa.view.bean.QingjiaShenheResponse;
import com.example.administrator.oa.view.bean.WorkFromTypeBean;
import com.example.administrator.oa.view.bean.WorkFromTypeResponse;
import com.example.administrator.oa.view.bean.YingzhangTypeBean;
import com.example.administrator.oa.view.bean.YingzhangTypeResponse;
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
 * Created by Administrator on 2017/7/11.
 */

public class DubanActivity extends HeadBaseActivity {
    @BindView(R.id.bianhao)
    TextView mBianhao;
    @BindView(R.id.workfrom)
    TextView mWorkfrom;
    @BindView(R.id.firstBumen)
    EditText mFirstBumen;
    @BindView(R.id.fuzeren1)
    EditText mFuzeren1;
    @BindView(R.id.secondBumen)
    EditText mSecondBumen;
    @BindView(R.id.fuzeren2)
    EditText mFuzeren2;
    @BindView(R.id.date)
    TextView mDate;
    @BindView(R.id.ll_date)
    LinearLayout mLlDate;
    @BindView(R.id.qixian)
    EditText mQixian;
    @BindView(R.id.content)
    EditText mContent;
    @BindView(R.id.btn_caogao)
    Button mBtnCaogao;
    @BindView(R.id.btn_commit)
    Button mBtnCommit;

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
        return R.layout.activity_duban;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("工作督办单");
//        ((TextView) headerCenter.getChildAt(0)).setText("资金申请单");
        initThisView();
    }

    private void initThisView() {
        mSessionId = SPUtils.getString(this, "sessionId");
        processDefinitionId = getIntent().getStringExtra("processDefinitionId");
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
                            String label = bean.getName();
                            String value = bean.getValue();
                            switch (label) {
                                case "id":
                                    mBianhao.setText(value);
                                    break;
                                case "work":
                                    Log.d("bean.name", value);
                                    Log.d("bean.label", bean.getLabel());
                                    mWorkfrom.setTag(value);
                                    if(!TextUtils.isEmpty(bean.getLabel())){
                                        mWorkfrom.setText(bean.getLabel());
                                    }
                                    break;
                                case "department1":
                                    mFirstBumen.setText(value);
                                    break;
                                case "name1":
                                    mFuzeren1.setText(value);
                                    break;
                                case "department2":
                                    mSecondBumen.setText(value);
                                    break;
                                case "name2":
                                    mFuzeren2.setText(value);
                                    break;
                                case "date":
                                    mDate.setText(value);
                                    break;
                                case "term":
                                    mQixian.setText(value);
                                    break;
                                case "content":
                                    mContent.setText(value);
                                    break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<QingjiaShenheResponse> response) {
                Toast.makeText(DubanActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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
     * 获取审核信息
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
                if (null != response && null != response.get() && null != response.get().getData()) {
                    List<ProcessShenheHistoryBean> data = response.get().getData();
                    if (mAdapter != null) {
                        mAdapter.replaceAll(data);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessShenheHistoryRes> response) {
                Toast.makeText(DubanActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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
                if (mLoadingDialog != null) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<QingjiaShenheResponse> response) {
                if (null != response && null != response.get() && null != response.get().getData()) {
                    List<QingjiaShenheBean> shenheBeen = response.get().getData();
                    //按顺序填写数据
                    for (QingjiaShenheBean bean : shenheBeen) {
                        if(!TextUtils.isEmpty(bean.getName()) && !TextUtils.isEmpty(bean.getValue())) {
                            String label = bean.getName();
                            String value = bean.getValue();
                            switch (label) {
                                case "id":
                                    mBianhao.setText(value);
                                    break;
                                case "work_name":
                                    mWorkfrom.setText(value);
                                    break;
                                case "department1":
                                    mFirstBumen.setText(value);
                                    break;
                                case "name1":
                                    mFuzeren1.setText(value);
                                    break;
                                case "department2":
                                    mSecondBumen.setText(value);
                                    break;
                                case "name2":
                                    mFuzeren2.setText(value);
                                    break;
                                case "date":
                                    mDate.setText(value);
                                    break;
                                case "term":
                                    mQixian.setText(value);
                                    break;
                                case "content":
                                    mContent.setText(value);
                                    break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<QingjiaShenheResponse> response) {
                Toast.makeText(DubanActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        });

    }

    @OnClick({R.id.gzdbd_bianhao, R.id.ll_workfrom, R.id.ll_date, R.id.btn_caogao, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.gzdbd_bianhao:
                if(TextUtils.isEmpty(mBianhao.getText().toString())){
                    houQuFormCode();
                }
                break;
            case R.id.ll_workfrom:
                HuoquWorkNameAndID();
                break;
            case R.id.ll_date:
                selectDate("", mDate, "");
                break;
            case R.id.btn_caogao:
                RequestServerGoodsLingqu_Save(
                        mWorkfrom.getText().toString().trim(),
                        mBianhao.getText().toString().trim(),
                        mFirstBumen.getText().toString().trim(),
                        mFuzeren1.getText().toString().trim(),
                        mSecondBumen.getText().toString().trim(),
                        mFuzeren2.getText().toString().trim(),
                        mDate.getText().toString().trim(),
                        mQixian.getText().toString().trim(),
                        mContent.getText().toString().trim()
                );
                break;
            case R.id.btn_commit:
                jianYanshuju();
                break;
        }
    }

    /**
     * 请求网络接口 获取工作来源列表
     */
    private void HuoquWorkNameAndID() {

        //创建请求队列
        RequestQueue requestQueue = NoHttp.newRequestQueue();
        //创建请求
        Request<WorkFromTypeResponse> request = new JavaBeanRequest<>(UrlConstance.URL_GET_WORKFORM_TYPE,
                RequestMethod.POST, WorkFromTypeResponse.class);
        request.add("activityName", "");
        //添加url?key=value形式的参数
        requestQueue.add(0, request, new OnResponseListener<WorkFromTypeResponse>() {

            @Override
            public void onStart(int what) {
            }

            @Override
            public void onSucceed(int what, Response<WorkFromTypeResponse> response) {
                Log.w("2222", response.toString());
                if (null != response && null != response.get() && null != response.get().getData()) {
                    List<WorkFromTypeBean> data = response.get().getData();
                    chooseWorkNameAndId(data, mWorkfrom, "选择工作来源");
                }
            }

            @Override
            public void onFailed(int what, Response<WorkFromTypeResponse> response) {
                Toast.makeText(DubanActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {

            }
        });

    }

    public void chooseWorkNameAndId(final List<WorkFromTypeBean> data, final TextView tv, final String title) {
        if(null != mxxDialog){
            mxxDialog.dismiss();
        }
        mxxDialog = new XXDialog(this, R.layout.dialog_chooselist) {
            @Override
            public void convert(DialogViewHolder holder) {
                XXRecycleView xxre = (XXRecycleView) holder.getView(R.id.dialog_xxre);
                holder.setText(R.id.dialog_title, title);
                xxre.setLayoutManager(new LinearLayoutManager(DubanActivity.this));
                final CommonRecyclerAdapter<WorkFromTypeBean> adapter = new CommonRecyclerAdapter<WorkFromTypeBean>
                        (DubanActivity.this, data, R.layout.simple_list_item) {
                    @Override
                    public void convert(CommonViewHolder holder1, WorkFromTypeBean item, int i, boolean b) {
                        holder1.setText(R.id.tv, item.getName());
                        holder1.getView(R.id.more).setVisibility(View.GONE);
                        holder1.getView(R.id.users).setVisibility(View.GONE);
                    }
                };
                xxre.setAdapter(adapter);
                adapter.replaceAll(data);
                adapter.setOnItemClickListener(new CommonRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClickListener(CommonViewHolder commonViewHolder, int i) {
                        tv.setText(adapter.getDatas().get(i).getName());
                        tv.setTag(adapter.getDatas().get(i).getId());
                        mxxDialog.dismiss();
                    }
                });

            }
        }.showDialog();
    }

    /**
     * 填写的数据进行校验
     */
    private void jianYanshuju() {
        if (TextUtils.isEmpty(mBianhao.getText().toString().trim())) {
            Toast.makeText(this, "请点击获取编号", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mWorkfrom.getText().toString().trim())) {
            Toast.makeText(this, "请填写工作来源", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mFirstBumen.getText().toString().trim())) {
            Toast.makeText(this, "请填写主办部门", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mFuzeren1.getText().toString().trim())) {
            Toast.makeText(this, "请填写主办部门责任人", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mSecondBumen.getText().toString().trim())) {
            Toast.makeText(this, "请填写协办部门", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mFuzeren2.getText().toString().trim())) {
            Toast.makeText(this, "请填写协办部门负责人", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mDate.getText().toString().trim())) {
            Toast.makeText(this, "请选择交办日期", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mQixian.getText().toString().trim())) {
            Toast.makeText(this, "请填写办结期限", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mContent.getText().toString().trim())) {
            Toast.makeText(this, "请填写督办内容", Toast.LENGTH_SHORT).show();
        } else {
            if(!TextUtils.isEmpty(formCode) && !"caogao".equals(formCode)){
                RequestServerReCommit();
            } else {
                RequestServerGoodsLingqu(
                        mWorkfrom.getText().toString().trim(),
                        mBianhao.getText().toString().trim(),
                        mFirstBumen.getText().toString().trim(),
                        mFuzeren1.getText().toString().trim(),
                        mSecondBumen.getText().toString().trim(),
                        mFuzeren2.getText().toString().trim(),
                        mDate.getText().toString().trim(),
                        mQixian.getText().toString().trim(),
                        mContent.getText().toString().trim()
                );
            }
        }

    }

    /**
     * 请求网络，提交数据b
     */
    private void RequestServerGoodsLingqu(String workfrom, String bianhao
            , String firstBumen, String fuzeren1, String secondBumen, String fuzeren2, String date, String qixian, String content) {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_STARTPROCESS,
                RequestMethod.POST, ProcessJieguoResponse.class);

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"id\":" + "\"" + bianhao + "\",")
                .append("\"work_name\":" + "\"" + workfrom + "\",")
                .append("\"work\":" + "\"" + mWorkfrom.getTag().toString() + "\",")
                .append("\"department1\":" + "\"" + firstBumen + "\",")
                .append("\"name1\":" + "\"" + fuzeren1 + "\",")
                .append("\"department2\":" + "\"" + secondBumen + "\",")
                .append("\"name2\":" + "\"" + fuzeren2 + "\",")
                .append("\"date\":" + "\"" + date + "\",")
                .append("\"term\":" + "\"" + qixian + "\",")
                .append("\"businessKey\":" + "\"" + businessKey + "\",")
                .append("\"content\":" + "\"" + content + "\"")
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
                        Toast.makeText(DubanActivity.this, "流程发起成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(DubanActivity.this, "流程发起失败", Toast.LENGTH_SHORT).show();
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
        request.add("typecode", "dbbh");
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
                Toast.makeText(DubanActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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
    private void RequestServerGoodsLingqu_Save(String workfrom, String bianhao
            , String firstBumen, String fuzeren1, String secondBumen, String fuzeren2,
                                               String date, String qixian, String content) {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_SAVEDRAFT,
                RequestMethod.POST, ProcessJieguoResponse.class);

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"id\":" + "\"" + bianhao + "\",")
                .append("\"work_name\":" + "\"" + workfrom + "\",")
                .append("\"work\":" + "\"" + mWorkfrom.getTag().toString() + "\",")
                .append("\"department1\":" + "\"" + firstBumen + "\",")
                .append("\"name1\":" + "\"" + fuzeren1 + "\",")
                .append("\"department2\":" + "\"" + secondBumen + "\",")
                .append("\"name2\":" + "\"" + fuzeren2 + "\",")
                .append("\"date\":" + "\"" + date + "\",")
                .append("\"term\":" + "\"" + qixian + "\",")
                .append("\"businessKey\":" + "\"" + businessKey + "\",")
                .append("\"content\":" + "\"" + content + "\"")
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
                        Toast.makeText(DubanActivity.this, "已保存至草稿箱", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(DubanActivity.this, "保存草稿失败", Toast.LENGTH_SHORT).show();
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
     * 提交页面数据，完成当前审核
     */
    private void RequestServerReCommit() {
        //拼接data的json
        String workfrom = mWorkfrom.getText().toString().trim();
        String bianhao = mBianhao.getText().toString().trim();
        String firstbumen = mFirstBumen.getText().toString().trim();
        String fuzeren1 = mFuzeren1.getText().toString().trim();
        String secondbumen = mSecondBumen.getText().toString().trim();
        String fuzeren2 = mFuzeren2.getText().toString().trim();
        String date = mDate.getText().toString().trim();
        String qixian = mQixian.getText().toString().trim();
        String content = mContent.getText().toString().trim();

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"id\":" + "\"" + bianhao + "\",")
                .append("\"work_name\":" + "\"" + workfrom + "\",")
                .append("\"department1\":" + "\"" + firstbumen + "\",")
                .append("\"name1\":" + "\"" + fuzeren1 + "\",")
                .append("\"department2\":" + "\"" + secondbumen + "\",")
                .append("\"name2\":" + "\"" + fuzeren2 + "\",")
                .append("\"date\":" + "\"" + date + "\",")
                .append("\"term\":" + "\"" + qixian + "\",")
                .append("\"content\":" + "\"" + content + "\",")
//                .append("\"user\":" + "\"" + banlirenId + "\",")
//                .append("\"user_name\":" + "\"" + mTvBanliren.getText().toString() + "\"")
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
                if (mLoadingDialog != null) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<ProcessJieguoResponse> response) {
                if (null != response && null != response.get()) {
                    if (response.get().getCode() == 200) {
                        Toast.makeText(DubanActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(DubanActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(DubanActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
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
