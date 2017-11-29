package com.example.administrator.oa.view.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.bean.ProcessJieguoResponse;
import com.example.administrator.oa.view.bean.ProcessShenheHistoryBean;
import com.example.administrator.oa.view.bean.ProcessShenheHistoryRes;
import com.example.administrator.oa.view.bean.QingjiaShenheBean;
import com.example.administrator.oa.view.bean.QingjiaShenheResponse;
import com.example.administrator.oa.view.constance.UrlConstance;
import com.example.administrator.oa.view.net.JavaBeanRequest;
import com.example.administrator.oa.view.utils.SPUtils;
import com.lsh.XXRecyclerview.CommonRecyclerAdapter;
import com.lsh.XXRecyclerview.CommonViewHolder;
import com.lsh.XXRecyclerview.XXRecycleView;
import com.luoshihai.xxdialog.XXDialog;
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

/**
 * Created by Administrator on 2017/7/25.
 */

public class QingJiaActivity_shenhe extends HeadBaseActivity {
    @BindView(R.id.qingjia_name)
    TextView mQingjiaName;
    @BindView(R.id.qingjia_bumen)
    TextView mQingjiaBumen;
    @BindView(R.id.qingjia_fangshi)
    TextView mQingjiaFangshi;
    @BindView(R.id.qingjia_tianshu)
    TextView mQingjiaTianshu;
    @BindView(R.id.qingjia_shiyou)
    TextView mQingjiaShiyou;
    @BindView(R.id.xxre)
    XXRecycleView mXxre;
    @BindView(R.id.btn_caogao)
    Button mBtnCaogao;
    @BindView(R.id.btn_commit)
    Button mBtnCommit;
    @BindView(R.id.start_time)
    TextView mStartTime;
    @BindView(R.id.stop_time)
    TextView mStopTime;
    @BindView(R.id.qingjia_type)
    TextView mQingjiaType;
    private String mTaskId;
    private String mSessionId;
    private List<ProcessShenheHistoryBean> datas = new ArrayList();
    private CommonRecyclerAdapter<ProcessShenheHistoryBean> mAdapter;
    private XXDialog xxDialog;

    @Override
    protected int getChildLayoutRes() {
        return R.layout.activity_qingjia_shenhe;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("请假申请");
        initThisView();
    }

    private void initThisView() {
        mTaskId = getIntent().getStringExtra("taskId");
        mSessionId = SPUtils.getString(this, "sessionId");
        //获取服务器数据，填充表单数据
        RequestServer();
        mXxre.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CommonRecyclerAdapter<ProcessShenheHistoryBean>(this, datas, R.layout.item_process_shenhejilu) {
            @Override
            public void convert(CommonViewHolder holder, ProcessShenheHistoryBean item, int i, boolean b) {
                holder.setText(R.id.name, item.getAssignee());
                holder.setText(R.id.content, item.getComment());
                holder.setText(R.id.date, item.getCompleteTime());
            }
        };
        mXxre.setAdapter(mAdapter);
    }

    @OnClick({R.id.btn_caogao, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_caogao:
                RequestServerCommit("不同意");
                break;
            case R.id.btn_commit:
//                xxDialog = new XXDialog(this, R.layout.dialog_shenhe_yijian) {
//                    @Override
//                    public void convert(DialogViewHolder holder) {
//
//                    }
//                };
                RequestServerCommit("同意");
                break;
        }
    }

    /**
     * 请求网络接口-流程审核记录
     */
    private void RequestServer() {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();

        //1-流程审核记录
        //创建请求
        Request<ProcessShenheHistoryRes> request = new JavaBeanRequest<>(UrlConstance.URL_GET_PROCESS_HESTORY,
                RequestMethod.POST, ProcessShenheHistoryRes.class);
        //添加url?key=value形式的参数
        request.addHeader("sessionId", mSessionId);
        request.add("taskId", mTaskId);

        Queue.add(1, request, new OnResponseListener<ProcessShenheHistoryRes>() {

            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<ProcessShenheHistoryRes> response) {
                Log.w("2222", response.toString());
                if (null != response && null != response.get() && null != response.get().getData()) {
                    List<ProcessShenheHistoryBean> data = response.get().getData();
                    if (mAdapter != null) {
                        mAdapter.replaceAll(data);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessShenheHistoryRes> response) {
                Toast.makeText(QingJiaActivity_shenhe.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {

            }
        });

        //2-拉取页面数据
        //创建请求
        Request<QingjiaShenheResponse> request2 = new JavaBeanRequest<>(UrlConstance.URL_GET_PROCESS_INIT,
                RequestMethod.POST, QingjiaShenheResponse.class);
        //添加url?key=value形式的参数
        request2.add("taskId", mTaskId);
        Queue.add(0, request2, new OnResponseListener<QingjiaShenheResponse>() {

            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<QingjiaShenheResponse> response) {
                Log.w("3333", response.toString());
                if (null != response && null != response.get() && null != response.get().getData()) {
                    List<QingjiaShenheBean> shenheBeen = response.get().getData();
                    //按顺序填写数据
                    mQingjiaBumen.setText(shenheBeen.get(0).getValue());
                    mQingjiaName.setText(shenheBeen.get(1).getValue());
                    mStartTime.setText(shenheBeen.get(2).getValue());
                    mStopTime.setText(shenheBeen.get(3).getValue());
                    mQingjiaFangshi.setText(shenheBeen.get(4).getValue());
                    mQingjiaTianshu.setText(shenheBeen.get(5).getValue());
                    mQingjiaType.setText(shenheBeen.get(6).getValue());
                    mQingjiaShiyou.setText(shenheBeen.get(7).getValue());
                }
            }

            @Override
            public void onFailed(int what, Response<QingjiaShenheResponse> response) {
                Toast.makeText(QingJiaActivity_shenhe.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {

            }
        });
    }


    /**
     * 提交页面数据，完成当前审核
     */
    private void RequestServerCommit(String comment) {
        //拼接data的json
        String bumen = mQingjiaBumen.getText().toString();
        String name = mQingjiaName.getText().toString();
        String starttime = mStartTime.getText().toString();
        String stoptime = mStopTime.getText().toString();
        String fangshi = mQingjiaFangshi.getText().toString();
        String tianshu = mQingjiaTianshu.getText().toString();
        String type = mQingjiaType.getText().toString();
        String reason = mQingjiaShiyou.getText().toString();

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"departments\":" + "\"" + bumen + "\",")
                .append("\"name\":" + "\"" + name + "\",")
                .append("\"startTime\":" + "\"" + starttime + "\",")
                .append("\"endTime\":" + "\"" + stoptime + "\",")
                .append("\"day\":" + "\"" + fangshi + "\",")
                .append("\"number\":" + "\"" + tianshu + "\",")
                .append("\"type\":" + "\"" + type + "\",")
                .append("\"reason\":" + "\"" + reason + "\",")
                .append("\"comment\":" + "\"" + comment + "\"")
                .append("}");

        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();

        //1-流程审核记录
        //创建请求
        Request<ProcessJieguoResponse> requestCommit = new JavaBeanRequest<>(UrlConstance.URL_PROCESS_COMMIT,
                RequestMethod.POST, ProcessJieguoResponse.class);
        //添加url?key=value形式的参数
        requestCommit.addHeader("sessionId", mSessionId);
        requestCommit.add("taskId", mTaskId);

        requestCommit.add("data", json.toString());
        Queue.add(0, requestCommit, new OnResponseListener<ProcessJieguoResponse>() {

            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<ProcessJieguoResponse> response) {
                Log.w("221", response.toString());
                if (null != response && null != response.get()) {
                    if (response.get().getCode() == 200) {
                        Toast.makeText(QingJiaActivity_shenhe.this, "流程审核成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(QingJiaActivity_shenhe.this, "流程审核失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {

            }
        });
    }


}
