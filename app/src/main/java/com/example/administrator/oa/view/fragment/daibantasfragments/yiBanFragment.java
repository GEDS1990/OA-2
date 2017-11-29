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

public class yiBanFragment extends Fragment {

    private String mTitle;
    private List<TaksDaibanBean> datas = new ArrayList<>();
    private CommonRecyclerAdapter<TaksDaibanBean> mAdapter;
    private XXRecycleView mXxre;
    private FragmentActivity mActivity;
    private Dialog mLoadingDialog;

    public static yiBanFragment getInstance(String title) {
        yiBanFragment sf = new yiBanFragment();
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
        mXxre.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new CommonRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(CommonViewHolder commonViewHolder, int i) {
//                Intent intent = new Intent(getContext(), MsgDetailActivity.class);
//                startActivity(intent);
            }
        });
        mLoadingDialog = ViewUtils.createLoadingDialog(getContext(), "数据处理中...");
        RequestServer();
        return view;
    }

    /**
     * 请求网络接口
     */
    private void RequestServer() {
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
