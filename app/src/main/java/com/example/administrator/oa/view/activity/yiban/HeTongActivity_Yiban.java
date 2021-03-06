package com.example.administrator.oa.view.activity.yiban;

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
import com.example.administrator.oa.view.activity.HeadBaseActivity;
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
 * Created by Administrator on 2017/8/8.
 */

public class HeTongActivity_Yiban extends HeadBaseActivity {
    @BindView(R.id.hetongName)
    TextView mHetongName;
    @BindView(R.id.bumen)
    TextView mBumen;
    @BindView(R.id.bianhao)
    TextView mBianhao;
    @BindView(R.id.zhuti_jia)
    TextView mZhutiJia;
    @BindView(R.id.zhuti_yi)
    TextView mZhutiYi;
    @BindView(R.id.zhuti_bin)
    TextView mZhutiBin;
    @BindView(R.id.zhuti_ding)
    TextView mZhutiDing;
    @BindView(R.id.canyu_jia)
    TextView mCanyuJia;
    @BindView(R.id.canyu_yi)
    TextView mCanyuYi;
    @BindView(R.id.canyu_bin)
    TextView mCanyuBin;
    @BindView(R.id.canyu_ding)
    TextView mCanyuDing;
    @BindView(R.id.textView2)
    TextView mTextView2;
    @BindView(R.id.date)
    TextView mDate;
    @BindView(R.id.address)
    TextView mAddress;
    @BindView(R.id.qiatan_jinzhan)
    TextView mQiatanJinzhan;
    @BindView(R.id.main_content)
    TextView mMainContent;
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
    @BindView(R.id.btn_caogao)
    Button mBtnCaogao;
    @BindView(R.id.btn_commit)
    Button mBtnCommit;
    @BindView(R.id.linearButton)
    LinearLayout linearButton;
    @BindView((R.id.xxre))
    XXRecycleView mXxre;
    private String mTaskId;
    private String mProcessTaskType;
    private String mUserType;
    private String mDepartmentName;
    private String mDepartmentId;
    private String mSessionId;
    private CommonRecyclerAdapter<ProcessShenheHistoryBean> mAdapter;
    private CommonRecyclerAdapter<ZuzhiUserBean> mHuiqianAdapter;
    private List<ProcessShenheHistoryBean> datas = new ArrayList<>();
    private List<ZuzhiUserBean> datas2 = new ArrayList<>();
    private XXDialog mxxUsersDialog;
    private XXDialog mxxDialog2;

    @Override
    protected int getChildLayoutRes() {
        return R.layout.activity_hetong_shenhe;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("合同流程");
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
        linearButton.setVisibility(View.GONE);
        //判断是否是发起会签节点
        if ("vote".equals(mProcessTaskType)) {
            mBtnCaogao.setText("退回发起人");
            mLlHuiqianyijian.setVisibility(View.VISIBLE);
        } else {
            mBtnCaogao.setText("不同意");
            mLlHuiqianyijian.setVisibility(View.GONE);
        }
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
                    case "退回发起人":
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
                Toast.makeText(HeTongActivity_Yiban.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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
                    mHetongName.setText(shenheBeen.get(0).getValue());
                    mBumen.setText(shenheBeen.get(1).getValue());
                    mBianhao.setText(shenheBeen.get(2).getValue());

                    for (QingjiaShenheBean bean : shenheBeen) {
                        String label = bean.getLabel();
                        String value = bean.getValue();
                        switch (label) {
                            case "name1":
                                mZhutiJia.setText(value);
                                break;
                            case "name2":
                                mZhutiYi.setText(value);
                                break;
                            case "name3":
                                mZhutiBin.setText(value);
                                break;
                            case "name4":
                                mZhutiDing.setText(value);
                                break;
                            case "people1":
                                mCanyuJia.setText(value);
                                break;
                            case "people2":
                                mCanyuYi.setText(value);
                                break;
                            case "people3":
                                mCanyuBin.setText(value);
                                break;
                            case "people4":
                                mCanyuDing.setText(value);
                                break;
                            case "date":
                                mDate.setText(value);
                                break;
                            case "address":
                                mAddress.setText(value);
                                break;
                            case "situation":
                                mQiatanJinzhan.setText(value);
                                break;
                            case "content":
                                mMainContent.setText(value);
                                break;
                        }

                        //当有type为userpicker的时候说明是可以发起会签的节点
                        if ("userpicker".equals(bean.getType())) {
                            mLlHuiqianren.setVisibility(View.VISIBLE);
                            mXxreHuiqianren.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<QingjiaShenheResponse> response) {
                Toast.makeText(HeTongActivity_Yiban.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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
        String name = mHetongName.getText().toString();
        String danwei = mBumen.getText().toString();
        String biahao = mBianhao.getText().toString();


        String name1 = mZhutiJia.getText().toString();
        String name2 = mZhutiYi.getText().toString();
        String name3 = mZhutiBin.getText().toString();
        String name4 = mZhutiDing.getText().toString();

        String people1 = mCanyuJia.getText().toString();
        String people2 = mCanyuYi.getText().toString();
        String people3 = mCanyuBin.getText().toString();
        String people4 = mCanyuDing.getText().toString();

        String date = mDate.getText().toString();
        String address = mAddress.getText().toString();
        String situation = mQiatanJinzhan.getText().toString();
        String mainContent = mMainContent.getText().toString();

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
                .append("\"title\":" + "\"" + name + "\",")
                .append("\"id\":" + "\"" + biahao + "\",")
                .append("\"name1\":" + "\"" + name1 + "\",")
                .append("\"name2\":" + "\"" + name2 + "\",")
                .append("\"name3\":" + "\"" + name3 + "\",")
                .append("\"name4\":" + "\"" + name4 + "\",")

                .append("\"people1\":" + "\"" + people1 + "\",")
                .append("\"people2\":" + "\"" + people2 + "\",")
                .append("\"people3\":" + "\"" + people3 + "\",")
                .append("\"people4\":" + "\"" + people4 + "\",")

                .append("\"situation\":" + "\"" + situation + "\",")
                .append("\"content\":" + "\"" + mainContent + "\",")
                .append("\"date\":" + "\"" + date + "\",")
                .append("\"address\":" + "\"" + address + "\",")
                .append("\"leader_name\":" + "\"" + leadersName.toString() + "\",")
                .append("\"leader\":" + "\"" + leadersID.toString() + "\",")
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
                        Toast.makeText(HeTongActivity_Yiban.this, "流程审核成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(HeTongActivity_Yiban.this, "流程审核失败", Toast.LENGTH_SHORT).show();
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
        if (!TextUtils.isEmpty(yijian)) {
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
                            Toast.makeText(HeTongActivity_Yiban.this, "退回成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }

                @Override
                public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                    Toast.makeText(HeTongActivity_Yiban.this, "流程审核失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFinish(int what) {
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                }
            });
        } else {
            Toast.makeText(this, "请填写会签处理意见", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 请求网络接口，获取组织结构数据
     *
     * @param title
     */
    private void RequestServerGetZuzhi(final String title) {
        //创建请求队列
        RequestQueue ProcessQueue = NoHttp.newRequestQueue();
        //创建请求
        Request<OrganizationResponse> request = new JavaBeanRequest<>(UrlConstance.URL_GET_ZUZHI, RequestMethod.POST, OrganizationResponse.class);
        request.add("partyStructTypeId", "1");
        ProcessQueue.add(0, request, new OnResponseListener<OrganizationResponse>() {

            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<OrganizationResponse> response) {
                Log.w("3333", response.toString());
                if (null != response && null != response.get() && null != response.get().getData()) {
                    if (response.get().getData().get(0).isOpen()) {
                        chooseDate2(response.get().getData().get(0).getChildren(), title);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<OrganizationResponse> response) {
                Toast.makeText(HeTongActivity_Yiban.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {

            }
        });
    }

    /**
     * 选择相关机构
     *
     * @param data
     * @param title
     */
    public void chooseDate2(final List<ChildrenBean> data, final String title) {
        mxxDialog2 = new XXDialog(this, R.layout.dialog_chooselist) {
            @Override
            public void convert(DialogViewHolder holder) {
                XXRecycleView xxre = (XXRecycleView) holder.getView(R.id.dialog_xxre);
                holder.setText(R.id.dialog_title, title);
                xxre.setLayoutManager(new LinearLayoutManager(HeTongActivity_Yiban.this));
                List<ChildrenBean> datas = new ArrayList();
                final CommonRecyclerAdapter<ChildrenBean> adapter = new CommonRecyclerAdapter<ChildrenBean>(HeTongActivity_Yiban.this,
                        datas, R.layout.simple_list_item) {
                    @Override
                    public void convert(CommonViewHolder holder1, final ChildrenBean item, final int i, boolean b) {
                        holder1.setText(R.id.tv, item.getName());
                        if (item.isOpen()) {
                            holder1.getView(R.id.more).setVisibility(View.VISIBLE);
                        } else {
                            holder1.getView(R.id.more).setVisibility(View.GONE);
                        }

                        holder1.getView(R.id.more).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mxxDialog2.dismiss();
                                if (item.isOpen()) {
                                    chooseDate2(item.getChildren(), title);
                                }
                            }
                        });
                    }
                };
                xxre.setAdapter(adapter);
                adapter.replaceAll(data);
                adapter.setOnItemClickListener(new CommonRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClickListener(CommonViewHolder commonViewHolder, int i) {
                        RequestServerGetUsers(adapter.getDatas().get(i).getId(), adapter.getDatas().get(i).getName());
                        mxxDialog2.dismiss();
                    }
                });
            }
        }.showDialog();
    }


    /**
     * 请求网络接口，获取组织结构下的具体人员列表
     *
     * @param partyEntityId
     */
    private void RequestServerGetUsers(long partyEntityId, final String departmentName) {
        //创建请求队列
        RequestQueue ProcessQueue = NoHttp.newRequestQueue();
        //创建请求
        Request<ZuzhiUserListResponse> request = new JavaBeanRequest<>(UrlConstance.URL_GET_ZUZHI_USERS,
                RequestMethod.POST, ZuzhiUserListResponse.class);
        request.add("partyStructTypeId", "1");
        request.add("partyEntityId", partyEntityId + "");
        ProcessQueue.add(0, request, new OnResponseListener<ZuzhiUserListResponse>() {

            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<ZuzhiUserListResponse> response) {
                Log.w("3333", response.toString());
                if (null != response && null != response.get() && null != response.get().getData()) {
                    chooseUsersDate(response.get().getData(), mTvHuiqian, departmentName);
                }
            }

            @Override
            public void onFailed(int what, Response<ZuzhiUserListResponse> response) {
                Toast.makeText(HeTongActivity_Yiban.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {

            }
        });
    }

    /**
     * 选择某部门下的具体人员
     *
     * @param data
     * @param tv
     * @param title
     */
    public void chooseUsersDate(final List<ZuzhiUserBean> data, final TextView tv, final String title) {
        mxxUsersDialog = new XXDialog(this, R.layout.dialog_chooselist) {
            @Override
            public void convert(DialogViewHolder holder) {
                XXRecycleView xxre = (XXRecycleView) holder.getView(R.id.dialog_xxre);
                holder.setText(R.id.dialog_title, title);
                xxre.setLayoutManager(new LinearLayoutManager(HeTongActivity_Yiban.this));
                List<ZuzhiUserBean> datas = new ArrayList();
                final CommonRecyclerAdapter<ZuzhiUserBean> adapter = new CommonRecyclerAdapter<ZuzhiUserBean>(HeTongActivity_Yiban.this,
                        datas, R.layout.simple_list_item) {
                    @Override
                    public void convert(CommonViewHolder holder1, ZuzhiUserBean item, int i, boolean b) {
                        holder1.setText(R.id.tv, item.getName());
                        holder1.getView(R.id.more).setVisibility(View.GONE);
                        holder1.getView(R.id.users).setVisibility(View.GONE);
                        ((ImageView) holder1.getView(R.id.icon)).setImageResource(R.drawable.personal);

                    }
                };
                xxre.setAdapter(adapter);
                adapter.replaceAll(data);
                adapter.setOnItemClickListener(new CommonRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClickListener(CommonViewHolder commonViewHolder, int i) {
//                        mZuzhiUserBean = adapter.getDatas().get(i);
//                        if (mHuiqianAdapter != null) {
//                            boolean flag = false;
//                            for (int t = 0; t < mHuiqianAdapter.getDatas().size(); t++) {
//                                if (mHuiqianAdapter.getDatas().get(t).getId().equals(mZuzhiUserBean.getId())) {
//                                    Toast.makeText(HeTongActivity_shenhe.this, "不要重复添加", Toast.LENGTH_SHORT).show();
//                                    flag = true;
//                                }
//                            }
//                            if (!flag) {
//                                mHuiqianAdapter.add(mZuzhiUserBean);
//                            }
//                        }
//                        if (mZuzhiUserBean != null) {
//                            mxxUsersDialog.dismiss();
//                        }
                    }
                });
            }
        }.showDialog();
    }
}
