package com.example.administrator.oa.view.fragment.daibantasfragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.activity.BanwenActivity_shenhe;
import com.example.administrator.oa.view.activity.BaoXiaoApplyActivity_shenhe;
import com.example.administrator.oa.view.activity.ChuchaiActivity_shenhe;
import com.example.administrator.oa.view.activity.Duban_shenhe;
import com.example.administrator.oa.view.activity.FundApplyFeiHetong_shenhe;
import com.example.administrator.oa.view.activity.FundApplyHetong_shenhe;
import com.example.administrator.oa.view.activity.GoodsApplyActivity_shenhe;
import com.example.administrator.oa.view.activity.GudingzichanApplyActivity_shenhe;
import com.example.administrator.oa.view.activity.HeTongActivity_shenhe;
import com.example.administrator.oa.view.activity.HouseRent_shenhe;
import com.example.administrator.oa.view.activity.JiekuanApplyActivity_shenhe;
import com.example.administrator.oa.view.activity.NewsFabuActivity_shenhe;
import com.example.administrator.oa.view.activity.QingJiaActivity_shenhe;
import com.example.administrator.oa.view.activity.QiyeTuizu_shenhe;
import com.example.administrator.oa.view.activity.Qiyekaohe_shenhe;
import com.example.administrator.oa.view.activity.ShouwenProcess_shenhe;
import com.example.administrator.oa.view.activity.Touzixieyi_shenhe;
import com.example.administrator.oa.view.activity.WorkConnection_shenhe;
import com.example.administrator.oa.view.activity.YongcheActivity_shenhe;
import com.example.administrator.oa.view.activity.YongyinApplyActivity_shenhe;
import com.example.administrator.oa.view.activity.ZijingDiaofaActivity_shenhe;
import com.example.administrator.oa.view.bean.ProcessTaskTypeResponse;
import com.example.administrator.oa.view.bean.TaksDaibanBean;
import com.example.administrator.oa.view.bean.TaksDaibanResponse;
import com.example.administrator.oa.view.constance.UrlConstance;
import com.example.administrator.oa.view.net.JavaBeanRequest;
import com.example.administrator.oa.view.utils.SPUtils;
import com.example.administrator.oa.view.utils.viewutils.ViewUtils;
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

/**
 * Created by Administrator on 2017/6/28.
 */

public class daiBanFragment extends Fragment {

    private String mTitle;
    private List<TaksDaibanBean> datas = new ArrayList<>();
    private CommonRecyclerAdapter<TaksDaibanBean> mAdapter;
    private FragmentActivity mActivity;
    private Dialog mLoadingDialog;

    public static daiBanFragment getInstance(String title) {
        daiBanFragment sf = new daiBanFragment();
        sf.mTitle = title;
        return sf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_simple_card, null);
        XXRecycleView xxre = (XXRecycleView) view.findViewById(R.id.xxre);

        mActivity = getActivity();

        xxre.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new CommonRecyclerAdapter<TaksDaibanBean>(getContext(), datas, R.layout.item_daibantask) {
            @Override
            public void convert(CommonViewHolder holder, TaksDaibanBean item, int i, boolean b) {
                holder.setText(R.id.name, item.getAssigneeDisplayName());
                holder.setText(R.id.date, item.getCreateTime());
                holder.setText(R.id.taskname, item.getName());
                holder.setText(R.id.msg_content, item.getPresentationSubject());

                if (!TextUtils.isEmpty(item.getAssigneeDisplayName())) {
                    String firstStr = String.valueOf(item.getAssigneeDisplayName().charAt(0));
                    holder.setText(R.id.iv, firstStr);
                }
            }
        };
        xxre.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new CommonRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(CommonViewHolder commonViewHolder, int i) {
                String taskId = mAdapter.getDatas().get(i).getId();
                String processType = mAdapter.getDatas().get(i).getPresentationSubject().split("\\-")[0];
                NeedHuiQian(taskId, processType);
            }
        });
        mLoadingDialog = ViewUtils.createLoadingDialog(getContext(), "数据处理中...");
        RequestServer();
        return view;
    }

    /**
     * 判断当前节点类型
     *
     * @param taskId
     */
    private void NeedHuiQian(final String taskId, final String processType) {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        final Request<ProcessTaskTypeResponse> request = new JavaBeanRequest<>(UrlConstance.URL_PROCESS_TASKTYPE,
                RequestMethod.POST, ProcessTaskTypeResponse.class);
        //添加url?key=value形式的参数
        request.add("taskId", taskId);
        Queue.add(0, request, new OnResponseListener<ProcessTaskTypeResponse>() {
            @Override
            public void onStart(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<ProcessTaskTypeResponse> response) {
                Log.w("2222", response.toString());
                if (response != null && response.get() != null && response.get().getData() != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("taskId", taskId);
                    bundle.putString("processTaskType", response.get().getData().getType());
                    switch (processType) {
                        case "请假流程":
                            readyGo(QingJiaActivity_shenhe.class, bundle);//不会签节点
                            break;
                        case "出差申请":
                            readyGo(ChuchaiActivity_shenhe.class, bundle);//不会签节点
                            break;
                        case "工作联系":
                            readyGo(WorkConnection_shenhe.class, bundle);
                            break;
                        case "办文流程":
                            readyGo(BanwenActivity_shenhe.class, bundle);//0k
                            break;
                        case "用车申请":
                            readyGo(YongcheActivity_shenhe.class, bundle);//不会签节点
                            break;
                        case "新闻发布":
                            readyGo(NewsFabuActivity_shenhe.class, bundle);//0k
                            break;
                        case "低值易耗品领用":
                            readyGo(GoodsApplyActivity_shenhe.class, bundle);//0k
                            break;
                        case "固定资产领用":
                            readyGo(GudingzichanApplyActivity_shenhe.class, bundle);//0k
                            break;
                        case "用印申请流程":
                            readyGo(YongyinApplyActivity_shenhe.class, bundle);//0k
                            break;
                        case "借款申请":
                            readyGo(JiekuanApplyActivity_shenhe.class, bundle);//0k
                            break;
                        case "报销流程":
                            readyGo(BaoXiaoApplyActivity_shenhe.class, bundle);
                            break;
                        case "普通合同":
                            readyGo(HeTongActivity_shenhe.class, bundle);//0k
                            break;
                        case "资金调拨":
                            readyGo(ZijingDiaofaActivity_shenhe.class, bundle);//0k
                            break;
                        case "企业考核":
                            readyGo(Qiyekaohe_shenhe.class, bundle);
                            break;
                        case "企业退租":
                            readyGo(QiyeTuizu_shenhe.class, bundle);//0k
                            break;
                        case "租房流程":
                            readyGo(HouseRent_shenhe.class, bundle);//0k
                            break;
                        case "督办流程":
                            readyGo(Duban_shenhe.class, bundle);//ok
                            break;
                        case "收文流程":
                            readyGo(ShouwenProcess_shenhe.class, bundle);//0k
                            break;
                        case "资金申请（合同付款）":
                            readyGo(FundApplyHetong_shenhe.class, bundle);//0k
                            break;
                        case "资金申请（非合同付款）":
                            readyGo(FundApplyFeiHetong_shenhe.class, bundle);//0k
                            break;
                        case "投资协议流程":
                            readyGo(Touzixieyi_shenhe.class, bundle);//0k
                            break;
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessTaskTypeResponse> response) {
                Toast.makeText(mActivity, "获取流程节点类型失败", Toast.LENGTH_SHORT).show();
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
     * 请求网络接口
     */
    private void RequestServer() {
        String sessionId = SPUtils.getString(getContext(), "sessionId");
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<TaksDaibanResponse> request = new JavaBeanRequest<>(UrlConstance.URL_GET_DAIBAN_TASK_LIST,
                RequestMethod.POST, TaksDaibanResponse.class);
        //添加url?key=value形式的参数
        request.addHeader("sessionId", sessionId);
        Queue.add(0, request, new OnResponseListener<TaksDaibanResponse>() {

            @Override
            public void onStart(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<TaksDaibanResponse> response) {
                Log.w("2222", response.toString());
                if (null != response && null != response.get() && null != response.get().getData()) {
                    flag = true;
                    List<TaksDaibanBean> beanList = response.get().getData();
                    if (mAdapter != null) {
                        mAdapter.replaceAll(beanList);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<TaksDaibanResponse> response) {
                Toast.makeText(mActivity, "操作失败", Toast.LENGTH_SHORT).show();
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
     * 打开Activity，可以传递参数，不finish
     *
     * @param clazz
     * @param bundle
     */
    public void readyGo(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(getContext(), clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    boolean flag = false;

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null && flag) {
            RequestServer();
        }
    }
}
