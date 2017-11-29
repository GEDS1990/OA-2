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
 * Created by Administrator on 2017/8/3.
 */

public class ChuchaiActivity_shenhe extends HeadBaseActivity {

    @BindView(R.id.chuchai_name)
    TextView mChuchaiName;
    @BindView(R.id.chuchai_bumen)
    TextView mChuchaiBumen;
    @BindView(R.id.ll_bumen)
    LinearLayout mLlBumen;
    @BindView(R.id.chuchai_gongju)
    TextView mChuchaiGongju;
    @BindView(R.id.ll_gongju)
    LinearLayout mLlGongju;
    @BindView(R.id.chuchai_start)
    TextView mChuchaiStart;
    @BindView(R.id.ll_start)
    LinearLayout mLlStart;
    @BindView(R.id.chuchai_stop)
    TextView mChuchaiStop;
    @BindView(R.id.ll_stop)
    LinearLayout mLlStop;
    @BindView(R.id.chuchai_didian)
    TextView mChuchaiDidian;
    @BindView(R.id.chuchai_shiyou)
    TextView mChuchaiShiyou;
    @BindView(R.id.chuchai_beizhu)
    TextView mChuchaiBeizhu;
    @BindView(R.id.xxre)
    XXRecycleView mXxre;
    @BindView(R.id.btn_caogao)
    Button mBtnCaogao;
    @BindView(R.id.btn_commit)
    Button mBtnCommit;
    private String mTaskId;
    private String mSessionId;
    private CommonRecyclerAdapter<ProcessShenheHistoryBean> mAdapter;
    private List<ProcessShenheHistoryBean> datas = new ArrayList<>();
    private String mProcessTaskType;
    private String mUserType;
    private String mDepartmentName;
    private String mDepartmentId;

    @Override
    protected int getChildLayoutRes() {
        return R.layout.activity_chuchai_shenhe;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("出差审核");
        initThisView();
    }

    private void initThisView() {
        mTaskId = getIntent().getStringExtra("taskId");
        mProcessTaskType = getIntent().getStringExtra("processTaskType");
        mUserType =SPUtils.getString(this,"userType");
        mDepartmentName = SPUtils.getString(this,"departmentName");
        mDepartmentId = SPUtils.getString(this,"departmentId");
        Log.w("6666", mProcessTaskType + "/,mUserType=" + mUserType);
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
                Toast.makeText(ChuchaiActivity_shenhe.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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
                if (mLoadingDialog != null) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<QingjiaShenheResponse> response) {
                if (null != response && null != response.get() && null != response.get().getData()) {
                    List<QingjiaShenheBean> shenheBeen = response.get().getData();
                    //按顺序填写数据
                    mChuchaiBumen.setText(shenheBeen.get(0).getValue());
                    mChuchaiName.setText(shenheBeen.get(1).getValue());
                    mChuchaiStart.setText(shenheBeen.get(2).getValue());
                    mChuchaiStop.setText(shenheBeen.get(3).getValue());
                    mChuchaiDidian.setText(shenheBeen.get(4).getValue());
                    mChuchaiShiyou.setText(shenheBeen.get(5).getValue());
                    mChuchaiGongju.setText(shenheBeen.get(6).getValue());
                    mChuchaiBeizhu.setText(shenheBeen.get(7).getValue());
                }
            }

            @Override
            public void onFailed(int what, Response<QingjiaShenheResponse> response) {
                Toast.makeText(ChuchaiActivity_shenhe.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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
     * 提交页面数据，完成当前审核
     */
    private void RequestServerCommit(String comment) {
        //拼接data的json
        String bumen = mChuchaiBumen.getText().toString();
        String name = mChuchaiName.getText().toString();
        String starttime = mChuchaiStart.getText().toString();
        String stoptime = mChuchaiStop.getText().toString();
        String gongju = mChuchaiGongju.getText().toString();
        String address = mChuchaiDidian.getText().toString();
        String beizhu = mChuchaiBeizhu.getText().toString();
        String reason = mChuchaiShiyou.getText().toString();

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"departments_name\":" + "\"" + bumen + "\",")
                .append("\"name\":" + "\"" + name + "\",")
                .append("\"traffic\":" + "\"" + gongju + "\",")
                .append("\"startTime\":" + "\"" + starttime + "\",")
                .append("\"endTime\":" + "\"" + stoptime + "\",")
                .append("\"address\":" + "\"" + address + "\",")
                .append("\"reason\":" + "\"" + reason + "\",")
                .append("\"beizhu\":" + "\"" + beizhu + "\",")
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
                if (mLoadingDialog != null) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<ProcessJieguoResponse> response) {
                if (null != response && null != response.get()) {
                    if (response.get().getCode() == 200) {
                        Toast.makeText(ChuchaiActivity_shenhe.this, "流程审核成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(ChuchaiActivity_shenhe.this, "流程审核失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }

}
