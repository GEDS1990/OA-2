package com.example.administrator.oa.view.activity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
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
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/8/10.
 */

public class HouseRent_shenhe extends HeadBaseActivity {
    @BindView(R.id.hetong_name)
    TextView mHetongName;
    @BindView(R.id.bianhao)
    TextView mBianhao;
    @BindView(R.id.bumen)
    TextView mBumen;
    @BindView(R.id.zhubanren)
    TextView mZhubanren;
    @BindView(R.id.name_jia)
    TextView mNameJia;
    @BindView(R.id.name_yi)
    TextView mNameYi;
    @BindView(R.id.textView2)
    TextView mTextView2;
    @BindView(R.id.date)
    TextView mDate;
    @BindView(R.id.ll_date)
    LinearLayout mLlDate;
    @BindView(R.id.address)
    TextView mAddress;
    @BindView(R.id.main_content)
    TextView mMainContent;
    @BindView(R.id.zichanyy_yijian)
    EditText mZichanyyYijian;
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
    @BindView(R.id.ll_zichanyy_yijian)
    LinearLayout mLlZichanyyYijian;

    private String mTaskId;
    private String mProcessTaskType;
    private String mUserType;
    private String mSessionId;
    private CommonRecyclerAdapter<ProcessShenheHistoryBean> mAdapter;
    private List<ProcessShenheHistoryBean> datas = new ArrayList<>();
    private CommonRecyclerAdapter<ZuzhiUserBean> mHuiqianAdapter;
    private List<ZuzhiUserBean> datas2 = new ArrayList<>();
    private XXDialog mxxUsersDialog;
    private XXDialog mxxDialog2;

    @Override
    protected int getChildLayoutRes() {
        return R.layout.activity_houserent_shenhe;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("房屋租赁审核");
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
        //判断是否是发起会签节点
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
                    if (null != mAdapter) {
                        mAdapter.replaceAll(data);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessShenheHistoryRes> response) {
                Toast.makeText(HouseRent_shenhe.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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
                                // 房屋租赁流程资产运营
                                case "rental-asset":
                                    mBtnCaogao.setVisibility(View.GONE);
                                    mBtnCommit.setText("完成");
                                    break;
                                // 房屋租赁流程会签审核
                                case "rental-leader":
                                    mZichanyyYijian.setFocusable(false);
                                    mZichanyyYijian.setGravity(Gravity.RIGHT);
                                    mLlHuiqianren.setVisibility(View.VISIBLE);
                                    mXxreHuiqianren.setVisibility(View.VISIBLE);
                                    mBtnCaogao.setText("不同意");
                                    mBtnCommit.setText("同意");
                                    break;
                                // 房屋租赁流程会签
                                case "rental-return":
                                    mZichanyyYijian.setFocusable(false);
                                    mZichanyyYijian.setGravity(Gravity.RIGHT);
                                    mLlHuiqianyijian.setVisibility(View.VISIBLE);
                                    mBtnCaogao.setText("回退发起人");
                                    mBtnCommit.setText("完成");
                                    break;
                                // 房屋租赁流程通知
                                case "rental-notice":
                                    mZichanyyYijian.setFocusable(false);
                                    mZichanyyYijian.setGravity(Gravity.RIGHT);
                                    mLlShenheyijian.setVisibility(View.VISIBLE);
                                    mShenheyijian.setFocusable(false);
                                    mBtnCaogao.setVisibility(View.GONE);
                                    mBtnCommit.setText("完成");
                                    break;
                            }
                        }
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
                                case "comment":
                                    mShenheyijian.setText(value);
                                    break;
                                // 主办人
                                case "initiatorRental":
                                    mZhubanren.setText(value);
                                    break;
                                // 资产运营部意见
                                case "rentalAsset":
                                    mZichanyyYijian.setText(value);
                                    break;
                            }
                        }
//                        if ("userpicker".equals(bean.getType())) {
//                            mLlHuiqianren.setVisibility(View.VISIBLE);
//                            mXxreHuiqianren.setVisibility(View.VISIBLE);
//                        }
//                        if ("rentalAsset".equals(bean.getLabel())&&"Y".equals(bean.getReadOnly())) {
//                            mZichanyyYijian.setFocusable(false);
//                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<QingjiaShenheResponse> response) {
                Toast.makeText(HouseRent_shenhe.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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

        String bumen = mBumen.getText().toString();
        String hetongname = mHetongName.getText().toString();
        String name1 = mNameJia.getText().toString();
        String name2 = mNameYi.getText().toString();
        String date = mDate.getText().toString();
        String address = mAddress.getText().toString();
        String content = mMainContent.getText().toString();
        String zichanyy = mZichanyyYijian.getText().toString();
        String bianhao = mBianhao.getText().toString();
        String zhubanren = mZhubanren.getText().toString();

        if (TextUtils.isEmpty(zichanyy)) {
            Toast.makeText(this, "请填写资产运营部意见", Toast.LENGTH_SHORT).show();
            return;
        }

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
                .append("\"title\":" + "\"" + hetongname + "\",")
                .append("\"rentalId\":" + "\"" + bianhao + "\",")
                .append("\"rentalDepartments\":" + "\"" + bumen + "\",")
                .append("\"initiatorRental\":" + "\"" + zhubanren + "\",")
                .append("\"name1\":" + "\"" + name1 + "\",")
                .append("\"name2\":" + "\"" + name2 + "\",")
                .append("\"rentalDate\":" + "\"" + date + "\",")
                .append("\"rentalAddress\":" + "\"" + address + "\",")
                .append("\"rentalContent\":" + "\"" + content + "\",")
                .append("\"rentalAsset\":" + "\"" + zichanyy + "\",")
                .append("\"leader\":" + "\"" + leadersID.toString() + "\",")
                .append("\"leader_name\":" + "\"" + leadersName.toString() + "\",")
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
                        Toast.makeText(HouseRent_shenhe.this, "流程审核成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(HouseRent_shenhe.this, "流程审核失败", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(HouseRent_shenhe.this, "退回成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }

                @Override
                public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                    Toast.makeText(HouseRent_shenhe.this, "流程审核失败", Toast.LENGTH_SHORT).show();
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
