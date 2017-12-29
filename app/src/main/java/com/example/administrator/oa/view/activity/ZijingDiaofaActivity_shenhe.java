package com.example.administrator.oa.view.activity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.bean.ProcessJieguoResponse;
import com.example.administrator.oa.view.bean.ProcessShenheHistoryBean;
import com.example.administrator.oa.view.bean.ProcessShenheHistoryRes;
import com.example.administrator.oa.view.bean.QingjiaShenheBean;
import com.example.administrator.oa.view.bean.QingjiaShenheResponse;
import com.example.administrator.oa.view.bean.ZijinDiaoboBean;
import com.example.administrator.oa.view.bean.ZijngDiaoboshenheBean;
import com.example.administrator.oa.view.bean.ZuzhiUserBean;
import com.example.administrator.oa.view.bean.ZuzhiUserListResponse;
import com.example.administrator.oa.view.bean.organization_structure.ChildrenBean;
import com.example.administrator.oa.view.bean.organization_structure.OrganizationResponse;
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
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/8/8.
 */

public class ZijingDiaofaActivity_shenhe extends HeadBaseActivity {
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.bumen)
    TextView mBumen;
    @BindView(R.id.bianhao)
    TextView mBianhao;
    @BindView(R.id.xxre)
    XXRecycleView mXxre;
    @BindView(R.id.tv_huiqian)
    TextView mTvHuiqian;
    @BindView(R.id.ll_huiqianren)
    LinearLayout mLlHuiqianren;
    @BindView(R.id.xxre_huiqianren)
    XXRecycleView mXxreHuiqianren;
    @BindView(R.id.huiqianyijian)
    EditText mHuiqianyijian;
    @BindView(R.id.ll_huiqianyijian)
    LinearLayout mLlHuiqianyijian;
    @BindView(R.id.shenheyijian)
    TextView mShenheyijian;
    @BindView(R.id.ll_shenheyijian)
    LinearLayout mLlShenheyijian;
    @BindView(R.id.btn_caogao)
    Button mBtnCaogao;
    @BindView(R.id.btn_commit)
    Button mBtnCommit;
    @BindView(R.id.xxre_goodsapply)
    XXRecycleView mXxreGoodsApply;
    private String mTaskId;
    private String mProcessTaskType;
    private String mUserType;
    private String mDepartmentName;
    private String mDepartmentId;
    private String mSessionId;
    private CommonRecyclerAdapter<ProcessShenheHistoryBean> mAdapter;
    private List<ProcessShenheHistoryBean> datas = new ArrayList<>();
    private List<ZuzhiUserBean> datas2 = new ArrayList<>();
    private CommonRecyclerAdapter<ZuzhiUserBean> mHuiqianAdapter;
    private XXDialog mxxUsersDialog;
    private XXDialog mxxDialog2;
    private CommonRecyclerAdapter<ZijinDiaoboBean> mGoodApplyAdapter;
    private List<ZijinDiaoboBean> data3 = new ArrayList<>();

    @Override
    protected int getChildLayoutRes() {
        return R.layout.activity_zijingdiaofa_shenhe;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("合同审核单");
        initThisView();
    }

    private void initThisView() {
        mTaskId = getIntent().getStringExtra("taskId");
        mProcessTaskType = getIntent().getStringExtra("processTaskType");
        mUserType = SPUtils.getString(this, "userType");
        mDepartmentName = SPUtils.getString(this, "departmentName");
        mDepartmentId = SPUtils.getString(this, "departmentId");
        Log.w("6666", mProcessTaskType + "/,mUserType=" + mUserType);
        mSessionId = SPUtils.getString(this, "sessionId");

        //判断是否是发起会签节点
//        if ("vote".equals(mProcessTaskType)) {
//            mBtnCaogao.setText("退回发起人");
//            mLlHuiqianyijian.setVisibility(View.VISIBLE);
//        } else {
//            mBtnCaogao.setText("不同意");
//            mLlHuiqianyijian.setVisibility(View.GONE);
//        }
        //获取服务器数据，填充表单数据
        RequestServer();
        //流程记录的view
        mXxre.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CommonRecyclerAdapter<ProcessShenheHistoryBean>(this, datas, R.layout.item_myprocess_shenhejilu) {
            @Override
            public void convert(CommonViewHolder holder, ProcessShenheHistoryBean item, int i, boolean b) {
                holder.setText(R.id.processNameContent, item.getName());
                holder.setText(R.id.name, item.getAssignee());
                holder.setText(R.id.startTimeContent, item.getCreateTime());
                holder.setText(R.id.completeTimeContent, item.getCompleteTime());
            }
        };
        mXxre.setAdapter(mAdapter);

        //添加会签人
        mXxreHuiqianren.setLayoutManager(new GridLayoutManager(this, 4));
        mHuiqianAdapter = new CommonRecyclerAdapter<ZuzhiUserBean>(this, datas2, R.layout.item_add_person) {
            @Override
            public void convert(CommonViewHolder holder, final ZuzhiUserBean item, int i, boolean b) {
                holder.setText(R.id.name, item.getName());
                holder.getView(R.id.delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mHuiqianAdapter.remove(item);
                    }
                });
            }
        };
        mXxreHuiqianren.setAdapter(mHuiqianAdapter);

        //展示申请的物品明细单
        mXxreGoodsApply.setLayoutManager(new LinearLayoutManager(this));
        mGoodApplyAdapter = new CommonRecyclerAdapter<ZijinDiaoboBean>(this, data3, R.layout.item_zijing_diaobo_shenhe) {
            @Override
            public void convert(CommonViewHolder holder, ZijinDiaoboBean item, int position, boolean b) {
                holder.setText(R.id.number, "( " + (position + 1) + " )");
                holder.setText(R.id.payAccount, item.getFukuanAccount());
                holder.setText(R.id.shouKuanAccount, item.getShoukuanAccount());
                holder.setText(R.id.money, item.getMoney());
            }
        };
        mXxreGoodsApply.setAdapter(mGoodApplyAdapter);
    }


    @OnClick({R.id.ll_huiqianren, R.id.btn_caogao, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_huiqianren:
//                RequestServerGetZuzhi("选择会签人");
                if("0".equals(mLlHuiqianren.getTag())) {
                    mLlHuiqianren.setTag("1");
                    RequestServerGetZuzhi(mLlHuiqianren, mTvHuiqian, "请选择会签人", mHuiqianAdapter);
                }
                break;
            case R.id.btn_caogao:
                switch (mBtnCaogao.getText().toString()) {
                    case "不同意":
                        RequestServerCommit("不同意");
                        break;
                    case "回退发起人":
                        RequestServerTuihui();
                        break;
                }
                break;
            case R.id.btn_commit:
                RequestServerCommit("同意");
                break;
        }
    }

    /**
     * 请求网络接口-流程审核记录
     */
    private void RequestServer() {
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
                Toast.makeText(ZijingDiaofaActivity_shenhe.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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
                        if(!TextUtils.isEmpty(bean.getFormName()) && !TextUtils.isEmpty(bean.getFormCode())) {
                            Log.d("FormName", bean.getFormName());
                            Log.d("FormCode", bean.getFormCode());
                            switch (bean.getFormCode()) {
                                // 资金调拨部长审核
                                case "capital-leader":
                                    mLlHuiqianren.setVisibility(View.VISIBLE);
                                    mXxreHuiqianren.setVisibility(View.VISIBLE);
                                    mBtnCaogao.setText("不同意");
                                    mBtnCommit.setText("同意");
                                    break;
                                // 资金调拨会签
                                case "capital-return":
                                    mLlHuiqianyijian.setVisibility(View.VISIBLE);
                                    mBtnCaogao.setText("回退发起人");
                                    mBtnCommit.setText("完成");
                                    break;
                                // 资金调拨审核
                                case "capital-comment":
                                    mBtnCaogao.setText("不同意");
                                    mBtnCommit.setText("同意");
                                    break;
                                // 资金调拨通知
                                case "capital-notice":
                                    mLlShenheyijian.setVisibility(View.VISIBLE);
                                    mShenheyijian.setFocusable(false);
                                    mBtnCaogao.setVisibility(View.GONE);
                                    mBtnCommit.setText("完成");
                                    break;
                                // 资金调拨，回退之后
                                case "capital-request":
                                    mBtnCaogao.setVisibility(View.GONE);
                                    mBtnCommit.setText("提交");
                                    break;
                            }
                        }
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
                                case "comment":
                                    mShenheyijian.setText(value);
                                    break;
                                case "leader":
                                    if (TextUtils.isEmpty(bean.getLabel())) {
                                        return;
                                    }
                                    String[] ids = value.split(",");
                                    String[] names = bean.getLabel().split(",");
                                    for (int i=0; i<ids.length;i++) {
                                        mHuiqianAdapter.add(new ZuzhiUserBean(ids[i], names[i]));
                                    }
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
                    mGoodApplyAdapter.addAll(pay);
                }
            }

            @Override
            public void onFailed(int what, Response<QingjiaShenheResponse> response) {
                Toast.makeText(ZijingDiaofaActivity_shenhe.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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
    private void RequestServerCommit(String comment) {
        //拼接data的json
        String name = mName.getText().toString();
        String danwei = mBumen.getText().toString();
        String biahao = mBianhao.getText().toString();


        StringBuffer leadersID = new StringBuffer();
        StringBuffer leadersName = new StringBuffer();

        for (ZuzhiUserBean bean : mHuiqianAdapter.getDatas()) {
            leadersID.append(bean.getId()).append(",");
            leadersName.append(bean.getName()).append(",");
        }

        if (leadersID.toString().endsWith(",")) {
            leadersID.deleteCharAt(leadersID.toString().length() - 1);
        }
        if (leadersName.toString().endsWith(",")) {
            leadersName.deleteCharAt(leadersName.toString().length() - 1);
        }

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"departments_name\":" + "\"" + danwei + "\",")
                .append("\"name\":" + "\"" + name + "\",")
                .append("\"id\":" + "\"" + biahao + "\",")
                .append("\"leader\":" + "\"" + leadersID.toString() + "\",")
                .append("\"leader_name\":" + "\"" + leadersName.toString() + "\",")
                .append("\"comment\":" + "\"" + comment + "\",");

        for (int i = 0; i < 10; i++) {
            if (i <= mGoodApplyAdapter.getDatas().size() - 1) {
                ZijinDiaoboBean bean = mGoodApplyAdapter.getDatas().get(i);
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
                        Toast.makeText(ZijingDiaofaActivity_shenhe.this, "流程审核成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(ZijingDiaofaActivity_shenhe.this, "流程审核失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(ZijingDiaofaActivity_shenhe.this, "流程审核失败", Toast.LENGTH_SHORT).show();
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
     * 退回发起人
     */
    private void RequestServerTuihui() {
        String yijian = mHuiqianyijian.getText().toString().trim();
//        if (!TextUtils.isEmpty(yijian)) {
            //创建请求队列
            RequestQueue Queue = NoHttp.newRequestQueue();
            //创建请求
            Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_HUIQIAN_TUIHUI,
                    RequestMethod.POST, ProcessJieguoResponse.class);
            //添加url?key=value形式的参数
            request.add("taskId", mTaskId);
            request.add("comment", yijian);
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
                            Toast.makeText(ZijingDiaofaActivity_shenhe.this, "退回成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }

                @Override
                public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                    Toast.makeText(ZijingDiaofaActivity_shenhe.this, "流程审核失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFinish(int what) {
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                }
            });
//        } else {
//            Toast.makeText(this, "请填写会签处理意见", Toast.LENGTH_SHORT).show();
//        }
    }
}
