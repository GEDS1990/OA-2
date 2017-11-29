package com.example.administrator.oa.view.fragment.myprocessfragments;

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
 * Created by Administrator on 2017/9/19.
 */

public class CompleteProFragment extends Fragment {

    private String mTitle;
    private List<MyProcessInfoBean> datas = new ArrayList<>();
    private CommonRecyclerAdapter<MyProcessInfoBean> mAdapter;
    private FragmentActivity mActivity;
    private Dialog mLoadingDialog;
    private XXRecycleView mXxre;

    public static CompleteProFragment getInstance(String title) {
        CompleteProFragment sf = new CompleteProFragment();
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
        mAdapter = new CommonRecyclerAdapter<MyProcessInfoBean>(getContext(), datas, R.layout.item_simple_text) {
            @Override
            public void convert(CommonViewHolder holder, MyProcessInfoBean item, int i, boolean b) {
                holder.setText(R.id.text, item.getName());
            }
        };
        mXxre.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new CommonRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(CommonViewHolder commonViewHolder, int i) {
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
    private void RequestServer(final boolean isRefresh) {
        String sessionId = SPUtils.getString(getContext(), "sessionId");
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<MyProcessBean> request = new JavaBeanRequest<>(UrlConstance.URL_COMPLETE_PRO,
                RequestMethod.POST, MyProcessBean.class);
        //添加url?key=value形式的参数
        request.addHeader("sessionId", sessionId);
        Queue.add(0, request, new OnResponseListener<MyProcessBean>() {

            @Override
            public void onStart(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<MyProcessBean> response) {
                Log.w("2222", response.toString());
                if (null != response && null != response.get() && null != response.get().getData()) {
                    List<MyProcessInfoBean> beanList = response.get().getData();
                    if (mAdapter != null) {
                        mAdapter.replaceAll(beanList);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<MyProcessBean> response) {
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
        startActivity(intent);
    }

}
