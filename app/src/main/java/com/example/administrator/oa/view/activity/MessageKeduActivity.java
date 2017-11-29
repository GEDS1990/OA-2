package com.example.administrator.oa.view.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.bean.MsgListBean;
import com.example.administrator.oa.view.constance.UrlConstance;
import com.example.administrator.oa.view.net.JavaBeanRequest;
import com.example.administrator.oa.view.utils.SPUtils;
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
 * Created by Administrator on 2017/7/11.
 */

public class MessageKeduActivity extends HeadBaseActivity {

    @BindView(R.id.xxre_msg)
    XXRecycleView mXxreMsg;
    List<MsgListBean> datas = new ArrayList();
    private CommonRecyclerAdapter<MsgListBean> mMsgAdapter;

    @Override
    protected int getChildLayoutRes() {
        return R.layout.activity_msg_kedu;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("系统消息");
        initThisView();
    }

    private void initThisView() {
        mXxreMsg.setLayoutManager(new LinearLayoutManager(this));
        mMsgAdapter = new CommonRecyclerAdapter<MsgListBean>(this, datas, R.layout.item_message) {
            @Override
            public void convert(CommonViewHolder holder, MsgListBean item, int position, boolean b) {
                holder.setText(R.id.name, item.getSenderUsername());
                holder.setText(R.id.date, item.getTime());
                holder.setText(R.id.msg_content, item.getContent());
            }
        };
        mXxreMsg.setAdapter(mMsgAdapter);
        mMsgAdapter.setOnItemClickListener(new CommonRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(CommonViewHolder commonViewHolder, int i) {
                String msgId = mMsgAdapter.getDatas().get(i).getId();
                Bundle bundle = new Bundle();
                bundle.putString("msgId", msgId);
                readyGo(MsgDetailActivity.class, bundle);
            }
        });

        try {
            RequestServerMsgList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //下拉刷新
        mXxreMsg.setPullRefreshEnabled(true);
        mXxreMsg.setOnRefreshListener(new PullRefreshRecycleView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                flag = true;
                try {
                    RequestServerMsgList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void refreshEnd() {
                flag = false;
                Toast.makeText(MessageKeduActivity.this, "刷新数据成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    boolean flag = false;


    /**
     * 请求网络接口
     */
    private void RequestServerMsgList() throws Exception {
        //获取sessionid
        String sessionId = SPUtils.getString(this, "sessionId");
        //Log.w("获取sessionid", sessionId);
        //创建请求队列
        RequestQueue msgQueue = NoHttp.newRequestQueue();
        //创建请求
        Request<MsgListBean> request = new JavaBeanRequest(UrlConstance.URL_MSG_LIST, RequestMethod.POST, MsgListBean.class);
        //添加url?key=value形式的参数
        //request.add("sessionId", "43294b44-b4a9-48a1-8cc1-e641917431c0");
        request.addHeader("sessionId", sessionId);
        msgQueue.add(0, request, new OnResponseListener<MsgListBean>() {

            @Override
            public void onStart(int what) {
                if (mLoadingDialog!=null) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<MsgListBean> response) {
                Log.w("44442", response.toString());
                if (null != response && null != response.get() && null != response.get().getData()) {
                    if (null != mMsgAdapter) {
                        mMsgAdapter.replaceAll(response.get().getData());
                        mMsgAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<MsgListBean> response) {
                Toast.makeText(MessageKeduActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (flag && mXxreMsg != null) {
                    mXxreMsg.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mXxreMsg.stopRefresh();
                        }
                    }, 1500);
                }

                if (mLoadingDialog!=null) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }
}
