package com.example.administrator.oa.view.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.bean.LiuChongDingyiBean;
import com.example.administrator.oa.view.bean.ProcessDetailBean;
import com.example.administrator.oa.view.bean.ProcessListResponse;
import com.example.administrator.oa.view.bean.ProcessTypeListBean;
import com.example.administrator.oa.view.constance.UrlConstance;
import com.example.administrator.oa.view.constance.commonConstance;
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

/**
 * Created by Administrator on 2017/6/27.
 */

public class LiuchongDingyiActivity extends HeadBaseActivity {
    @BindView(R.id.xxre_common_liuchong)
    XXRecycleView mXxreCommonLiuchong;
    List<ProcessTypeListBean> datas = new ArrayList<>();
    private CommonRecyclerAdapter<ProcessTypeListBean> mCommonAdapter;
    private List<LiuChongDingyiBean> mTempProcessLsit;

    @Override
    protected int getChildLayoutRes() {
        return R.layout.activity_liuchongdningyi;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("流程定义");
        initThisView();
    }

    private void initThisView() {
        //RequestServer();
        RequestServerLogin();
        //常用流程
        mXxreCommonLiuchong.setLayoutManager(new LinearLayoutManager(this));
        //item_liuchong
        mCommonAdapter = new CommonRecyclerAdapter<ProcessTypeListBean>(this, datas, R.layout.item_liuchong_list) {
            @Override
            public void convert(CommonViewHolder holder, ProcessTypeListBean item, int position, boolean b) {
                holder.setText(R.id.title, item.getType());
                XXRecycleView xxRe = (XXRecycleView) holder.getView(R.id.xxre);

                if ("公共流程".equals(item.getType())) {
                    xxRe.setLayoutManager(new GridLayoutManager(LiuchongDingyiActivity.this, 2));
                } else {
                    xxRe.setLayoutManager(new LinearLayoutManager(LiuchongDingyiActivity.this));
                }
                ArrayList<ProcessDetailBean> prodatas = new ArrayList<>();
                final CommonRecyclerAdapter<ProcessDetailBean> adapter = new CommonRecyclerAdapter<ProcessDetailBean>
                        (LiuchongDingyiActivity.this, prodatas, R.layout.item_liuchong) {
                    @Override
                    public void convert(CommonViewHolder holder, ProcessDetailBean item, int position, boolean b) {
                        holder.setText(R.id.name, item.getName());
//                        holder.setText(R.id.longname, item.getName());
                        for (int i = 0; i < commonConstance.liuchong.length; i++) {
                            if (getString(commonConstance.liuchong[i][0]).equals(item.getName())) {
                                holder.getView(R.id.icon).setBackgroundResource(commonConstance.liuchong[i][2]);
                            }
                        }
                    }
                };
                xxRe.setAdapter(adapter);
                adapter.replaceAll(item.getProcess());
                adapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClickListener(CommonViewHolder viewHolder, int i) {
                        String processType = adapter.getDatas().get(i).getName();
                        String processDefinitionId = adapter.getDatas().get(i).getProcessDefinitionId();
                        GoFaqiliuchong(processType, processDefinitionId);
                    }
                });

            }
        };
        mXxreCommonLiuchong.setAdapter(mCommonAdapter);
    }

    private void GoFaqiliuchong(String processType, String processDefinitionId) {
        Bundle bundle = new Bundle();
        bundle.putString("processDefinitionId", processDefinitionId);
        switch (processType) {
            case "请假流程":
                readyGo(QingJiaActivity.class, bundle);//ok-
                //readyGo(QingJiaActivity2.class, bundle);//ok-
                break;
            case "出差申请":
                readyGo(ChuChaiActivity.class, bundle);//ok-
                break;
            case "工作联系":
                readyGo(workConectionActivity.class, bundle);//ok-
                break;
            case "办文流程":
                readyGo(BanwenActivity.class, bundle);//ok-
                break;
            case "用车申请":
                readyGo(YongcheActivity.class, bundle);//ok-
                break;
            case "新闻发布":
                readyGo(NewsfabuActivity.class, bundle);//ok-
                break;
            case "低值易耗品领用":
                readyGo(DizhiyihaoActivity.class, bundle);//ok-
                break;
            case "固定资产领用":
                readyGo(GudingZichanActivity.class, bundle);//ok-
                break;
            case "用印申请":
                readyGo(YongyinActivity.class, bundle);//ok-
                break;
            case "借款申请":
                readyGo(JieKuanActivity.class, bundle);//ok-
                break;
            case "报销流程":
                readyGo(BaoxiaoActivity.class, bundle);//ok-
                break;
            case "合同流程":
                readyGo(HetongActivity.class, bundle);//ok-
                break;
            case "资金调拨":
                readyGo(ZijinActivity.class, bundle);//ok-
                break;
            case "企业考核":
                readyGo(QiyeKaoheActivity.class, bundle);//ok-
                break;
            case "企业退租":
                readyGo(QiyeTuizuActivity.class, bundle);//ok-
                break;
            case "租房流程":
                readyGo(ZufangActivity.class, bundle);//ok-
                break;
            case "资金申请（合同付款）":
                readyGo(ZijinShenqingActivity.class, bundle);//ok-
                break;
            case "资金申请（非合同）":
                readyGo(ZijinShenqingFeiHetongActivity.class, bundle);//ok-
                break;
            case "督办流程":
                readyGo(DubanActivity.class, bundle);//ok-
                break;
            case "收文流程":
                readyGo(ShouWenActivity.class, bundle);//ok-
                break;
            case "投资协议流程":
                readyGo(TouziXieyiActivity.class, bundle);//ok-
                break;
            case "招投标文件":
                readyGo(ZhaobiaoFileActivity.class, bundle);//ok
                break;
        }
    }


    /**
     * 请求流程定义接口
     */
    private void RequestServerLogin() {
        //departmentName,departments
        String departmentName = SPUtils.getString(this, "departmentName");
        String departments = SPUtils.getString(this, "departments");

        //创建请求队列
        RequestQueue ProcessQueue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessListResponse> request = new JavaBeanRequest(UrlConstance.URL_PROCESS_DEFIN,
                RequestMethod.POST, ProcessListResponse.class);
        request.add("departmentName", departmentName);
        request.add("departments", departments);
        ProcessQueue.add(0, request, new OnResponseListener<ProcessListResponse>() {

            @Override
            public void onStart(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<ProcessListResponse> response) {
                Log.w("LiuchongDingyiActivity", response.toString());
                if (null != response && null != response.get() && null != response.get().getData()) {
                    List<ProcessTypeListBean> procssDefinList = response.get().getData();
                    if (null != mCommonAdapter) {
                        mCommonAdapter.replaceAll(procssDefinList);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessListResponse> response) {
                Toast.makeText(LiuchongDingyiActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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
