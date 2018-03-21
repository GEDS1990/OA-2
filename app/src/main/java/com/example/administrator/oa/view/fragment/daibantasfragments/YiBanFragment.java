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
import com.example.administrator.oa.view.activity.BanwenActivity;
import com.example.administrator.oa.view.activity.BanwenActivity_shenhe;
import com.example.administrator.oa.view.activity.BaoXiaoApplyActivity_shenhe;
import com.example.administrator.oa.view.activity.BaoxiaoActivity;
import com.example.administrator.oa.view.activity.ChuChaiActivity;
import com.example.administrator.oa.view.activity.ChuchaiActivity_shenhe;
import com.example.administrator.oa.view.activity.DizhiyihaoActivity;
import com.example.administrator.oa.view.activity.DubanActivity;
import com.example.administrator.oa.view.activity.Duban_shenhe;
import com.example.administrator.oa.view.activity.FundApplyFeiHetong_shenhe;
import com.example.administrator.oa.view.activity.FundApplyHetong_shenhe;
import com.example.administrator.oa.view.activity.GoodsApplyActivity_shenhe;
import com.example.administrator.oa.view.activity.GudingZichanActivity;
import com.example.administrator.oa.view.activity.GudingzichanApplyActivity_shenhe;
import com.example.administrator.oa.view.activity.HeTongActivity_shenhe;
import com.example.administrator.oa.view.activity.HetongActivity;
import com.example.administrator.oa.view.activity.HouseRent_shenhe;
import com.example.administrator.oa.view.activity.JieKuanActivity;
import com.example.administrator.oa.view.activity.JiekuanApplyActivity_shenhe;
import com.example.administrator.oa.view.activity.NewsFabuActivity_shenhe;
import com.example.administrator.oa.view.activity.NewsfabuActivity;
import com.example.administrator.oa.view.activity.QingJiaActivity;
import com.example.administrator.oa.view.activity.QingJiaActivity_shenhe;
import com.example.administrator.oa.view.activity.QiyeKaoheActivity;
import com.example.administrator.oa.view.activity.QiyeTuizuActivity;
import com.example.administrator.oa.view.activity.QiyeTuizu_shenhe;
import com.example.administrator.oa.view.activity.Qiyekaohe_shenhe;
import com.example.administrator.oa.view.activity.ShouWenActivity;
import com.example.administrator.oa.view.activity.ShouwenProcess_shenhe;
import com.example.administrator.oa.view.activity.TouziXieyiActivity;
import com.example.administrator.oa.view.activity.Touzixieyi_shenhe;
import com.example.administrator.oa.view.activity.WenjianLiuzhuanActivity;
import com.example.administrator.oa.view.activity.Wenjianliuzhuan_shenhe;
import com.example.administrator.oa.view.activity.WorkConnection_shenhe;
import com.example.administrator.oa.view.activity.YongcheActivity;
import com.example.administrator.oa.view.activity.YongcheActivity_shenhe;
import com.example.administrator.oa.view.activity.YongyinActivity;
import com.example.administrator.oa.view.activity.YongyinApplyActivity_shenhe;
import com.example.administrator.oa.view.activity.ZijinActivity;
import com.example.administrator.oa.view.activity.ZijinShenqingFeiHetongActivity;
import com.example.administrator.oa.view.activity.ZijingDiaofaActivity_shenhe;
import com.example.administrator.oa.view.activity.ZufangActivity;
import com.example.administrator.oa.view.activity.workConectionActivity;
import com.example.administrator.oa.view.activity.yiban.BanwenActivity_Yiban;
import com.example.administrator.oa.view.activity.yiban.BaoXiaoApplyActivity_Yiban;
import com.example.administrator.oa.view.activity.yiban.ChuchaiActivity_Yiban;
import com.example.administrator.oa.view.activity.yiban.Duban_Yiban;
import com.example.administrator.oa.view.activity.yiban.FundApplyFeiHetong_Yiban;
import com.example.administrator.oa.view.activity.yiban.GoodsApplyActivity_Yiban;
import com.example.administrator.oa.view.activity.yiban.GudingzichanApplyActivity_Yiban;
import com.example.administrator.oa.view.activity.yiban.HeTongActivity_Yiban;
import com.example.administrator.oa.view.activity.yiban.HouseRent_Yiban;
import com.example.administrator.oa.view.activity.yiban.JiekuanApplyActivity_Yiban;
import com.example.administrator.oa.view.activity.yiban.NewsFabuActivity_Yiban;
import com.example.administrator.oa.view.activity.yiban.QingJiaActivity_Yiban;
import com.example.administrator.oa.view.activity.yiban.QiyeTuizu_Yiban;
import com.example.administrator.oa.view.activity.yiban.Qiyekaohe_Yiban;
import com.example.administrator.oa.view.activity.yiban.ShouwenProcess_Yiban;
import com.example.administrator.oa.view.activity.yiban.Touzixieyi_Yiban;
import com.example.administrator.oa.view.activity.yiban.Wenjianliuzhuan_Yiban;
import com.example.administrator.oa.view.activity.yiban.WorkConnection_Yiban;
import com.example.administrator.oa.view.activity.yiban.YongcheActivity_Yiban;
import com.example.administrator.oa.view.activity.yiban.YongyinApplyActivity_Yiban;
import com.example.administrator.oa.view.activity.yiban.ZijingDiaofaActivity_Yiban;
import com.example.administrator.oa.view.bean.QingjiaShenheBean;
import com.example.administrator.oa.view.bean.QingjiaShenheResponse;
import com.example.administrator.oa.view.bean.TaksDaibanBean;
import com.example.administrator.oa.view.bean.TaksDaibanResponse;
import com.example.administrator.oa.view.constance.UrlConstance;
import com.example.administrator.oa.view.net.JavaBeanRequest;
import com.example.administrator.oa.view.utils.SPUtils;
import com.example.administrator.oa.view.utils.viewutils.ViewUtils;
import com.lsh.XXRecyclerview.CommonRecyclerAdapter;
import com.lsh.XXRecyclerview.CommonViewHolder;
import com.lsh.XXRecyclerview.PullRefreshRecycleView;
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

public class YiBanFragment extends Fragment {

    private String mTitle;
    private XXRecycleView xxre;
    private List<TaksDaibanBean> datas = new ArrayList<>();
    private CommonRecyclerAdapter<TaksDaibanBean> mAdapter;
    private FragmentActivity mActivity;
    private Dialog mLoadingDialog;
    private String formCode;

    public static YiBanFragment getInstance(String title) {
        YiBanFragment sf = new YiBanFragment();
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
        xxre = (XXRecycleView) view.findViewById(R.id.xxre);

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
                String processId = mAdapter.getDatas().get(i - xxre.getHeaderCount()).getProcessId();
                String taskId = mAdapter.getDatas().get(i - xxre.getHeaderCount()).getId();
                String processType = mAdapter.getDatas().get(i - xxre.getHeaderCount()).getPresentationSubject().split("\\-")[0];
//                String formCode = mAdapter.getDatas().get(i - xxre.getHeaderCount()).getFormCode();
//                NeedHuiQian(processId, taskId, processType, formCode);
                getFormCode(processId, taskId, processType);

            }
        });
        mLoadingDialog = ViewUtils.createLoadingDialog(getContext(), "数据处理中...");
        RequestServer(false);
        //下拉刷新
        xxre.setPullRefreshEnabled(true);
        xxre.setOnRefreshListener(new PullRefreshRecycleView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    RequestServer(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void refreshEnd() {
//                Toast.makeText(mainActivity, "刷新数据成功", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    /**
     * 请求网络接口
     */
    private void RequestServer(final boolean isRefresh) {
        String sessionId = SPUtils.getString(getContext(), "sessionId");
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<TaksDaibanResponse> request = new JavaBeanRequest<>(UrlConstance.URL_GET_YIBAN_TASK_LIST,
                RequestMethod.POST, TaksDaibanResponse.class);
        //添加url?key=value形式的参数
        request.addHeader("sessionId", sessionId);
        Queue.add(0, request, new OnResponseListener<TaksDaibanResponse>() {

            @Override
            public void onStart(int what) {
                if (null != mLoadingDialog) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<TaksDaibanResponse> response) {
                Log.w("2222", response.toString());
                if (null != response && null != response.get() && null != response.get().getData()) {
                    List<TaksDaibanBean> beanList = response.get().getData();
                    // 如果没有数据，就显示“暂无数据”
                    if (null == beanList || 0 >= beanList.size()){
                        xxre.setEmptyView(R.layout.emptyview);
                    }
                    if (null != mAdapter) {
                        mAdapter.replaceAll(beanList);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<TaksDaibanResponse> response) {
                Toast.makeText(mActivity, "操作失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (null != mLoadingDialog) {
                    mLoadingDialog.dismiss();
                }
                if (isRefresh) {
                    xxre.stopRefresh();
                }

            }
        });
    }

    private void getFormCode(final String processId, final String taskId, final String processType){
        RequestQueue Queue = NoHttp.newRequestQueue();
        Request<QingjiaShenheResponse> request2 = new JavaBeanRequest<>(UrlConstance.URL_GET_PROCESS_INIT,
                RequestMethod.POST, QingjiaShenheResponse.class);
        //添加url?key=value形式的参数
        request2.add("taskId", taskId);
        Queue.add(0, request2, new OnResponseListener<QingjiaShenheResponse>() {

            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<QingjiaShenheResponse> response) {
                Log.w("3333", response.toString());
                if (null != response && null != response.get() && null != response.get().getData()) {
                    List<QingjiaShenheBean> shenheBeen = response.get().getData();
                    for (QingjiaShenheBean bean : shenheBeen) {
                        if(!TextUtils.isEmpty(bean.getFormName()) && !TextUtils.isEmpty(bean.getFormCode())) {
                            Log.d("FormName", bean.getFormName());
                            Log.d("FormCode", bean.getFormCode());
                            formCode = bean.getFormCode();
                            goFaqiliuchong(processId, taskId, processType, formCode);
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<QingjiaShenheResponse> response) {
            }

            @Override
            public void onFinish(int what) {

            }
        });
    }

    /**
     * 跳入对应的审核界面
     * @param processType
     * @param taskId
     */
    private void goFaqiliuchong(String processId, String taskId, String processType, String formCode) {
        Bundle bundle = new Bundle();
        bundle.putString("formCode", formCode);
        bundle.putString("taskId", taskId);
        bundle.putString("processTaskType", processType);
        switch (processId) {
            case "1":
                readyGo(QingJiaActivity_Yiban.class, bundle);//不会签节点
                break;
            case "3":
                readyGo(JiekuanApplyActivity_Yiban.class, bundle);//0k
                break;
            case "4":
                readyGo(Qiyekaohe_Yiban.class, bundle);
                break;
            case "5":
                readyGo(ChuchaiActivity_Yiban.class, bundle);//不会签节点
                break;
            case "6":
                readyGo(WorkConnection_Yiban.class, bundle);
                break;
            case "7":
                readyGo(QiyeTuizu_Yiban.class, bundle);//0k
                break;
            case "9":
                readyGo(BanwenActivity_Yiban.class, bundle);//0k
                break;
            case "10":
                readyGo(ShouwenProcess_Yiban.class, bundle);//0k
                break;
            case "11":
                readyGo(YongcheActivity_Yiban.class, bundle);//不会签节点
                break;
            case "12":
                readyGo(NewsFabuActivity_Yiban.class, bundle);//0k
                break;
            case "13":
                readyGo(Touzixieyi_Yiban.class, bundle);//0k
                break;
            case "14":
                readyGo(HouseRent_Yiban.class, bundle);//0k
                break;
            case "15":
                readyGo(GoodsApplyActivity_Yiban.class, bundle);//0k
                break;
            case "16":
                readyGo(GudingzichanApplyActivity_Yiban.class, bundle);//0k
                break;
            case "18":
                readyGo(YongyinApplyActivity_Yiban.class, bundle);//0k
                break;
            case "20":
                readyGo(BaoXiaoApplyActivity_Yiban.class, bundle);
                break;
            case "21":
                readyGo(HeTongActivity_Yiban.class, bundle);//0k
                break;
            case "25":
                readyGo(ZijingDiaofaActivity_Yiban.class, bundle);//0k
                break;
            case "27":
                readyGo(FundApplyFeiHetong_Yiban.class, bundle);//0k
                break;
            case "28":
                readyGo(Duban_Yiban.class, bundle);//ok
                break;
            case "30":
                readyGo(Wenjianliuzhuan_Yiban.class, bundle);
                break;
            case "资金申请（合同付款）":
                readyGo(FundApplyHetong_shenhe.class, bundle);//0k
                break;

        }
    }

    /**
     * 打开Activity，可以传递参数，不finish
     *
     * @param clazz
     * @param bundle
     */
    public void readyGo(Class<?> clazz, Bundle bundle) {
        if(null != getContext() && null != clazz) {
            Intent intent = new Intent(getContext(), clazz);
            if (null != bundle) {
                intent.putExtras(bundle);
            }
            startActivity(intent);
        }
    }

    boolean flag = false;

    @Override
    public void onResume() {
        super.onResume();
        RequestServer(false);
    }

    /**
     * 当界面重新展示时（fragment.show）,调用onrequest刷新界面
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        if (!hidden && null != mAdapter) {
            RequestServer(false);
        }
    }
}
