package com.example.administrator.oa.view.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
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
import com.example.administrator.oa.view.bean.FormBianmaBean;
import com.example.administrator.oa.view.bean.ProcessJieguoResponse;
import com.example.administrator.oa.view.bean.ProcessShenheHistoryBean;
import com.example.administrator.oa.view.bean.ProcessShenheHistoryRes;
import com.example.administrator.oa.view.bean.QingjiaShenheBean;
import com.example.administrator.oa.view.bean.QingjiaShenheResponse;
import com.example.administrator.oa.view.bean.ZijinDiaoboBean;
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
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/7/6.
 */

public class ZijinActivity extends HeadBaseActivity {


    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.bumen)
    TextView mBumen;
    @BindView(R.id.bianhao)
    TextView mBianhao;
//    @BindView(R.id.tv_fenguanleader)
//    TextView mTvFenguanleader;
//    @BindView(R.id.ll_fenguanleader)
//    LinearLayout mLlFenguanleader;
    @BindView(R.id.payAccount)
    EditText mPayAccount;
    @BindView(R.id.shouKuanAccount)
    EditText mShouKuanAccount;
    @BindView(R.id.money)
    EditText mMoney;
    @BindView(R.id.add)
    TextView mAdd;
    @BindView(R.id.xxre)
    XXRecycleView mXxre;
    @BindView(R.id.btn_caogao)
    Button mBtnCaogao;
    @BindView(R.id.btn_commit)
    Button mBtnCommit;
    private CommonRecyclerAdapter<ZijinDiaoboBean> mGoodAdapter;
    private List<ZijinDiaoboBean> mGoodDatas = new ArrayList<>();
    private String mUserName;
    private String mUserId;
    private String mDepartmentId;
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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return R.layout.activity_zijingdiaofa;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("资金调拨");
        initThisView();
    }

    private void initThisView() {
        mSessionId = SPUtils.getString(this, "sessionId");
        mUserName = SPUtils.getString(this, "userName");
        mUserId = SPUtils.getString(this, "userId");
        mDepartmentId = SPUtils.getString(this, "departmentId");
        mDepartmentName = SPUtils.getString(this, "departmentName");
        processDefinitionId = getIntent().getStringExtra("processDefinitionId");
        mName.setText(mUserName);
        mBumen.setText(mDepartmentName);

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

        mXxre.setLayoutManager(new LinearLayoutManager(this));
        mGoodAdapter = new CommonRecyclerAdapter<ZijinDiaoboBean>(this, mGoodDatas, R.layout.item_zijing_diaobo) {
            @Override
            public void convert(CommonViewHolder holder, final ZijinDiaoboBean item, int position, boolean b) {

                holder.setText(R.id.number, "( " + (position + 1) + " )");
                holder.setText(R.id.payAccount, item.getFukuanAccount());
                holder.setText(R.id.shouKuanAccount, item.getShoukuanAccount());
                holder.setText(R.id.money, item.getMoney());

                holder.getView(R.id.delet).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mGoodAdapter.remove(item);
                    }
                });
            }
        };
        mXxre.setAdapter(mGoodAdapter);

        //设置输入字符
        mMoney.setFilters(new InputFilter[]{inputFilter});
        // 对报销金额进行文本监听
        mMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //如果输入框为空则不处理
                if (TextUtils.isEmpty(s)) {
                    return;
                }
                //第一个字符不可以为小数点
                if (s.length() == 1 && s.toString().equals(".")) {
                    mMoney.setText("");
                    return;
                }
                // 如果第一个数字是0，那它后面只能是小数点
                if (s.length() == 2 && s.toString().startsWith("0") && !s.toString().equals("0.")) {
                    mMoney.setText("0");
                    return;
                }

                mMoney.setSelection(s.toString().length());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * 控制人民币的输入
     */
    private InputFilter inputFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            // 删除等特殊字符，直接返回
            if (TextUtils.isEmpty(source)) {
                return null;
            }
            String dValue = dest.toString();
            String[] splitArray = dValue.split("\\.");
            if (splitArray.length > 1) {
                String dotValue = splitArray[1];
                // 2 表示输入框的小数位数
                int diff = dotValue.length() + 1 - 2;
                if (diff > 0) {
                    return source.subSequence(start, end - diff);
                }
            }
            return null;
        }
    };

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
                    ArrayList<ZijinDiaoboBean> pay = new ArrayList<ZijinDiaoboBean>();
                    // 先将物品明细的good存入列表，好定物品明细的数量
                    for (QingjiaShenheBean bean : shenheBeen) {
                        if (!TextUtils.isEmpty(bean.getLabel()) && !TextUtils.isEmpty(bean.getValue()) && bean.getLabel().startsWith("pay")) {
                            pay.add(new ZijinDiaoboBean(Integer.valueOf(bean.getLabel().replace("pay", "")), bean.getValue(), "", ""));
                        }
                    }
                    for (QingjiaShenheBean bean : shenheBeen) {
                        if (!TextUtils.isEmpty(bean.getName()) && !TextUtils.isEmpty(bean.getValue())) {
                            Log.d("Caogao", bean.getName());
                            Log.d("Caogao", bean.getValue());
                            //当有type为userpicker的时候说明是可以发起会签的节点
                            String label = bean.getName();
                            String value = bean.getValue();
                            switch (label) {
                                case "id":
                                    mBianhao.setText(value);
                                    break;
                            }
                            // 处理物品明细
                            if (!TextUtils.isEmpty(bean.getLabel())) {
                                for (int j = 0; j < pay.size(); j++) {
                                    if (bean.getLabel().startsWith("income") &&
                                            Integer.valueOf(bean.getLabel().replace("income", "")) == (pay.get(j).getIndex())) {
                                        pay.get(j).setShoukuanAccount(bean.getValue());
                                    }
                                    if (bean.getLabel().startsWith("money") &&
                                            Integer.valueOf(bean.getLabel().replace("money", "")) == (pay.get(j).getIndex())) {
                                        pay.get(j).setMoney(bean.getValue());
                                    }
                                }
                            }
                        }
                    }
                    // 排序
                    Collections.sort(pay);
                    // 把放入list里的物品明细添加进adapter里，展示出来
                    mGoodAdapter.addAll(pay);
                }
            }

            @Override
            public void onFailed(int what, Response<QingjiaShenheResponse> response) {
                Toast.makeText(ZijinActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ZijinActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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
                    ArrayList<ZijinDiaoboBean> pay = new ArrayList<ZijinDiaoboBean>();
                    // 先将物品明细的good存入列表，好定物品明细的数量
                    for (QingjiaShenheBean bean : shenheBeen) {
                        if (!TextUtils.isEmpty(bean.getLabel()) && !TextUtils.isEmpty(bean.getValue()) && bean.getLabel().startsWith("pay")) {
                            pay.add(new ZijinDiaoboBean(Integer.valueOf(bean.getLabel().replace("pay", "")), bean.getValue(), "", ""));
                        }
                    }
                    for (QingjiaShenheBean bean : shenheBeen) {
                        if (!TextUtils.isEmpty(bean.getName()) && !TextUtils.isEmpty(bean.getValue())) {
                            //当有type为userpicker的时候说明是可以发起会签的节点
                            String label = bean.getName();
                            String value = bean.getValue();
                            switch (label) {
                                case "transactor":
                                    mName.setText(value);
                                    break;
                                case "departments":
                                    mBumen.setText(value);
                                    break;
                                case "id":
                                    mBianhao.setText(value);
                                    break;
                            }
                        }
                        // 处理物品明细
                        if (!TextUtils.isEmpty(bean.getLabel())) {
                            for (int j = 0; j < pay.size(); j++) {
                                if (bean.getLabel().startsWith("income") &&
                                        Integer.valueOf(bean.getLabel().replace("income", "")) == (pay.get(j).getIndex())) {
                                    pay.get(j).setShoukuanAccount(bean.getValue());
                                }
                                if (bean.getLabel().startsWith("money") &&
                                        Integer.valueOf(bean.getLabel().replace("money", "")) == (pay.get(j).getIndex())) {
                                    pay.get(j).setMoney(bean.getValue());
                                }
                            }
                        }
                    }
                    // 排序
                    Collections.sort(pay);
                    // 把放入list里的物品明细添加进adapter里，展示出来
                    mGoodAdapter.addAll(pay);
                }
            }

            @Override
            public void onFailed(int what, Response<QingjiaShenheResponse> response) {
                Toast.makeText(ZijinActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        });

    }

    // , R.id.ll_fenguanleader,
    @OnClick({R.id.zjdb_bianhao, R.id.add, R.id.btn_caogao, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.zjdb_bianhao:
                if(TextUtils.isEmpty(mBianhao.getText().toString())){
                    houQuFormCode();
                }
                break;
            case R.id.add:
                String pay = mPayAccount.getText().toString().trim();
                String income = mShouKuanAccount.getText().toString().trim();
                String money = mMoney.getText().toString().trim();
                if (TextUtils.isEmpty(pay)) {
                    Toast.makeText(this, "请输入付款账号", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(income)) {
                    Toast.makeText(this, "请输入到账账号", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(money)) {
                    Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
                } else {
                    mGoodAdapter.add(new ZijinDiaoboBean(pay, income, money));
                    mXxre.scrollToPosition(mGoodAdapter.getItemCount() - 1);
                    mPayAccount.setText("");
                    mShouKuanAccount.setText("");
                    mMoney.setText("");
                }
                break;
            case R.id.btn_caogao:
                RequestServerGoodsLingqu_Save(mBianhao.getText().toString().trim());
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

        if (TextUtils.isEmpty(mUserId)) {
            Toast.makeText(this, "经办人缺失", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mBumen.getText().toString().trim())) {
            Toast.makeText(this, "申请部门缺失", Toast.LENGTH_SHORT).show();
//        } else if (TextUtils.isEmpty(mTvFenguanleader.getText().toString().trim())) {
//            Toast.makeText(this, "请选择分管领导", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mBianhao.getText().toString().trim())) {
            Toast.makeText(this, "请点击获取编号", Toast.LENGTH_SHORT).show();
        } else if (mGoodAdapter.getDatas().size() < 1) {
            Toast.makeText(this, "请填写资金调拨明细单，并确认添加", Toast.LENGTH_SHORT).show();
        } else {
            if(!TextUtils.isEmpty(formCode) && !"caogao".equals(formCode)){
                RequestServerReCommit();
            } else {
                RequestServerGoodsLingqu(mBianhao.getText().toString().trim());
            }
        }

    }

    /**
     * 请求您网络，提交数据
     */
    private void RequestServerGoodsLingqu(String bianhao) {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_STARTPROCESS,
                RequestMethod.POST, ProcessJieguoResponse.class);

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"departments\":" + "\"" + mDepartmentName + "\",")
                .append("\"departments_id\":" + "\"" + mDepartmentId + "\",")
                .append("\"businessKey\":" + "\"" + businessKey + "\",")
                .append("\"transactor\":" + "\"" + mUserName + "\",")
                .append("\"id\":" + "\"" + bianhao + "\",");
//                .append("\"manager_name\":" + "\"" + mZuzhiUserBean.getName() + "\",")
//                .append("\"manager\":" + "\"" + mZuzhiUserBean.getId() + "\",");

        for (int i = 0; i < 10; i++) {
            if (i <= mGoodAdapter.getDatas().size() - 1) {
                ZijinDiaoboBean bean = mGoodAdapter.getDatas().get(i);
                json.append("\"pay" + (i + 1) + "\":" + "\"" + bean.getFukuanAccount() + "\",")
                        .append("\"income" + (i + 1) + "\":" + "\"" + bean.getShoukuanAccount() + "\",")
                        .append("\"money" + (i + 1) + "\":" + "\"" + bean.getMoney() + "\",");
            } else {
                json.append("\"pay" + (i + 1) + "\":" + "\"\",")
                        .append("\"income" + (i + 1) + "\":" + "\"\",")
                        .append("\"money" + (i + 1) + "\":" + "\"\",");
            }
        }

        json.deleteCharAt(json.length() - 1);
        json.append("}");

        //添加url?key=value形式的参数
        request.addHeader("sessionId", mSessionId);
        request.add("processDefinitionId", processDefinitionId);
        request.add("businessKey", businessKey);
        request.add("data", json.toString());
        Log.w("99999", json.toString());
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
                        Toast.makeText(ZijinActivity.this, "流程发起成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(ZijinActivity.this, "流程发起失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.show();
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
                } else {
                    mBianhao.setTag("0");
                }
            }

            @Override
            public void onFailed(int what, Response<FormBianmaBean> response) {
                Toast.makeText(ZijinActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
                mBianhao.setTag("0");
            }

            @Override
            public void onFinish(int what) {

            }
        });

    }

    /**
     * 请求您网络，提交数据
     */
    private void RequestServerGoodsLingqu_Save(String bianhao) {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_SAVEDRAFT,
                RequestMethod.POST, ProcessJieguoResponse.class);

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"departments\":" + "\"" + mDepartmentName + "\",")
                .append("\"departments_id\":" + "\"" + mDepartmentId + "\",")
                .append("\"businessKey\":" + "\"" + businessKey + "\",")
                .append("\"transactor\":" + "\"" + mUserName + "\",")
                .append("\"id\":" + "\"" + bianhao + "\",");
//                .append("\"manager_name\":" + "\"" + mZuzhiUserBean.getName() + "\",")
//                .append("\"manager\":" + "\"" + mZuzhiUserBean.getId() + "\",");

        for (int i = 0; i < 10; i++) {
            if (i <= mGoodAdapter.getDatas().size() - 1) {
                ZijinDiaoboBean bean = mGoodAdapter.getDatas().get(i);
                json.append("\"pay" + (i + 1) + "\":" + "\"" + bean.getFukuanAccount() + "\",")
                        .append("\"income" + (i + 1) + "\":" + "\"" + bean.getShoukuanAccount() + "\",")
                        .append("\"money" + (i + 1) + "\":" + "\"" + bean.getMoney() + "\",");
            } else {
                json.append("\"pay" + (i + 1) + "\":" + "\"\",")
                        .append("\"income" + (i + 1) + "\":" + "\"\",")
                        .append("\"money" + (i + 1) + "\":" + "\"\",");
            }
        }

        json.deleteCharAt(json.length() - 1);
        json.append("}");

        //添加url?key=value形式的参数
        request.addHeader("sessionId", mSessionId);
        request.add("processDefinitionId", processDefinitionId);
        request.add("businessKey", businessKey);
        request.add("data", json.toString());
        Log.w("99999", json.toString());
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
                        Toast.makeText(ZijinActivity.this, "已保存至草稿箱", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(ZijinActivity.this, "保存草稿失败", Toast.LENGTH_SHORT).show();
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
        String name = mName.getText().toString();
        String danwei = mBumen.getText().toString();
        String biahao = mBianhao.getText().toString();

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"departments_name\":" + "\"" + danwei + "\",")
                .append("\"name\":" + "\"" + name + "\",")
                .append("\"id\":" + "\"" + biahao + "\",");
//                .append("\"leader\":" + "\"" + leadersID.toString() + "\",")
//                .append("\"leader_name\":" + "\"" + leadersName.toString() + "\",")
//                .append("\"comment\":" + "\"" + comment + "\",");

        for (int i = 0; i < 10; i++) {
            if (i <= mGoodAdapter.getDatas().size() - 1) {
                ZijinDiaoboBean bean = mGoodAdapter.getDatas().get(i);
                json.append("\"pay" + (i + 1) + "\":" + "\"" + bean.getFukuanAccount() + "\",")
                        .append("\"income" + (i + 1) + "\":" + "\"" + bean.getShoukuanAccount() + "\",")
                        .append("\"money" + (i + 1) + "\":" + "\"" + bean.getMoney() + "\",");
            } else {
                json.append("\"pay" + (i + 1) + "\":" + "\"\",")
                        .append("\"income" + (i + 1) + "\":" + "\"\",")
                        .append("\"money" + (i + 1) + "\":" + "\"\",");
            }
        }

        json.deleteCharAt(json.length() - 1);
        json.append("}");

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
                        Toast.makeText(ZijinActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(ZijinActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
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
