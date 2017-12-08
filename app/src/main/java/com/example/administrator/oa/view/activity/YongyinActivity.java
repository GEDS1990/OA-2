package com.example.administrator.oa.view.activity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.bean.FormBianmaBean;
import com.example.administrator.oa.view.bean.ProcessJieguoResponse;
import com.example.administrator.oa.view.bean.QingjiaShenheBean;
import com.example.administrator.oa.view.bean.QingjiaShenheResponse;
import com.example.administrator.oa.view.bean.YingzhangTypeBean;
import com.example.administrator.oa.view.bean.YingzhangTypeResponse;
import com.example.administrator.oa.view.constance.UrlConstance;
import com.example.administrator.oa.view.net.JavaBeanRequest;
import com.example.administrator.oa.view.utils.SPUtils;
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
 * Created by Administrator on 2017/7/6.
 */

public class YongyinActivity extends HeadBaseActivity {
    @BindView(R.id.jingbanren)
    TextView mJingbanren;
    @BindView(R.id.bianhao)
    TextView mBianhao;
    @BindView(R.id.danwei)
    TextView mDanwei;
    @BindView(R.id.ll_danwei)
    LinearLayout mllDanwei;
//    @BindView(R.id.fenguanleader)
//    TextView mFenguanleader;
//    @BindView(R.id.ll_ledername)
//    LinearLayout mLlLedername;
//    @BindView(R.id.tv_buzhang)
//    TextView mTvBuzhang;
//    @BindView(R.id.ll_buzhang)
//    LinearLayout mLlBuzhang;
    @BindView(R.id.yinjianname)
    TextView mYinjianname;
    @BindView(R.id.ll_yinjianname)
    LinearLayout mLLYinjianname;
    @BindView(R.id.fenshu)
    EditText mFenshu;
    @BindView(R.id.shiyou)
    EditText mShiyou;
    @BindView(R.id.btn_caogao)
    Button mBtnCaogao;
    @BindView(R.id.btn_commit)
    Button mBtnCommit;
    private String mUserName;
    private String mDepartmentName;
    private String mDepartmentId;
    private String mUserId;
    private String processDefinitionId = "";
    private String businessKey = "";

    @Override
    protected int getChildLayoutRes() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return R.layout.activity_yongyin;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("用印申请");
        initThisView();
    }

    private void initThisView() {
        mUserName = SPUtils.getString(this, "userName");
        mUserId = SPUtils.getString(this, "userId");
        mDepartmentName = SPUtils.getString(this, "departmentName");
        mDepartmentId = SPUtils.getString(this, "departmentId");
        processDefinitionId = getIntent().getStringExtra("processDefinitionId");
        businessKey = getIntent().getStringExtra("businessKey");
        mJingbanren.setText(mUserName);
        mDanwei.setText(mDepartmentName);

        checkFormCaoGao();
    }

    /**
     * 检测是否是从草稿箱界面跳转过来
     */
    private void checkFormCaoGao(){
        if(!TextUtils.isEmpty(businessKey)){
            // 获取草稿信息
            RequestServerGetInfo(businessKey);
        }
    }

    /**
     * 获取草稿信息
     */
    private void RequestServerGetInfo(String businessKey) {
        String sessionId = SPUtils.getString(this, "sessionId");
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<QingjiaShenheResponse> request = new JavaBeanRequest<>(UrlConstance.URL_CAOGAOXIANG_INFO,
                RequestMethod.POST, QingjiaShenheResponse.class);
        //添加url?key=value形式的参数
        request.addHeader("sessionId", sessionId);
        request.add("processDefinitionId", processDefinitionId);
        request.add("businessKey", businessKey);
        Queue.add(0, request, new OnResponseListener<QingjiaShenheResponse>() {

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
                    for (QingjiaShenheBean bean : shenheBeen) {
                        if(!TextUtils.isEmpty(bean.getName()) && !TextUtils.isEmpty(bean.getValue())) {
                            Log.d("Caogao", bean.getName());
                            Log.d("Caogao", bean.getValue());
                            //当有type为userpicker的时候说明是可以发起会签的节点
                            String label = bean.getName();
                            String value = bean.getValue();
                            switch (label) {
                                case "bh":
                                    mBianhao.setText(value);
                                    break;
                                case "yjmc":
                                    if(!TextUtils.isEmpty(bean.getLabel())) {
                                        mYinjianname.setText(bean.getLabel());
                                        mYinjianname.setTag(value);
                                    }
                                    break;
                                case "fs":
                                    mFenshu.setText(value);
                                    break;
                                case "sy":
                                    mShiyou.setText(value);
                                    break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<QingjiaShenheResponse> response) {
                Toast.makeText(YongyinActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }

    // R.id.ll_ledername, R.id.ll_buzhang,
    @OnClick({R.id.yy_bianhao,  R.id.ll_yinjianname, R.id.btn_caogao, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.yy_bianhao:
                if("0".equals(mBianhao.getTag())) {
                    mBianhao.setTag("1");
                    houQuFormCode();
                }
                break;
//            case R.id.ll_ledername:
//                RequestServerGetZuzhi("请选择分管领导审核", mFenguanleader);
//                break;
//            case R.id.ll_buzhang:
//                List<String> datas_buzhang = new ArrayList();
//                datas_buzhang.add("是");
//                datas_buzhang.add("否");
//                chooseDate(datas_buzhang, mTvBuzhang, "是否需要部长审核");
//                break;
            case R.id.ll_yinjianname:
//                if("0".equals(mYinjianname.getTag())) {
//                    mYinjianname.setTag("1");
                    HuoquyingzhangCode();
//                }

                break;
            case R.id.btn_caogao:
                RequestServerCommit_Save(mBianhao.getText().toString().trim(),
                        mDanwei.getText().toString().trim(),
//                    mFenguanleader.getText().toString().trim(),
                        mYinjianname.getText().toString().trim(),
                        mFenshu.getText().toString().trim(),
                        mShiyou.getText().toString().trim()
//                    mTvBuzhang.getText().toString().trim()
                );
                break;
            case R.id.btn_commit:
                jianYanshuju();
                break;
        }
    }


    /**
     * 请求网络接口 获取编号
     */
    private void houQuFormCode() {

        //创建请求队列
        RequestQueue requestQueue = NoHttp.newRequestQueue();
        //创建请求
        Request<FormBianmaBean> request = new JavaBeanRequest<>(UrlConstance.URL_BIAODAN_CODE, RequestMethod.POST, FormBianmaBean.class);
        //添加url?key=value形式的参数
        request.add("typecode", "yybh");
        requestQueue.add(0, request, new OnResponseListener<FormBianmaBean>() {

            @Override
            public void onStart(int what) {
            }

            @Override
            public void onSucceed(int what, Response<FormBianmaBean> response) {
                Log.w("2222", response.toString());
                if (null != response && null != response.get() && null != response.get().getData()) {
                    mBianhao.setText(response.get().getData().getCode());
                } else {
                    mBianhao.setTag("0");
                }
            }

            @Override
            public void onFailed(int what, Response<FormBianmaBean> response) {
                Toast.makeText(YongyinActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
                mBianhao.setTag("0");
            }

            @Override
            public void onFinish(int what) {

            }
        });

    }


    private void jianYanshuju() {
        if (TextUtils.isEmpty(mJingbanren.getText().toString().trim())) {
            Toast.makeText(this, "经办人缺失", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mBianhao.getText().toString().trim())) {
            Toast.makeText(this, "请点击获取编号", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mDanwei.getText().toString().trim())) {
            Toast.makeText(this, "用印单位/部门缺失", Toast.LENGTH_SHORT).show();
//        } else if (TextUtils.isEmpty(mFenguanleader.getText().toString().trim())) {
//            Toast.makeText(this, "请填写文章标题", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mYinjianname.getText().toString().trim())) {
            Toast.makeText(this, "请选择印鉴名称", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mFenshu.getText().toString().trim())) {
            Toast.makeText(this, "请填写印鉴份数", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mShiyou.getText().toString().trim())) {
            Toast.makeText(this, "请填写用印事由", Toast.LENGTH_SHORT).show();
//        } else if (TextUtils.isEmpty(mTvBuzhang.getText().toString().trim())) {
//            Toast.makeText(this, "是否需要部长审核", Toast.LENGTH_SHORT).show();
        } else {
            RequestServerCommit(mBianhao.getText().toString().trim(),
                    mDanwei.getText().toString().trim(),
//                    mFenguanleader.getText().toString().trim(),
                    mYinjianname.getText().toString().trim(),
                    mFenshu.getText().toString().trim(),
                    mShiyou.getText().toString().trim()
//                    mTvBuzhang.getText().toString().trim()
            );
        }
    }


    /**
     * 请求网络接口,发起流程
     */
    // String leaderName,  , String comment
    private void RequestServerCommit(String bianhao, String danwei, String yinjianname,
                                     String fenshu, String shiyou) {
        String sessionId = SPUtils.getString(this, "sessionId");

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"bh\":" + "\"" + bianhao + "\",")
                .append("\"jbr_id\":" + "\"" + mUserId + "\",")
                .append("\"jbr\":" + "\"" + mUserName + "\",")
                .append("\"businessKey\":" + "\"" + businessKey + "\",")
//                .append("\"fgld_name\":" + "\"" + leaderName + "\",")
//                .append("\"fgld\":" + "\"" + mZuzhiUserBean.getId() + "\",")
//                .append("\"yydw_name\":" + "\"" + danwei + "\",")
//                .append("\"yydw\":" + "\"" + mDepartmentId + "\",")
                .append("\"yydw\":" + "\"" + mDepartmentName + "\",")
                .append("\"yydw_id\":" + "\"" + mDepartmentId + "\",")
                .append("\"yjmc_name\":" + "\"" + yinjianname + "\",")
                .append("\"yjmc\":" + "\"" + mYinjianname.getTag().toString() + "\",")
                .append("\"fs\":" + "\"" + fenshu + "\",")
//                .append("\"comment\":" + "\"" + comment + "\",")
                .append("\"sy\":" + "\"" + shiyou + "\"")
                .append("}");

        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_STARTPROCESS,
                RequestMethod.POST, ProcessJieguoResponse.class);
        //添加url?key=value形式的参数
        request.addHeader("sessionId", sessionId);
        request.add("processDefinitionId", processDefinitionId);
        request.add("businessKey", businessKey);
        request.add("data", json.toString());
        Queue.add(0, request, new OnResponseListener<ProcessJieguoResponse>() {

            @Override
            public void onStart(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<ProcessJieguoResponse> response) {
                Log.w("2222", response.toString());
                if (null != response && null != response.get()) {
                    if (response.get().getCode() == 200) {
                        Toast.makeText(YongyinActivity.this, "流程发起成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(YongyinActivity.this, "流程发起失败", Toast.LENGTH_SHORT).show();
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
     * 请求网络接口 获取印章类型列表
     */
    private void HuoquyingzhangCode() {

        //创建请求队列
        RequestQueue requestQueue = NoHttp.newRequestQueue();
        //创建请求
        Request<YingzhangTypeResponse> request = new JavaBeanRequest<>(UrlConstance.URL_GET_YINGZHANG_TYPE,
                RequestMethod.POST,YingzhangTypeResponse.class);
        //添加url?key=value形式的参数
        requestQueue.add(0, request, new OnResponseListener<YingzhangTypeResponse>() {

            @Override
            public void onStart(int what) {
            }

            @Override
            public void onSucceed(int what, Response<YingzhangTypeResponse> response) {
                Log.w("2222", response.toString());
                if (null != response && null != response.get() && null != response.get().getData()) {
                    List<YingzhangTypeBean> data = response.get().getData();
                    chooseNameAndId(data, mYinjianname, "选择印鉴名称");
                }
            }

            @Override
            public void onFailed(int what, Response<YingzhangTypeResponse> response) {
                Toast.makeText(YongyinActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {

            }
        });

    }

    /**
     * 保存草稿
     */
    // String leaderName,  , String comment
    private void RequestServerCommit_Save(String bianhao, String danwei, String yinjianname,
                                     String fenshu, String shiyou) {
        String sessionId = SPUtils.getString(this, "sessionId");

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"bh\":" + "\"" + bianhao + "\",")
                .append("\"jbr_id\":" + "\"" + mUserId + "\",")
                .append("\"jbr\":" + "\"" + mUserName + "\",")
                .append("\"businessKey\":" + "\"" + businessKey + "\",")
//                .append("\"fgld_name\":" + "\"" + leaderName + "\",")
//                .append("\"fgld\":" + "\"" + mZuzhiUserBean.getId() + "\",")
//                .append("\"yydw_name\":" + "\"" + danwei + "\",")
//                .append("\"yydw\":" + "\"" + mDepartmentId + "\",")
                .append("\"yydw\":" + "\"" + mDepartmentName + "\",")
                .append("\"yydw_id\":" + "\"" + mDepartmentId + "\",")
                .append("\"yjmc_name\":" + "\"" + yinjianname + "\",")
                .append("\"yjmc\":" + "\"" + mYinjianname.getTag().toString() + "\",")
                .append("\"fs\":" + "\"" + fenshu + "\",")
//                .append("\"comment\":" + "\"" + comment + "\",")
                .append("\"sy\":" + "\"" + shiyou + "\"")
                .append("}");

        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_SAVEDRAFT,
                RequestMethod.POST, ProcessJieguoResponse.class);
        //添加url?key=value形式的参数
        request.addHeader("sessionId", sessionId);
        request.add("processDefinitionId", processDefinitionId);
        request.add("businessKey", businessKey);
        request.add("data", json.toString());
        Queue.add(0, request, new OnResponseListener<ProcessJieguoResponse>() {

            @Override
            public void onStart(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<ProcessJieguoResponse> response) {
                Log.w("2222", response.toString());
                if (null != response && null != response.get()) {
                    if (response.get().getCode() == 200) {
                        Toast.makeText(YongyinActivity.this, "已保存至草稿箱", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(YongyinActivity.this, "保存草稿失败", Toast.LENGTH_SHORT).show();
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
