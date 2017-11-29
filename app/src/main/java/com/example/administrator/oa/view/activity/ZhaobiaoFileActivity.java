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
 * Created by Administrator on 2017/8/25.
 */

public class ZhaobiaoFileActivity extends HeadBaseActivity {
    @BindView(R.id.textView3)
    TextView mTextView3;
    @BindView(R.id.name)
    EditText mName;
    @BindView(R.id.fuwuqi)
    EditText mFuwuqi;
    @BindView(R.id.paytiaojian)
    EditText mPaytiaojian;
    @BindView(R.id.banfa)
    EditText mBanfa;
    @BindView(R.id.textView4)
    TextView mTextView4;
    @BindView(R.id.kongzhijia)
    EditText mKongzhijia;
    @BindView(R.id.type)
    EditText mType;
    @BindView(R.id.tv_huiqian)
    TextView mTvHuiqian;
    @BindView(R.id.ll_huiqian)
    LinearLayout mLlHuiqian;
    @BindView(R.id.tv_huiqianre)
    TextView mTvHuiqianre;
    @BindView(R.id.ll_huiqianren)
    LinearLayout mLlHuiqianren;
    @BindView(R.id.xxre_huiqianren)
    XXRecycleView mXxreHuiqianren;
    @BindView(R.id.btn_caogao)
    Button mBtnCaogao;
    @BindView(R.id.btn_commit)
    Button mBtnCommit;
    private String mSessionId;
    private String mUserName;
    private String mUserId;
    private String mDepartmentId;
    private String mDepartmentName;
    private String mProcessId;
    private CommonRecyclerAdapter<ZuzhiUserBean> mHuiqianAdapter;
    private List<ZuzhiUserBean> datas2 = new ArrayList<>();
    private XXDialog mxxDialog2;
    private XXDialog mxxUsersDialog;

    @Override
    protected int getChildLayoutRes() {
        return R.layout.activity_zhaobiaofile;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("投资协议申报");
        initThisView();
    }

    private void initThisView() {
        mSessionId = SPUtils.getString(this, "sessionId");
        mUserName = SPUtils.getString(this, "userName");
        mUserId = SPUtils.getString(this, "userId");
        mDepartmentId = SPUtils.getString(this, "departmentId");
        mDepartmentName = SPUtils.getString(this, "departmentName");
        mProcessId = getIntent().getStringExtra("processDefinitionId");

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

    @OnClick({R.id.ll_huiqian, R.id.ll_huiqianren, R.id.btn_caogao, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_huiqian:
                List<String> datas_gongju = new ArrayList();
                datas_gongju.add("是");
                datas_gongju.add("否");
                chooseDate(datas_gongju, mTvHuiqian, "是否转发给大领导");
                break;
            case R.id.ll_huiqianren:
//                RequestServerGetZuzhi("选择会签人");
                if("0".equals(mLlHuiqianren.getTag())) {
                    mLlHuiqianren.setTag("1");
                    RequestServerGetZuzhi(mLlHuiqianren, mTvHuiqianre, "请选择会签人", mHuiqianAdapter);
                }
                break;
            case R.id.btn_caogao:
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
        if (TextUtils.isEmpty(mName.getText().toString().trim())) {
            Toast.makeText(this, "请填项目名称", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mFuwuqi.getText().toString().trim())) {
            Toast.makeText(this, "请填写服务期", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mPaytiaojian.getText().toString().trim())) {
            Toast.makeText(this, "请填写付款条件", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mBanfa.getText().toString().trim())) {
            Toast.makeText(this, "请填写评判办法", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mKongzhijia.getText().toString().trim())) {
            Toast.makeText(this, "请填写控制价", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mType.getText().toString().trim())) {
            Toast.makeText(this, "请填写类型", Toast.LENGTH_SHORT).show();
        } else {
            RequestServerGoodsLingqu(
                    mName.getText().toString().trim(),
                    mFuwuqi.getText().toString().trim(),
                    mPaytiaojian.getText().toString().trim(),
                    mBanfa.getText().toString().trim(),
                    mKongzhijia.getText().toString().trim(),
                    mType.getText().toString().trim()
            );
        }
    }

    /**
     * 请求网络，提交数据b
     */
    private void RequestServerGoodsLingqu(String name, String fuwuqu, String paytiaojian, String banfa, String kongzhijia, String type) {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_STARTPROCESS,
                RequestMethod.POST, ProcessJieguoResponse.class);

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
                .append("\"name\":" + "\"" + name + "\",")
                .append("\"term\":" + "\"" + fuwuqu + "\",")
                .append("\"pay\":" + "\"" + paytiaojian + "\",")
                .append("\"way\":" + "\"" + banfa + "\",")
                .append("\"price\":" + "\"" + kongzhijia + "\",")
                .append("\"type\":" + "\"" + kongzhijia + "\",")
                .append("\"comment\":" + "\"" + type + "\",")
                .append("\"leader_name\":" + "\"" + leadersName.toString() + "\",")
                .append("\"leader\":" + "\"" + leadersID.toString() + "\"")
                .append("}");
        //添加url?key=value形式的参数
        request.addHeader("sessionId", mSessionId);
        request.add("processDefinitionId", mProcessId);
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
                        Toast.makeText(ZhaobiaoFileActivity.this, "流程发起成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(ZhaobiaoFileActivity.this, "流程发起失败", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ZhaobiaoFileActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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
                xxre.setLayoutManager(new LinearLayoutManager(ZhaobiaoFileActivity.this));
                List<ChildrenBean> datas = new ArrayList();
                final CommonRecyclerAdapter<ChildrenBean> adapter = new CommonRecyclerAdapter<ChildrenBean>(ZhaobiaoFileActivity.this,
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
                Toast.makeText(ZhaobiaoFileActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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
                xxre.setLayoutManager(new LinearLayoutManager(ZhaobiaoFileActivity.this));
                List<ZuzhiUserBean> datas = new ArrayList();
                final CommonRecyclerAdapter<ZuzhiUserBean> adapter = new CommonRecyclerAdapter<ZuzhiUserBean>(ZhaobiaoFileActivity.this,
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
//                                    Toast.makeText(ZhaobiaoFileActivity.this, "不要重复添加", Toast.LENGTH_SHORT).show();
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
