package com.example.administrator.oa.view.fragment.daibantasfragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.activity.BanwenActivity;
import com.example.administrator.oa.view.activity.BaoxiaoActivity;
import com.example.administrator.oa.view.activity.ChuChaiActivity;
import com.example.administrator.oa.view.activity.DizhiyihaoActivity;
import com.example.administrator.oa.view.activity.DubanActivity;
import com.example.administrator.oa.view.activity.GudingZichanActivity;
import com.example.administrator.oa.view.activity.HetongActivity;
import com.example.administrator.oa.view.activity.JieKuanActivity;
import com.example.administrator.oa.view.activity.NewsfabuActivity;
import com.example.administrator.oa.view.activity.QingJiaActivity;
import com.example.administrator.oa.view.activity.QiyeKaoheActivity;
import com.example.administrator.oa.view.activity.QiyeTuizuActivity;
import com.example.administrator.oa.view.activity.ShouWenActivity;
import com.example.administrator.oa.view.activity.TouziXieyiActivity;
import com.example.administrator.oa.view.activity.YongcheActivity;
import com.example.administrator.oa.view.activity.YongyinActivity;
import com.example.administrator.oa.view.activity.ZhaobiaoFileActivity;
import com.example.administrator.oa.view.activity.ZijinActivity;
import com.example.administrator.oa.view.activity.ZijinShenqingActivity;
import com.example.administrator.oa.view.activity.ZijinShenqingFeiHetongActivity;
import com.example.administrator.oa.view.activity.ZufangActivity;
import com.example.administrator.oa.view.activity.workConectionActivity;
import com.example.administrator.oa.view.bean.CaoGaoXiangInfoBean;
import com.example.administrator.oa.view.bean.CaoGaoXiangListBean;
import com.example.administrator.oa.view.bean.MyProcessBean;
import com.example.administrator.oa.view.bean.MyProcessInfoBean;
import com.example.administrator.oa.view.bean.ProcessTaskTypeResponse;
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
 * 草稿箱
 * Created by aawang on 2017/9/19.
 */

public class CaoGaoXiangFragment extends Fragment {

    private String mTitle;
    private List<CaoGaoXiangInfoBean> datas = new ArrayList<>();
    private CommonRecyclerAdapter<CaoGaoXiangInfoBean> mAdapter;
    private FragmentActivity mActivity;
    private Dialog mLoadingDialog;
    private XXRecycleView mXxre;

    public static CaoGaoXiangFragment getInstance(String title) {
        CaoGaoXiangFragment sf = new CaoGaoXiangFragment();
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
        mXxre = (XXRecycleView) view.findViewById(R.id.xxre);

        mActivity = getActivity();

        mXxre.setLayoutManager(new LinearLayoutManager(getContext()));
        mXxre.setEmptyView(R.layout.emptyview);
        mXxre.setPullRefreshEnabled(true);
        mAdapter = new CommonRecyclerAdapter<CaoGaoXiangInfoBean>(getContext(), datas, R.layout.item_simple_text) {
            @Override
            public void convert(CommonViewHolder holder, CaoGaoXiangInfoBean item, int i, boolean b) {
                holder.setText(R.id.text, item.getName()+"-"+item.getCreateTime());
            }
        };
        mXxre.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new CommonRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(CommonViewHolder commonViewHolder, int i) {
                String processId = mAdapter.getDatas().get(i - mXxre.getHeaderCount()).getProcessId();
                String processDefinitionId = mAdapter.getDatas().get(i - mXxre.getHeaderCount()).getProcessDefinitionId();
                String businessKey = mAdapter.getDatas().get(i - mXxre.getHeaderCount()).getBusinessKey();
                GoFaqiliuchong(processId, processDefinitionId, businessKey);
            }
        });
        mLoadingDialog = ViewUtils.createLoadingDialog(getContext(), "数据处理中...");
        RequestServer(false);
        mXxre.setOnRefreshListener(new PullRefreshRecycleView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.w("5544", "onRefresh");
                RequestServer(true);
            }

            @Override
            public void refreshEnd() {
                Log.w("5544", "refreshEnd");
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
        final Request<CaoGaoXiangListBean> request = new JavaBeanRequest<>(UrlConstance.URL_CAOGAOXIANG,
                RequestMethod.POST, CaoGaoXiangListBean.class);
        //添加url?key=value形式的参数
        request.addHeader("sessionId", sessionId);
        Queue.add(0, request, new OnResponseListener<CaoGaoXiangListBean>() {

            @Override
            public void onStart(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<CaoGaoXiangListBean> response) {
                Log.w("2222", response.toString());
                if (null != response && null != response.get() && null != response.get().getData()) {
                    List<CaoGaoXiangInfoBean> beanList = response.get().getData();
                    if (mAdapter != null) {
                        mAdapter.replaceAll(beanList);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<CaoGaoXiangListBean> response) {
                Toast.makeText(mActivity, "操作失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
                if (isRefresh) {
                    mXxre.stopRefresh();
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
        startActivityForResult(intent, 0);
    }

    private void GoFaqiliuchong(String processId, String processDefinitionId, String businessKey) {
        Bundle bundle = new Bundle();
//        bundle.putString("processId", processId);
        bundle.putString("processDefinitionId", processDefinitionId);
        bundle.putString("businessKey", businessKey);
        switch (processId) {
            // 请假流程
            case "1":
                readyGo(QingJiaActivity.class, bundle);//ok-
                break;
            // 借款流程
            case "3":
                readyGo(JieKuanActivity.class, bundle);//ok-
                break;
            case "5":
                readyGo(ChuChaiActivity.class, bundle);//ok-
                break;
            case "6":
                readyGo(workConectionActivity.class, bundle);//ok-
                break;
            case "9":
                readyGo(BanwenActivity.class, bundle);//ok-
                break;
            case "11":
                readyGo(YongcheActivity.class, bundle);//ok-
                break;
            case "12":
                readyGo(NewsfabuActivity.class, bundle);//ok-
                break;
            case "15":
                readyGo(DizhiyihaoActivity.class, bundle);//ok-
                break;
            case "16":
                readyGo(GudingZichanActivity.class, bundle);//ok-
                break;
            case "18":
                readyGo(YongyinActivity.class, bundle);//ok-
                break;
            case "20":
                readyGo(BaoxiaoActivity.class, bundle);//ok-
                break;
            case "21":
                readyGo(HetongActivity.class, bundle);//ok-
                break;
            case "25":
                readyGo(ZijinActivity.class, bundle);//ok-
                break;
            case "4":
                readyGo(QiyeKaoheActivity.class, bundle);//ok-
                break;
            case "7":
                readyGo(QiyeTuizuActivity.class, bundle);//ok-
                break;
            case "14":
                readyGo(ZufangActivity.class, bundle);//ok-
                break;
            case "资金申请（合同付款）":
                readyGo(ZijinShenqingActivity.class, bundle);//ok-
                break;
            case "27":
                readyGo(ZijinShenqingFeiHetongActivity.class, bundle);//ok-
                break;
            case "28":
                readyGo(DubanActivity.class, bundle);//ok-
                break;
            case "10":
                readyGo(ShouWenActivity.class, bundle);//ok-
                break;
            case "13":
                readyGo(TouziXieyiActivity.class, bundle);//ok-
                break;
            case "招投标文件":
                readyGo(ZhaobiaoFileActivity.class, bundle);//ok
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        RequestServer(false);
    }
}
