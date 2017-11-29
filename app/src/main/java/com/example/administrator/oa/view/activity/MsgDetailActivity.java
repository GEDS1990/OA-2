package com.example.administrator.oa.view.activity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.constance.UrlConstance;
import com.example.administrator.oa.view.net.JavaBeanRequest;
import com.example.administrator.oa.view.bean.MsgDetailBean;
import com.example.administrator.oa.view.utils.SPUtils;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.StringRequest;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/6/27.
 */

public class MsgDetailActivity extends HeadBaseActivity {
    @BindView(R.id.name_from)
    TextView mNameFrom;
    @BindView(R.id.data)
    TextView mData;
    @BindView(R.id.msg_content)
    TextView mMsgContent;

    @Override
    protected int getChildLayoutRes() {
        return R.layout.activity_msg_detail;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("消息详情");
        initThisView();
    }

    private void initThisView() {
        try {
            RequestServerMsgList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求网络接口
     */
    private void RequestServerMsgList() throws Exception {
        String msgId = getIntent().getStringExtra("msgId");
        Log.w("MsgDetailActivity", msgId);
        //获取sessionid
        String sessionId = SPUtils.getString(this, "sessionId");
        //Log.w("获取sessionid", sessionId);
        //创建请求队列
        RequestQueue msgDetailQueue = NoHttp.newRequestQueue();
        //创建请求
        Request<MsgDetailBean> request = new JavaBeanRequest(UrlConstance.URL_MSG_DETAIL, RequestMethod.POST, MsgDetailBean.class);
        //添加url?key=value形式的参数
        //request.add("sessionId", "43294b44-b4a9-48a1-8cc1-e641917431c0");
        request.addHeader("sessionId", sessionId);
        request.add("msgId", msgId);

        msgDetailQueue.add(0, request, new OnResponseListener<MsgDetailBean>() {

            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<MsgDetailBean> response) {
                Log.w("MsgDetailActivity", response.get().toString());
                if (null != response && null != response.get() && null != response.get().getData()) {
                    mNameFrom.setText("From : " + response.get().getData().getSenderUsername());
                    mData.setText(response.get().getData().getTime());
                    if (TextUtils.isEmpty(response.get().getData().getContent())) {
                        mMsgContent.setText("没有内容");
                    } else {
                        mMsgContent.setText("\t\t\t\t" + response.get().getData().getContent());
                    }
                } else {
                    Toast.makeText(MsgDetailActivity.this, "获取信息失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(int what, Response<MsgDetailBean> response) {
                Toast.makeText(MsgDetailActivity.this, "获取信息失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {

            }
        });
    }


    /**
     * 请求网络接口
     */
    private void RequestServerMsgList2() throws Exception {
        String msgId = getIntent().getStringExtra("msgId");
        Log.w("MsgDetailActivity", msgId);
        //获取sessionid
        String sessionId = SPUtils.getString(this, "sessionId");
        //Log.w("获取sessionid", sessionId);
        //创建请求队列
        RequestQueue msgDetailQueue = NoHttp.newRequestQueue();
        //创建请求
        Request<String> request = new StringRequest(UrlConstance.URL_MSG_DETAIL, RequestMethod.POST);
        //添加url?key=value形式的参数
        //request.add("sessionId", "43294b44-b4a9-48a1-8cc1-e641917431c0");
        request.addHeader("sessionId", sessionId);
        request.add("msgId", msgId);

        msgDetailQueue.add(0, request, new OnResponseListener<String>() {

            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
//                Log.w("MsgDetailActivity", response.toString());
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Toast.makeText(MsgDetailActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {

            }
        });
    }
}
