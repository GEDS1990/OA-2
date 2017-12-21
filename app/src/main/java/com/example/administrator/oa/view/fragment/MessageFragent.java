package com.example.administrator.oa.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.activity.ChatMsgListActivity;
import com.example.administrator.oa.view.activity.GonggaoActivity;
import com.example.administrator.oa.view.activity.LoginActivity;
import com.example.administrator.oa.view.activity.MainActivity;
import com.example.administrator.oa.view.activity.MsgDetailActivity;
import com.example.administrator.oa.view.bean.MsgListBean;
import com.example.administrator.oa.view.common.MyApp;
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
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/3/21.
 */
public class MessageFragent extends BaseFragment {

    @BindView(R.id.btn1_jishi)
    ImageView mBtn1Jishi;
    @BindView(R.id.btn2_kedu)
    ImageView mBtn2Kedu;
    Unbinder unbinder;
    @BindView(R.id.xxre_msg)
    XXRecycleView mXxreMsg;
    List<MsgListBean> datas = new ArrayList();
    private CommonRecyclerAdapter<MsgListBean> mMsgAdapter;
    private MainActivity mainActivity;
    RelativeLayout mRlBaseheaderRight;
    private TextView mGonggaoBtn;
    // 消息未读件数
    private String count;
    private MyApp application;

    @Override
    protected int getChildLayoutRes() {
        return R.layout.fragment_workbench;
    }

    @Override
    protected void initView(View childHeadView, RelativeLayout rlBaseheaderBack, RelativeLayout rlBaseheaderHeader,
                            RelativeLayout rlBaseheaderRight, View childView, LinearLayout myStatusBar) {
        this.mRlBaseheaderRight = rlBaseheaderRight;
        myStatusBar.setBackgroundResource(R.color.blue);
        ((TextView) rlBaseheaderHeader.getChildAt(0)).setText("消息中心");
        mainActivity = (MainActivity) getActivity();
        application = (MyApp) mainActivity.getApplicationContext();
        mGonggaoBtn = ((TextView) mRlBaseheaderRight.getChildAt(0));
        //mGonggaoBtn.setText("公告");
        mGonggaoBtn.setBackgroundResource(R.drawable.icon_laba);
        initThisView();
    }

    private void initThisView() {
        mXxreMsg.setLayoutManager(new LinearLayoutManager(mainActivity));
        mMsgAdapter = new CommonRecyclerAdapter<MsgListBean>(mainActivity, datas, R.layout.item_message) {
            @Override
            public void convert(CommonViewHolder holder, MsgListBean item, int position, boolean b) {
                holder.setText(R.id.name, item.getSenderUsername());
                holder.setText(R.id.date, item.getTime());
                if (TextUtils.isEmpty(item.getContent())) {
                    holder.setText(R.id.msg_content, "无具体内容");
                } else {
                    holder.setText(R.id.msg_content, item.getContent());
                }
                if (!TextUtils.isEmpty(item.getSenderUsername())) {
                    String firstStr = String.valueOf(item.getSenderUsername().charAt(0));
                    holder.setText(R.id.iv, firstStr);
                }

                // 添加未读消息小红点 by aawang 2017/11/21
                if ("0".equals(item.getStatus())) {
                    holder.getView(R.id.imageUnRead).setVisibility(View.VISIBLE);
                } else {
                    holder.getView(R.id.imageUnRead).setVisibility(View.INVISIBLE);
                }
            }
        };
        mXxreMsg.setAdapter(mMsgAdapter);
        mMsgAdapter.setOnItemClickListener(new CommonRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(CommonViewHolder commonViewHolder, int i) {
                String msgId = mMsgAdapter.getDatas().get(i - mXxreMsg.getHeaderCount()).getId();
                Intent intent = new Intent(getActivity(), MsgDetailActivity.class);
                intent.putExtra("msgId", msgId);
                startActivityForResult(intent, 1001);
            }
        });

        try {
            RequestServerMsgList(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //下拉刷新
        mXxreMsg.setPullRefreshEnabled(true);
        mXxreMsg.setOnRefreshListener(new PullRefreshRecycleView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    RequestServerMsgList(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void refreshEnd() {
//                Toast.makeText(mainActivity, "刷新数据成功", Toast.LENGTH_SHORT).show();
            }
        });

        mGonggaoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readyGo(GonggaoActivity.class);
            }
        });
    }

    /**
     * 请求网络接口
     */
    private void RequestServerMsgList(final boolean isRefresh) throws Exception {
        //获取sessionid
        String sessionId = SPUtils.getString(mainActivity, "sessionId");
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
                if (mLoadingDialog != null) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<MsgListBean> response) {
                Log.w("44442", response.toString());
                if (null != response && null != response.get() && null != response.get().getData()) {
                    List<MsgListBean> datas = response.get().getData();
                    // 如果没有数据，就显示“暂无数据”
                    if (null == datas || 1 >= datas.size()){
                        mXxreMsg.setEmptyView(R.layout.emptyview);
                    } else {
                        if (null != mMsgAdapter) {
                            count = datas.get(datas.size() - 1).getCount();
                            SPUtils.put(mainActivity, "messageCount", count);
                            // 通知MainActivity更新新消息图标
                            Intent intent = new Intent(application.mainAction);
                            mainActivity.sendBroadcast(intent);

                            // 最后一条不是消息  只是未读件数统计  不需要表示出来
                            datas.remove(datas.size() - 1);
                            mMsgAdapter.replaceAll(datas);
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<MsgListBean> response) {
                Toast.makeText(mainActivity, "获取消息失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (isRefresh) {
                    mXxreMsg.stopRefresh();
                }

                if (null != mLoadingDialog) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @OnClick({R.id.btn1_jishi, R.id.btn2_kedu})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn2_kedu:
                readyGo(ChatMsgListActivity.class);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(1001 == requestCode){
            try {
                RequestServerMsgList(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
