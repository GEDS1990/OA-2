package com.example.administrator.oa.view.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
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
import com.example.administrator.oa.view.bean.ZuzhiUserBean;
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
 * Created by Administrator on 2017/7/10.
 */

public class ZufangActivity extends HeadBaseActivity {
    @BindView(R.id.hetong_name)
    EditText mHetongName;
    @BindView(R.id.bumen)
    TextView mBumen;
    @BindView(R.id.bianhao)
    TextView mBianhao;
    @BindView(R.id.name_jia)
    EditText mNameJia;
    @BindView(R.id.name_yi)
    EditText mNameYi;
    @BindView(R.id.textView2)
    TextView mTextView2;
    @BindView(R.id.date)
    TextView mDate;
    @BindView(R.id.ll_date)
    LinearLayout mLlDate;
    @BindView(R.id.address)
    EditText mAddress;
    @BindView(R.id.main_content)
    EditText mMainContent;
    @BindView(R.id.btn_caogao)
    Button mBtnCaogao;
    @BindView(R.id.btn_commit)
    Button mBtnCommit;
    @BindView(R.id.zhubanren)
    TextView mZhubanren;
    private String mUserName;
    private String mUserId;
    private String mDepartmentName;
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
        return R.layout.activity_zufang;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("房屋租赁申请");
        initThisView();
    }

    private void initThisView() {
        mSessionId = SPUtils.getString(this, "sessionId");
        mUserName = SPUtils.getString(this, "userName");
        mUserId = SPUtils.getString(this, "userId");
        mDepartmentName = SPUtils.getString(this, "departmentName");
        processDefinitionId = getIntent().getStringExtra("processDefinitionId");
        mBumen.setText(mDepartmentName);
        mZhubanren.setText(mUserName);

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
                                case "rentalDepartments":
                                    mBumen.setText(value);
                                    break;
                                case "title":
                                    mHetongName.setText(value);
                                    break;
                                case "name1":
                                    mNameJia.setText(value);
                                    break;
                                case "name2":
                                    mNameYi.setText(value);
                                    break;
                                case "rentalDate":
                                    mDate.setText(value);
                                    break;
                                case "rentalAddress":
                                    mAddress.setText(value);
                                    break;
                                case "rentalContent":
                                    mMainContent.setText(value);
                                    break;
                                case "rentalId":
                                    mBianhao.setText(value);
                                    break;
                                // 主办人id
//                            case "initiatorRental":
//                                mZhubanren.setText(value);
//                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<QingjiaShenheResponse> response) {
                Toast.makeText(ZufangActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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
                    if (null != mAdapter) {
                        mAdapter.replaceAll(data);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessShenheHistoryRes> response) {
                Toast.makeText(ZufangActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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
                                case "rentalDepartments":
                                    mBumen.setText(value);
                                    break;
                                case "title":
                                    mHetongName.setText(value);
                                    break;
                                case "name1":
                                    mNameJia.setText(value);
                                    break;
                                case "name2":
                                    mNameYi.setText(value);
                                    break;
                                case "rentalDate":
                                    mDate.setText(value);
                                    break;
                                case "rentalAddress":
                                    mAddress.setText(value);
                                    break;
                                case "rentalContent":
                                    mMainContent.setText(value);
                                    break;
                                case "rentalId":
                                    mBianhao.setText(value);
                                    break;
                                // 主办人
                                case "initiatorRental":
                                    mZhubanren.setText(value);
                                    break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<QingjiaShenheResponse> response) {
                Toast.makeText(ZufangActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }

    @OnClick({R.id.fwzl_bianhao, R.id.ll_date, R.id.btn_caogao, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fwzl_bianhao:
                if("0".equals(mBianhao.getTag())) {
                    mBianhao.setTag("1");
                    houQuFormCode();
                }
                break;
            case R.id.ll_date:
                selectDate("", mDate, "");
                break;
            case R.id.btn_caogao:
                RequestServerGoodsLingqu_Save(mHetongName.getText().toString().trim(),
                        mBumen.getText().toString().trim(),
                        mBianhao.getText().toString().trim(),
                        mNameJia.getText().toString().trim(),
                        mNameYi.getText().toString().trim(),
                        mDate.getText().toString().trim(),
                        mAddress.getText().toString().trim(),
                        mMainContent.getText().toString().trim()
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
        if (TextUtils.isEmpty(mHetongName.getText().toString().trim())) {
            Toast.makeText(this, "请填写合同名称", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mBianhao.getText().toString().trim())) {
            Toast.makeText(this, "请点击获取编号", Toast.LENGTH_SHORT).show();
        }  else if (TextUtils.isEmpty(mBumen.getText().toString().trim())) {
            Toast.makeText(this, "主办部门缺失", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mZhubanren.getText().toString().trim())) {
            Toast.makeText(this, "主办人缺失", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mNameJia.getText().toString().trim())) {
            Toast.makeText(this, "请填写甲方名称", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mNameYi.getText().toString().trim())) {
            Toast.makeText(this, "请填写乙方名称", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mDate.getText().toString().trim())) {
            Toast.makeText(this, "请选择洽谈时间", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mAddress.getText().toString().trim())) {
            Toast.makeText(this, "请填写洽谈地址", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mMainContent.getText().toString().trim())) {
            Toast.makeText(this, "请填写合同主要内容", Toast.LENGTH_SHORT).show();
        } else {
            if(!TextUtils.isEmpty(formCode) && !"caogao".equals(formCode)){
                RequestServerReCommit();
            } else {
                RequestServerGoodsLingqu(mHetongName.getText().toString().trim(),
                        mBumen.getText().toString().trim(),
                        mBianhao.getText().toString().trim(),
                        mNameJia.getText().toString().trim(),
                        mNameYi.getText().toString().trim(),
                        mDate.getText().toString().trim(),
                        mAddress.getText().toString().trim(),
                        mMainContent.getText().toString().trim()
                );
            }
        }

    }

    /**
     * 请求网络，提交数据b
     */
    private void RequestServerGoodsLingqu(String hetongName, String bumen, String bianhao, String nameJia
            , String nameYi, String date, String address, String mainContent
    ) {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_STARTPROCESS,
                RequestMethod.POST, ProcessJieguoResponse.class);

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"title\":" + "\"" + hetongName + "\",")
                .append("\"rentalDepartments\":" + "\"" + bumen + "\",")
                .append("\"initiatorRental\":" + "\"" + mUserName + "\",")
                .append("\"initiatorRental_id\":" + "\"" + mUserId + "\",")
                .append("\"businessKey\":" + "\"" + businessKey + "\",")
                .append("\"rentalId\":" + "\"" + bianhao + "\",")
                .append("\"name1\":" + "\"" + nameJia + "\",")
                .append("\"name2\":" + "\"" + nameYi + "\",")
                .append("\"rentalDate\":" + "\"" + date + "\",")
                .append("\"rentalAddress\":" + "\"" + address + "\",")
                .append("\"rentalContent\":" + "\"" + mainContent + "\"")
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
                        Toast.makeText(ZufangActivity.this, "流程发起成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(ZufangActivity.this, "流程发起失败", Toast.LENGTH_SHORT).show();
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
        request.add("typecode", "zfbh");
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
                Toast.makeText(ZufangActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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
    private void RequestServerGoodsLingqu_Save(String hetongName, String bumen, String bianhao, String nameJia
            , String nameYi, String date, String address, String mainContent
    ) {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_SAVEDRAFT,
                RequestMethod.POST, ProcessJieguoResponse.class);

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"title\":" + "\"" + hetongName + "\",")
                .append("\"rentalDepartments\":" + "\"" + bumen + "\",")
                .append("\"initiatorRental\":" + "\"" + mUserName + "\",")
                .append("\"initiatorRental_id\":" + "\"" + mUserId + "\",")
                .append("\"businessKey\":" + "\"" + businessKey + "\",")
                .append("\"rentalId\":" + "\"" + bianhao + "\",")
                .append("\"name1\":" + "\"" + nameJia + "\",")
                .append("\"name2\":" + "\"" + nameYi + "\",")
                .append("\"rentalDate\":" + "\"" + date + "\",")
                .append("\"rentalAddress\":" + "\"" + address + "\",")
                .append("\"rentalContent\":" + "\"" + mainContent + "\"")
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
                        Toast.makeText(ZufangActivity.this, "流程发起成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(ZufangActivity.this, "保存草稿失败", Toast.LENGTH_SHORT).show();
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

        String bumen = mBumen.getText().toString();
        String hetongname = mHetongName.getText().toString();
        String name1 = mNameJia.getText().toString();
        String name2 = mNameYi.getText().toString();
        String date = mDate.getText().toString();
        String address = mAddress.getText().toString();
        String content = mMainContent.getText().toString();
        String bianhao = mBianhao.getText().toString();
        String zhubanren = mZhubanren.getText().toString();

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"title\":" + "\"" + hetongname + "\",")
                .append("\"rentalId\":" + "\"" + bianhao + "\",")
                .append("\"rentalDepartments\":" + "\"" + bumen + "\",")
                .append("\"initiatorRental\":" + "\"" + zhubanren + "\",")
                .append("\"name1\":" + "\"" + name1 + "\",")
                .append("\"name2\":" + "\"" + name2 + "\",")
                .append("\"rentalDate\":" + "\"" + date + "\",")
                .append("\"rentalAddress\":" + "\"" + address + "\",")
                .append("\"rentalContent\":" + "\"" + content + "\"")
//                .append("\"rentalAsset\":" + "\"" + zichanyy + "\",")
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
                        Toast.makeText(ZufangActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(ZufangActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
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
