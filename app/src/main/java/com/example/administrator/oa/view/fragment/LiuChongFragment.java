package com.example.administrator.oa.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.activity.BanwenActivity;
import com.example.administrator.oa.view.activity.BaoxiaoActivity;
import com.example.administrator.oa.view.activity.ChuChaiActivity;
import com.example.administrator.oa.view.activity.DaibanTaskActivity;
import com.example.administrator.oa.view.activity.DizhiyihaoActivity;
import com.example.administrator.oa.view.activity.DubanActivity;
import com.example.administrator.oa.view.activity.GudingZichanActivity;
import com.example.administrator.oa.view.activity.HetongActivity;
import com.example.administrator.oa.view.activity.JieKuanActivity;
import com.example.administrator.oa.view.activity.MainActivity;
import com.example.administrator.oa.view.activity.MyLiuChongActivity;
import com.example.administrator.oa.view.activity.NewsfabuActivity;
import com.example.administrator.oa.view.activity.QingJiaActivity;
import com.example.administrator.oa.view.activity.QiyeKaoheActivity;
import com.example.administrator.oa.view.activity.QiyeTuizuActivity;
import com.example.administrator.oa.view.activity.ShouWenActivity;
import com.example.administrator.oa.view.activity.TouziXieyiActivity;
import com.example.administrator.oa.view.activity.WenjianLiuzhuanActivity;
import com.example.administrator.oa.view.activity.YongcheActivity;
import com.example.administrator.oa.view.activity.YongyinActivity;
import com.example.administrator.oa.view.activity.ZhaobiaoFileActivity;
import com.example.administrator.oa.view.activity.ZijinActivity;
import com.example.administrator.oa.view.activity.ZijinShenqingActivity;
import com.example.administrator.oa.view.activity.ZijinShenqingFeiHetongActivity;
import com.example.administrator.oa.view.activity.ZufangActivity;
import com.example.administrator.oa.view.activity.workConectionActivity;
import com.example.administrator.oa.view.bean.LiuChongDingyiBean;
import com.example.administrator.oa.view.bean.ProcessDetailBean;
import com.example.administrator.oa.view.bean.ProcessListResponse;
import com.example.administrator.oa.view.bean.ProcessTypeListBean;
import com.example.administrator.oa.view.bean.UndoTaskNumResponse;
import com.example.administrator.oa.view.constance.UrlConstance;
import com.example.administrator.oa.view.constance.commonConstance;
import com.example.administrator.oa.view.net.JavaBeanRequest;
import com.example.administrator.oa.view.utils.SPUtils;
import com.example.administrator.oa.view.wiget.arl.AutoRollLayout;
import com.example.administrator.oa.view.wiget.arl.ObservableScrollView;
import com.example.administrator.oa.view.wiget.arl.RollItem;
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
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/3/21.
 */
public class LiuChongFragment extends BaseFragment {

    @BindView(R.id.head)
    ImageView mHead;
    @BindView(R.id.btn1)
    RelativeLayout mBtn1;
    @BindView(R.id.btn2)
    RelativeLayout mBtn2;
    @BindView(R.id.arl)
    AutoRollLayout mArl;
    @BindView(R.id.txtImgRed)
    TextView txtImgRed;
    @BindView(R.id.xxre_common_liuchong)
    XXRecycleView mXxreCommonLiuchong;
    @BindView(R.id.scrollView)
    ObservableScrollView mScrollView;
    List<ProcessTypeListBean> datas = new ArrayList<>();
    private CommonRecyclerAdapter<ProcessTypeListBean> mCommonAdapter;
    private List<LiuChongDingyiBean> mTempProcessLsit;
    Unbinder unbinder;
    private MainActivity mActivity;
    View mChildHeadView;
    LinearLayout myStatusBar;
    // 消息未读件数
    private String count;
    @Override
    protected int getChildLayoutRes() {
        return R.layout.fragment_manager;
    }

    @Override
    protected void initView(View childHeadView, RelativeLayout rlBaseheaderBack, RelativeLayout rlBaseheaderHeader,
                            RelativeLayout rlBaseheaderRight, View childView, LinearLayout myStatusBar) {
        this.mChildHeadView = childHeadView;
        this.myStatusBar = myStatusBar;
        mChildHeadView.setVisibility(View.GONE);
        myStatusBar.setVisibility(View.GONE);
        ((TextView) rlBaseheaderHeader.getChildAt(0)).setText("工作台");
        mActivity = ((MainActivity) getActivity());
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        initArl();
        initThisView();
        mXxreCommonLiuchong.setFocusable(false);
    }

    List<RollItem> rollItems = new ArrayList<>();

    private void initArl() {
        rollItems.add(new RollItem(R.drawable.arl1, ""));
        rollItems.add(new RollItem(R.drawable.arl2, ""));
        rollItems.add(new RollItem(R.drawable.arl3, ""));
        rollItems.add(new RollItem(R.drawable.arl4, ""));
        rollItems.add(new RollItem(R.drawable.arl5, ""));
        rollItems.add(new RollItem(R.drawable.arl6, ""));
        mArl.setItems(rollItems);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mArl.setAutoRoll(true);
        getTaskNum();
    }

    @Override
    public void onPause() {
        super.onPause();
        mArl.setAutoRoll(false);
    }

    @OnClick({R.id.btn1, R.id.btn2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                readyGo(DaibanTaskActivity.class);
                break;
            case R.id.btn2:
                readyGo(MyLiuChongActivity.class);
                break;
        }
    }

    private void initThisView() {
        // 获取工作台数据
        RequestServerLogin();
        // 获取待办任务数
        getTaskNum();
        // 加载数据
        setDatas();
    }

    /**
     * 请求流程定义接口
     */
    private void RequestServerLogin() {
        //departmentName,departments
        String departmentName = SPUtils.getString(mActivity, "departmentName");
        String departments = SPUtils.getString(mActivity, "departments");

        //创建请求队列
        RequestQueue ProcessQueue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessListResponse> request = new JavaBeanRequest(UrlConstance.URL_PROCESS_DEFIN,
                RequestMethod.POST, ProcessListResponse.class);
        request.addHeader("sessionId", SPUtils.getString(mActivity, "sessionId"));
        request.add("departmentName", departmentName);
        request.add("departments", departments);
        ProcessQueue.add(0, request, new OnResponseListener<ProcessListResponse>() {

            @Override
            public void onStart(int what) {
                if (null != mLoadingDialog) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<ProcessListResponse> response) {
                Log.w("LiuchongDingyiActivity", response.toString());
                if (null != response && null != response.get() && null != response.get().getData()) {
                    List<ProcessTypeListBean> procssDefinList = response.get().getData();
                    if (null != mCommonAdapter) {
                        mCommonAdapter.replaceAll(procssDefinList);
                        mCommonAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessListResponse> response) {
                Toast.makeText(mActivity, "获取信息失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (null != mLoadingDialog) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }

    /**
     * 获取待办任务数
     */
    private void getTaskNum() {
        //创建请求队列
        RequestQueue ProcessQueue = NoHttp.newRequestQueue();
        //创建请求
        Request<UndoTaskNumResponse> request = new JavaBeanRequest(UrlConstance.URL_GET_UNDOTASKNUM,
                RequestMethod.POST, UndoTaskNumResponse.class);
        request.addHeader("sessionId", SPUtils.getString(mActivity, "sessionId"));
        ProcessQueue.add(0, request, new OnResponseListener<UndoTaskNumResponse>() {

            @Override
            public void onStart(int what) {
                if (null != mLoadingDialog) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<UndoTaskNumResponse> response) {
                Log.w("getTaskNum", response.toString());
                if (null != response && null != response.get() && null != response.get().getData()) {
                    if(!"0".equals(response.get().getData().get(0).getCount())) {
                        txtImgRed.setText(response.get().getData().get(0).getCount());
                        txtImgRed.setVisibility(View.VISIBLE);
                    } else {
                        txtImgRed.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<UndoTaskNumResponse> response) {
                response.getException();
            }

            @Override
            public void onFinish(int what) {
                if (null != mLoadingDialog) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }

    private void setDatas(){
        //常用流程
        mXxreCommonLiuchong.setLayoutManager(new LinearLayoutManager(mActivity));
        mCommonAdapter = new CommonRecyclerAdapter<ProcessTypeListBean>(mActivity, datas, R.layout.item_liuchong_list) {
            @Override
            public void convert(CommonViewHolder holder, ProcessTypeListBean item, int position, boolean b) {
                holder.setText(R.id.title, item.getType());
                XXRecycleView xxRe = (XXRecycleView) holder.getView(R.id.xxre);
                Log.d("LiuCheng", item.getType());
                if ("公共流程".equals(item.getType())) {
                    xxRe.setLayoutManager(new GridLayoutManager(mActivity, 2));
                } else {
                    xxRe.setLayoutManager(new LinearLayoutManager(mActivity));
                }
                ArrayList<ProcessDetailBean> prodatas = new ArrayList<>();
                final CommonRecyclerAdapter<ProcessDetailBean> adapter = new CommonRecyclerAdapter<ProcessDetailBean>
                        (mActivity, prodatas, R.layout.item_liuchong) {
                    @Override
                    public void convert(CommonViewHolder holder, ProcessDetailBean item, int position, boolean b) {
                        holder.setText(R.id.name, item.getName());
                        for (int i = 0; i < commonConstance.liuchong.length; i++) {
                            if (getString(commonConstance.liuchong[i][0]).equals(item.getName())) {
                                holder.getView(R.id.icon).setBackgroundResource(commonConstance.liuchong[i][2]);
                            }
                        }
                    }
                };
                xxRe.setAdapter(adapter);
                adapter.replaceAll(item.getProcess());
                adapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClickListener(CommonViewHolder viewHolder, int i) {
                        String processType = adapter.getDatas().get(i).getName();
                        String processDefinitionId = adapter.getDatas().get(i).getProcessDefinitionId();
                        GoFaqiliuchong(processType, processDefinitionId);
                    }
                });

            }
        };
        mXxreCommonLiuchong.setAdapter(mCommonAdapter);
    }

    /**
     * 跳入对应的界面
     * @param processType
     * @param processDefinitionId
     */
    private void GoFaqiliuchong(String processType, String processDefinitionId) {
        Bundle bundle = new Bundle();
        bundle.putString("processDefinitionId", processDefinitionId);
        switch (processType) {
            case "请假流程":
                readyGo(QingJiaActivity.class, bundle);//ok-
                //readyGo(QingJiaActivity2.class, bundle);//ok-
                break;
            case "出差申请":
                readyGo(ChuChaiActivity.class, bundle);//ok-
                break;
            case "工作联系":
                readyGo(workConectionActivity.class, bundle);//ok-
                break;
            case "办文流程":
                readyGo(BanwenActivity.class, bundle);//ok-
                break;
            case "用车申请":
                readyGo(YongcheActivity.class, bundle);//ok-
                break;
            case "新闻发布":
                readyGo(NewsfabuActivity.class, bundle);//ok-
                break;
            case "低值易耗品领用":
                readyGo(DizhiyihaoActivity.class, bundle);//ok-
                break;
            case "固定资产领用":
                readyGo(GudingZichanActivity.class, bundle);//ok-
                break;
            case "用印申请":
                readyGo(YongyinActivity.class, bundle);//ok-
                break;
            case "借款申请":
                readyGo(JieKuanActivity.class, bundle);//ok-
                break;
            case "报销流程":
                readyGo(BaoxiaoActivity.class, bundle);//ok-
                break;
            case "合同流程":
                readyGo(HetongActivity.class, bundle);//ok-
                break;
            case "资金调拨":
                readyGo(ZijinActivity.class, bundle);//ok-
                break;
            case "企业考核":
                readyGo(QiyeKaoheActivity.class, bundle);//ok-
                break;
            case "企业退租":
                readyGo(QiyeTuizuActivity.class, bundle);//ok-
                break;
            case "租房流程":
                readyGo(ZufangActivity.class, bundle);//ok-
                break;
            case "资金申请（合同付款）":
                readyGo(ZijinShenqingActivity.class, bundle);//ok-
                break;
            case "资金申请（非合同）":
                readyGo(ZijinShenqingFeiHetongActivity.class, bundle);//ok-
                break;
            case "督办流程":
                readyGo(DubanActivity.class, bundle);//ok-
                break;
            case "收文流程":
                readyGo(ShouWenActivity.class, bundle);//ok-
                break;
            case "投资协议流程":
                readyGo(TouziXieyiActivity.class, bundle);//ok-
                break;
            case "招投标文件":
                readyGo(ZhaobiaoFileActivity.class, bundle);//ok
                break;
            case "文件流转":
                readyGo(WenjianLiuzhuanActivity.class, bundle);//ok
                break;
        }
    }

    /**
     * 当界面重新展示时（fragment.show）,调用onrequest刷新界面
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        if (!hidden && null != mCommonAdapter && 0 >= mCommonAdapter.getDatas().size()) {
            RequestServerLogin();
            // 获取待办任务数
            getTaskNum();
        }
    }


}

