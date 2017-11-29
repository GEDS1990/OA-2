package com.example.administrator.oa.view.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.bean.GgListResponse;
import com.example.administrator.oa.view.bean.GonggoBean;
import com.example.administrator.oa.view.constance.UrlConstance;
import com.example.administrator.oa.view.net.JavaBeanRequest;
import com.google.gson.Gson;
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

import butterknife.BindView;

/**
 * Created by Administrator on 2017/10/31.
 */

public class GonggaoActivity extends HeadBaseActivity {

    @BindView(R.id.xxre_gonggao)
    XXRecycleView mXxreGonggao;
    private List<GonggoBean> datas = new ArrayList<>();
    private CommonRecyclerAdapter<GonggoBean> mGonggaoAdapter;

    @Override
    protected int getChildLayoutRes() {
        return R.layout.gonggao_activity;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("公告栏");
        initThisView();
    }

    private void initThisView() {
        mXxreGonggao.setLayoutManager(new LinearLayoutManager(this));
        mGonggaoAdapter = new CommonRecyclerAdapter<GonggoBean>(this, datas, R.layout.item_gonggao) {
            @Override
            public void convert(CommonViewHolder holder, GonggoBean item, int position, boolean b) {
                holder.setText(R.id.name, item.getTitle());
                holder.setText(R.id.time, item.getTime());
                holder.setText(R.id.content, item.getContent());
            }
        };
        mXxreGonggao.setAdapter(mGonggaoAdapter);
        mGonggaoAdapter.setOnItemClickListener(new CommonRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(CommonViewHolder commonViewHolder, int i) {
//                long articleId = mGonggaoAdapter.getDatas().get(i - mXxreGonggao.getHeaderCount()).getArticleId();
                GonggoBean gonggoBean = mGonggaoAdapter.getDatas().get(i - mXxreGonggao.getHeaderCount());
                Bundle bundle = new Bundle();
                bundle.putString("gonggoBean", new Gson().toJson(gonggoBean));
                readyGo(GonggapDetailActivity.class, bundle);
            }
        });
        try {
            RequestServerGGList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //下拉刷新
        mXxreGonggao.setPullRefreshEnabled(true);
        mXxreGonggao.setOnRefreshListener(new PullRefreshRecycleView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                falg = true;
                try {
                    RequestServerGGList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void refreshEnd() {
                falg = false;
                Toast.makeText(GonggaoActivity.this, "刷新数据成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    boolean falg = false;

    /**
     * 请求网络接口
     */
    private void RequestServerGGList() throws Exception {
        //创建请求队列
        RequestQueue ggQueue = NoHttp.newRequestQueue();
        //创建请求
        Request<GgListResponse> request = new JavaBeanRequest<>(UrlConstance.URL_GONGGAO_LIST, RequestMethod.POST, GgListResponse.class);
        ggQueue.add(0, request, new OnResponseListener<GgListResponse>() {
            @Override
            public void onStart(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<GgListResponse> response) {
                if (null != response && null != response.get() && null != response.get().getData()) {
                    if (null != mGonggaoAdapter) {
                        mGonggaoAdapter.replaceAll(response.get().getData());
                        mGonggaoAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<GgListResponse> response) {
                Toast.makeText(GonggaoActivity.this, "获取信息失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (falg && mXxreGonggao != null) {
                    mXxreGonggao.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mXxreGonggao.stopRefresh();
                        }
                    }, 1500);
                }

                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }

}
