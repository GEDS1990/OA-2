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
import com.example.administrator.oa.view.bean.CompanyTypeBean;
import com.example.administrator.oa.view.bean.CompanyTypeResponse;
import com.example.administrator.oa.view.bean.FormBianmaBean;
import com.example.administrator.oa.view.bean.HetongBianhaoBean;
import com.example.administrator.oa.view.bean.HetongBianhaoResponse;
import com.example.administrator.oa.view.bean.ProcessJieguoResponse;
import com.example.administrator.oa.view.bean.ProcessShenheHistoryBean;
import com.example.administrator.oa.view.bean.ProcessShenheHistoryRes;
import com.example.administrator.oa.view.bean.QingjiaShenheBean;
import com.example.administrator.oa.view.bean.QingjiaShenheResponse;
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
 * Created by Administrator on 2017/7/7.
 */

public class QiyeTuizuActivity extends HeadBaseActivity {
    @BindView(R.id.companyName)
    TextView mCompanyName;
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
                            //当有type为userpicker的时候说明是可以发起会签的节点
                            String label = bean.getName();
                            String value = bean.getValue();
                            switch (label) {
                                case "company":
                                    Log.d("bean.name", value);
                                    Log.d("bean.label", bean.getLabel());
                                    if(!TextUtils.isEmpty(bean.getLabel())) {
                                        mCompanyName.setText(bean.getLabel());
                                        mCompanyName.setTag(value);
                                    }
                                    break;
                                case "id":
                                    Log.d("bean.name", value);
                                    Log.d("bean.label", bean.getLabel());
                                    if(!TextUtils.isEmpty(bean.getLabel())) {
                                        mBianhao.setText(bean.getLabel());
                                        mBianhao.setTag(value);
                                    }
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
                Toast.makeText(QiyeTuizuActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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
                    for (QingjiaShenheBean bean : shenheBeen) {
                        if(!TextUtils.isEmpty(bean.getName()) && !TextUtils.isEmpty(bean.getValue())) {
                            //当有type为userpicker的时候说明是可以发起会签的节点
                            String label = bean.getName();
                            String value = bean.getValue();
                            switch (label) {
                                case "company_name":
                                    mCompanyName.setText(value);
                                    break;
                                case "id_name":
                                    mBianhao.setText(value);
                                    break;
                                case "address":
                                    mOldaddress.setText(value);
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
    @OnClick({R.id.qytz_bianhao, R.id.ll_companyName, R.id.btn_caogao, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.qytz_bianhao:
//                if("0".equals(mBianhao.getTag())) {
//                    mBianhao.setTag("1");
//                    houQuFormCode();
//                }
                HuoquHetongNameAndID();
                break;
//            case R.id.ll_fenguanleader:
//                RequestServerGetZuzhi("请选择分管领导审核",mTvFenguanleader);
//                break;
            case R.id.ll_companyName:
                HuoquCompanyNameAndID();
                break;
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
     * 请求网络接口 获取公司名称列表
     */
    private void HuoquCompanyNameAndID() {

        //创建请求队列
        RequestQueue requestQueue = NoHttp.newRequestQueue();
        //创建请求
        Request<CompanyTypeResponse> request = new JavaBeanRequest<>(UrlConstance.URL_GET_COMPANY_TYPE,
                RequestMethod.POST, CompanyTypeResponse.class);
        request.add("companyname", "");
        //添加url?key=value形式的参数
        requestQueue.add(0, request, new OnResponseListener<CompanyTypeResponse>() {

            @Override
            public void onStart(int what) {
            }

            @Override
            public void onSucceed(int what, Response<CompanyTypeResponse> response) {
                Log.w("2222", response.toString());
                if (null != response && null != response.get() && null != response.get().getData()) {
                    List<CompanyTypeBean> data = response.get().getData();
                    chooseCompanyNameAndId(data, mCompanyName, "选择企业名称");
                }
            }

            @Override
            public void onFailed(int what, Response<CompanyTypeResponse> response) {
                Toast.makeText(QiyeTuizuActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {

            }
        });

    }

    public void chooseCompanyNameAndId(final List<CompanyTypeBean> data, final TextView tv, final String title) {
        if(null != mxxDialog){
            mxxDialog.dismiss();
        }
        mxxDialog = new XXDialog(this, R.layout.dialog_chooselist) {
            @Override
            public void convert(DialogViewHolder holder) {
                XXRecycleView xxre = (XXRecycleView) holder.getView(R.id.dialog_xxre);
                holder.setText(R.id.dialog_title, title);
                xxre.setLayoutManager(new LinearLayoutManager(QiyeTuizuActivity.this));
                final CommonRecyclerAdapter<CompanyTypeBean> adapter = new CommonRecyclerAdapter<CompanyTypeBean>
                        (QiyeTuizuActivity.this, data, R.layout.simple_list_item) {
                    @Override
                    public void convert(CommonViewHolder holder1, CompanyTypeBean item, int i, boolean b) {
                        holder1.setText(R.id.tv, item.getCompanyname());
                        holder1.getView(R.id.more).setVisibility(View.GONE);
                        holder1.getView(R.id.users).setVisibility(View.GONE);
                    }
                };
                xxre.setAdapter(adapter);
                adapter.replaceAll(data);
                adapter.setOnItemClickListener(new CommonRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClickListener(CommonViewHolder commonViewHolder, int i) {
                        tv.setText(adapter.getDatas().get(i).getCompanyname());
                        tv.setTag(adapter.getDatas().get(i).getCompanypId());
                        mxxDialog.dismiss();
                    }
                });

            }
        }.showDialog();
    }

    /**
     * 请求网络接口 获取合同编号
     */
    private void HuoquHetongNameAndID() {

        //创建请求队列
        RequestQueue requestQueue = NoHttp.newRequestQueue();
        //创建请求
        Request<HetongBianhaoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_GET_HETONG_TYPE,
                RequestMethod.POST, HetongBianhaoResponse.class);
        request.add("contractName", "");
        //添加url?key=value形式的参数
        requestQueue.add(0, request, new OnResponseListener<HetongBianhaoResponse>() {

            @Override
            public void onStart(int what) {
            }

            @Override
            public void onSucceed(int what, Response<HetongBianhaoResponse> response) {
                Log.w("2222", response.toString());
                if (null != response && null != response.get() && null != response.get().getData()) {
                    List<HetongBianhaoBean> data = response.get().getData();
                    chooseHetongNameAndId(data, mBianhao, "选择合同编号");
                }
            }

            @Override
            public void onFailed(int what, Response<HetongBianhaoResponse> response) {
                Toast.makeText(QiyeTuizuActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {

            }
        });

    }

    public void chooseHetongNameAndId(final List<HetongBianhaoBean> data, final TextView tv, final String title) {
        if(null != mxxDialog){
            mxxDialog.dismiss();
        }
        mxxDialog = new XXDialog(this, R.layout.dialog_chooselist) {
            @Override
            public void convert(DialogViewHolder holder) {
                XXRecycleView xxre = (XXRecycleView) holder.getView(R.id.dialog_xxre);
                holder.setText(R.id.dialog_title, title);
                xxre.setLayoutManager(new LinearLayoutManager(QiyeTuizuActivity.this));
                final CommonRecyclerAdapter<HetongBianhaoBean> adapter = new CommonRecyclerAdapter<HetongBianhaoBean>
                        (QiyeTuizuActivity.this, data, R.layout.simple_list_item) {
                    @Override
                    public void convert(CommonViewHolder holder1, HetongBianhaoBean item, int i, boolean b) {
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
                        tv.setTag(adapter.getDatas().get(i).getContracId());
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
        if (TextUtils.isEmpty(mCompanyName.getText().toString().trim())) {
            Toast.makeText(this, "请填写企业名称", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mBianhao.getText().toString().trim())) {
            Toast.makeText(this, "请点击获取编号", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mOldaddress.getText().toString().trim())) {
            Toast.makeText(this, "请填写原办公地址", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(mContent.getText().toString().trim())) {
            Toast.makeText(this, "请填写内容", Toast.LENGTH_SHORT).show();
        }  else {
            if(!TextUtils.isEmpty(formCode) && !"caogao".equals(formCode)){
                RequestServerReCommit();
            } else {
                RequestServerGoodsLingqu(mCompanyName.getText().toString().trim(),
                        mContent.getText().toString().trim(),
                        mBianhao.getText().toString().trim(),
                        mOldaddress.getText().toString().trim()
                );
            }
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
                .append("\"company_name\":" + "\"" + companyName + "\",")
                .append("\"company\":" + "\"" + mCompanyName.getTag().toString() + "\",")
                .append("\"businessKey\":" + "\"" + businessKey + "\",")
                .append("\"content\":" + "\"" + content + "\",")
                .append("\"id\":" + "\"" + mBianhao.getTag().toString() + "\",")
                .append("\"id_name\":" + "\"" + bianhao + "\",")
                .append("\"address\":" + "\"" + oldaddress + "\"")
//                .append("\"address_name\":" + "\"" + mZuzhiUserBean.getName() + "\",")
//                .append("\"address\":" + "\"" + mZuzhiUserBean.getId() + "\"")
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
                .append("\"company_name\":" + "\"" + companyName + "\",")
                .append("\"company\":" + "\"" + mCompanyName.getTag().toString() + "\",")
                .append("\"businessKey\":" + "\"" + businessKey + "\",")
                .append("\"content\":" + "\"" + content + "\",")
                .append("\"id\":" + "\"" + mBianhao.getTag().toString() + "\",")
                .append("\"id_name\":" + "\"" + bianhao + "\",")
                .append("\"address\":" + "\"" + oldaddress + "\"")
//                .append("\"address_name\":" + "\"" + mZuzhiUserBean.getName() + "\",")
//                .append("\"address\":" + "\"" + mZuzhiUserBean.getId() + "\"")
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

    /**
     * 提交页面数据，完成当前审核
     */

    private void RequestServerReCommit() {
        //拼接data的json
        String danwei = mCompanyName.getText().toString();
        String bianhao = mBianhao.getText().toString();
        String address = mOldaddress.getText().toString();
        String content = mContent.getText().toString();

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"company_name\":" + "\"" + danwei + "\",")
                .append("\"id_name\":" + "\"" + bianhao + "\",")
                .append("\"address\":" + "\"" + address + "\",")
                .append("\"content\":" + "\"" + content + "\"")
//                .append("\"investComment\":" + "\"" + yijian + "\",")
//                .append("\"leader\":" + "\"" + leadersID.toString() + "\",")
//                .append("\"leader_name\":" + "\"" + leadersName.toString() + "\",")
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
                if (mLoadingDialog != null) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<ProcessJieguoResponse> response) {
                if (null != response && null != response.get()) {
                    if (response.get().getCode() == 200) {
                        Toast.makeText(QiyeTuizuActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(QiyeTuizuActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
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
