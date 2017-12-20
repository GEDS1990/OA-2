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
 * Created by Administrator on 2017/8/10.
 */

public class Qiyekaohe_shenhe extends HeadBaseActivity {

    @BindView(R.id.companyName)
    TextView mCompanyName;
    @BindView(R.id.ll_bumen)
    LinearLayout mLlBumen;
    @BindView(R.id.bianhao)
    TextView mBianhao;
    @BindView(R.id.Oldaddress)
    TextView mOldaddress;
    @BindView(R.id.start_date)
    TextView mStartDate;
    @BindView(R.id.stop_date)
    TextView mStopDate;
    @BindView(R.id.wuyeshuidian)
    TextView wuyeshuidian;
    @BindView(R.id.image_wuyeshuidian)
    ImageView image_wuyeshuidian;
    @BindView(R.id.qianfei)
    EditText qianfei;
    @BindView(R.id.fangwujiaju)
    TextView fangwujiaju;
    @BindView(R.id.image_fangwujiaju)
    ImageView image_fangwujiaju;
    @BindView(R.id.ll_serviceInput)
    LinearLayout mLlServiceInput;
    @BindView(R.id.ll_investInput)
    LinearLayout mLlInvestInput;

    @BindView(R.id.qyzhfzkhyj)
    TextView qyzhfzkhyj;
    @BindView(R.id.image_qyzhfzkhyj)
    ImageView image_qyzhfzkhyj;
    @BindView(R.id.zlqjqnss)
    EditText zlqjqnss;
    @BindView(R.id.kaohebeizhu)
    EditText beizhu;
    @BindView(R.id.qyfzqk)
    TextView qyfzqk;
    @BindView(R.id.image_qyfzqk)
    ImageView image_qyfzqk;
    @BindView(R.id.trzqk)
    EditText trzqk;
    @BindView(R.id.sftgcwbb)
    TextView sftgcwbb;
    @BindView(R.id.image_sftgcwbb)
    ImageView image_sftgcwbb;
    @BindView(R.id.qyssqk)
    TextView qyssqk;
    @BindView(R.id.image_qyssqk)
    ImageView image_qyssqk;
    @BindView(R.id.rcxm)
    EditText rcxm;
    @BindView(R.id.zcsbrs)
    EditText zcsbrs;
    @BindView(R.id.wzqydzqk)
    EditText wzqydzqk;
    @BindView(R.id.tzcjrckpyj)
    TextView tzcjrckpyj;
    @BindView(R.id.image_tzcjrckpyj)
    ImageView image_tzcjrckpyj;
    @BindView(R.id.zlhtlx)
    TextView zlhtlx;
    @BindView(R.id.image_zlhtlx)
    ImageView image_zlhtlx;
    @BindView(R.id.fwzlsfjq)
    TextView fwzlsfjq;
    @BindView(R.id.image_fwzlsfjq)
    ImageView image_fwzlsfjq;
    @BindView(R.id.fwzlqf)
    EditText fwzlqf;
    @BindView(R.id.tzxylxqk)
    TextView tzxylxqk;
    @BindView(R.id.image_tzxylxqk)
    ImageView image_tzxylxqk;

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
    private String mTaskId;
    private String mProcessTaskType;
    private String mUserType;
    private String mSessionId;
    private CommonRecyclerAdapter<ProcessShenheHistoryBean> mAdapter;
    private List<ProcessShenheHistoryBean> datas = new ArrayList<>();
    private CommonRecyclerAdapter<ZuzhiUserBean> mHuiqianAdapter;
    private List<ZuzhiUserBean> datas2 = new ArrayList<>();

    @Override
    protected int getChildLayoutRes() {
        return R.layout.activity_qiyekaohe_shenhe;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("企业考核审核");
        initThisView();
    }

    private void initThisView() {
        mTaskId = getIntent().getStringExtra("taskId");
        mProcessTaskType = getIntent().getStringExtra("processTaskType");
        mUserType = SPUtils.getString(this, "userType");
        Log.w("6666", mProcessTaskType + "/,mUserType=" + mUserType);
        mSessionId = SPUtils.getString(this, "sessionId");

        //获取服务器数据，填充表单数据
        RequestServer();
//        //判断是否是发起会签节点
//        if ("vote".equals(mProcessTaskType)) {
//            mBtnCaogao.setText("退回发起人");
//            mLlHuiqianyijian.setVisibility(View.VISIBLE);
//        } else {
//            mBtnCaogao.setText("不同意");
//            mLlHuiqianyijian.setVisibility(View.GONE);
//        }
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
    }

    @OnClick({R.id.ll_wuyeshuidian, R.id.ll_fangwujiaju,
            R.id.ll_qyzhfzkhyj, R.id.ll_qyfzqk,
            R.id.ll_sftgcwbb, R.id.ll_qyssqk,
            R.id.ll_tzcjrckpyj, R.id.ll_zlhtlx,
            R.id.ll_fwzlsfjq, R.id.ll_tzxylxqk,
            R.id.ll_huiqianren, R.id.btn_caogao, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_wuyeshuidian:
                if("0".equals(wuyeshuidian.getTag().toString())) {
                    List<String> datas_1 = new ArrayList();
                    datas_1.add("是");
                    datas_1.add("否");
                    chooseDate(datas_1, wuyeshuidian, "物业费、水电费是否缴清");
                }
                break;
            case R.id.ll_fangwujiaju:
                if("0".equals(fangwujiaju.getTag().toString())) {
                    List<String> datas_2 = new ArrayList();
                    datas_2.add("是");
                    datas_2.add("否");
                    chooseDate(datas_2, fangwujiaju, "房屋和家具使用情况是否良好");
                }
                break;
            case R.id.ll_qyzhfzkhyj:
                if("0".equals(qyzhfzkhyj.getTag().toString())) {
                    List<String> datas_3 = new ArrayList();
                    datas_3.add("合格");
                    datas_3.add("不合格");
                    chooseDate(datas_3, qyzhfzkhyj, "企业综合发展考评建议");
                }
                break;
            case R.id.ll_qyfzqk:
                if("0".equals(qyfzqk.getTag().toString())) {
                    List<String> datas_4 = new ArrayList();
                    datas_4.add("高新技术企业");
                    datas_4.add("双软认证企业");
                    datas_4.add("知识产权");
                    datas_4.add("新三板挂牌");
                    datas_4.add("享受其他政府支持资金、条件");
                    chooseDate(datas_4, qyfzqk, "企业发展情况");
                }
                break;
            case R.id.ll_sftgcwbb:
                if("0".equals(sftgcwbb.getTag().toString())) {
                    List<String> datas_5 = new ArrayList();
                    datas_5.add("是");
                    datas_5.add("否");
                    chooseDate(datas_5, sftgcwbb, "是否提供财务报表");
                }
                break;
            case R.id.ll_qyssqk:
                if("0".equals(qyssqk.getTag().toString())) {
                    List<String> datas_6 = new ArrayList();
                    datas_6.add("主板上市");
                    datas_6.add("中小板上市");
                    datas_6.add("创业板上市");
                    datas_6.add("无");
                    chooseDate(datas_6, qyssqk, "企业上市情况");
                }
                break;

            case R.id.ll_tzcjrckpyj:
                if("0".equals(tzcjrckpyj.getTag().toString())) {
                    List<String> datas_7 = new ArrayList();
                    datas_7.add("合格");
                    datas_7.add("不合格");
                    chooseDate(datas_7, tzcjrckpyj, "投资促进与人才工作部考评建议");
                }
                break;
            case R.id.ll_zlhtlx:
                if("0".equals(zlhtlx.getTag().toString())) {
                    List<String> datas_8 = new ArrayList();
                    datas_8.add("合格");
                    datas_8.add("不合格");
                    chooseDate(datas_8, zlhtlx, "租赁合同考评建议");
                }
                break;
            case R.id.ll_fwzlsfjq:
                if("0".equals(fwzlsfjq.getTag().toString())) {
                    List<String> datas_9 = new ArrayList();
                    datas_9.add("是");
                    datas_9.add("否");
                    chooseDate(datas_9, fwzlsfjq, "房屋租赁是否缴清");
                }
                break;
            case R.id.ll_tzxylxqk:
                if("0".equals(tzxylxqk.getTag().toString())) {
                    List<String> datas_10 = new ArrayList();
                    datas_10.add("合格");
                    datas_10.add("不合格");
                    chooseDate(datas_10, tzxylxqk, "投资协议履行情况");
                }
                break;
            case R.id.ll_huiqianren:
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
                Toast.makeText(Qiyekaohe_shenhe.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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
                        if(!TextUtils.isEmpty(bean.getFormName()) && !TextUtils.isEmpty(bean.getFormCode())) {
                            Log.d("FormName", bean.getFormName());
                            Log.d("FormCode", bean.getFormCode());
                            switch (bean.getFormCode()) {
                                // 企业考核资产运营部完备信息
                                case "assessment-asset":
                                    mBtnCaogao.setVisibility(View.GONE);
                                    mBtnCommit.setText("完成");
                                    break;
                                // 企业考核企业服务部给意见
                                case "assessment-service":
                                    mLlServiceInput.setVisibility(View.VISIBLE);
                                    mBtnCaogao.setVisibility(View.GONE);
                                    mBtnCommit.setText("完成");
                                    break;
                                // 企业考核投资促进与人才工作部
                                case "assessment-invest":

                                    break;
                                // 企业考核会签
                                case "assessment-return":
                                    mLlHuiqianyijian.setVisibility(View.VISIBLE);
                                    mBtnCaogao.setText("回退发起人");
                                    mBtnCommit.setText("完成");
                                    break;
                                // 企业考核领导审核表
                                case "assessment-leader":
                                    mLlServiceInput.setVisibility(View.VISIBLE);
                                    mLlInvestInput.setVisibility(View.VISIBLE);
                                    mLlHuiqianren.setVisibility(View.VISIBLE);
                                    mXxreHuiqianren.setVisibility(View.VISIBLE);
                                    mBtnCaogao.setText("不同意");
                                    mBtnCommit.setText("同意");
                                    break;
                                // 企业考核通知
                                case "assessment-notice":
                                    break;
                            }
                        }
                        if(!TextUtils.isEmpty(bean.getName()) && !TextUtils.isEmpty(bean.getValue())) {
                            //当有type为userpicker的时候说明是可以发起会签的节点
                            String label = bean.getName();
                            String value = bean.getValue();
                            switch (label) {
                                case "company_name":
                                    mCompanyName.setText(value);
                                    break;
                                case "id":
                                    mBianhao.setText(value);
                                    break;
                                case "address":
                                    mOldaddress.setText(value);
                                    break;
                                case "startTime":
                                    mStartDate.setText(value);
                                    break;
                                case "endTime":
                                    mStopDate.setText(value);
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
                                case "cost":
                                    wuyeshuidian.setText(value);
                                    wuyeshuidian.setFocusable(false);
                                    image_wuyeshuidian.setVisibility(View.GONE);
                                    break;
                                case "arrears":
                                    qianfei.setText(value);
                                    qianfei.setFocusable(false);
                                    break;
                                case "use":
                                    fangwujiaju.setText(value);
                                    fangwujiaju.setFocusable(false);
                                    image_fangwujiaju.setVisibility(View.GONE);
                                    break;
                                case "serviceComment":
                                    qyzhfzkhyj.setText(value);
                                    image_qyzhfzkhyj.setVisibility(View.GONE);
                                    break;
                                case "revenue":
                                    zlqjqnss.setText(value);
                                    zlqjqnss.setFocusable(false);
                                    break;
                                case "remarks":
                                    beizhu.setText(value);
                                    beizhu.setFocusable(false);
                                    break;
                                case "develop":
                                    qyfzqk.setText(value);
                                    image_qyfzqk.setVisibility(View.GONE);
                                    break;
                                case "investment":
                                    trzqk.setText(value);
                                    trzqk.setFocusable(false);
                                    break;
                                case "finance":
                                    sftgcwbb.setText(value);
                                    image_sftgcwbb.setVisibility(View.GONE);
                                    break;
                                case "quote":
                                    qyssqk.setText(value);
                                    image_qyssqk.setVisibility(View.GONE);
                                    break;
                                case "personnel":
                                    rcxm.setText(value);
                                    rcxm.setFocusable(false);
                                    break;
                                case "security":
                                    zcsbrs.setText(value);
                                    zcsbrs.setFocusable(false);
                                    break;
                                case "capital":
                                    wzqydzqk.setText(value);
                                    wzqydzqk.setFocusable(false);
                                    break;
                            }
                        }
//                        if ("userpicker".equals(bean.getType())) {
//                            mLlHuiqianren.setVisibility(View.VISIBLE);
//                            mXxreHuiqianren.setVisibility(View.VISIBLE);
//                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<QingjiaShenheResponse> response) {
                Toast.makeText(Qiyekaohe_shenhe.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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
        String danwei = mCompanyName.getText().toString();
        String bianhao = mBianhao.getText().toString();
        String address = mOldaddress.getText().toString();

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
                .append("\"company_name\":" + "\"" + danwei + "\",")
                .append("\"id\":" + "\"" + bianhao + "\",")
                .append("\"address\":" + "\"" + address + "\",")
                .append("\"leader\":" + "\"" + leadersID.toString() + "\",")
                .append("\"leader_name\":" + "\"" + leadersName.toString() + "\",")

                .append("\"cost\":" + "\"" + wuyeshuidian.getText().toString() + "\",")
                .append("\"arrears\":" + "\"" + qianfei.getText().toString() + "\",")
                .append("\"use\":" + "\"" + fangwujiaju.getText().toString() + "\",")
                .append("\"serviceComment\":" + "\"" + qyzhfzkhyj.getText().toString() + "\",")
                .append("\"revenue\":" + "\"" + zlqjqnss.getText().toString() + "\",")
                .append("\"remarks\":" + "\"" + beizhu.getText().toString() + "\",")
                .append("\"develop\":" + "\"" + qyfzqk.getText().toString() + "\",")
                .append("\"investment\":" + "\"" + trzqk.getText().toString() + "\",")
                .append("\"finance\":" + "\"" + sftgcwbb.getText().toString() + "\",")
                .append("\"quote\":" + "\"" + qyssqk.getText().toString() + "\",")
                .append("\"personnel\":" + "\"" + rcxm.getText().toString() + "\",")
                .append("\"security\":" + "\"" + zcsbrs.getText().toString() + "\",")
                .append("\"capital\":" + "\"" + wzqydzqk.getText().toString() + "\",")

                .append("\"comment\":" + "\"" + comment + "\"")
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
                        Toast.makeText(Qiyekaohe_shenhe.this, "流程审核成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(Qiyekaohe_shenhe.this, "流程审核失败", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Qiyekaohe_shenhe.this, "回退成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }

                @Override
                public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                    Toast.makeText(Qiyekaohe_shenhe.this, "流程审核失败", Toast.LENGTH_SHORT).show();
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
