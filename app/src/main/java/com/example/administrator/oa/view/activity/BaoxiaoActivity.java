package com.example.administrator.oa.view.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.bean.ProcessJieguoResponse;
import com.example.administrator.oa.view.bean.QingjiaShenheBean;
import com.example.administrator.oa.view.bean.QingjiaShenheResponse;
import com.example.administrator.oa.view.constance.UrlConstance;
import com.example.administrator.oa.view.net.JavaBeanRequest;
import com.example.administrator.oa.view.utils.SPUtils;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.yanzhenjie.nohttp.FileBinary;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.OnUploadListener;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/7/6.
 */

public class BaoxiaoActivity extends HeadBaseActivity {
    @BindView(R.id.baoxiao_name)
    TextView mName;
    @BindView(R.id.baoxiao_bumen)
    TextView mBumen;
    @BindView(R.id.baoxiao_start)
    TextView mStart;
    @BindView(R.id.ll_start)
    LinearLayout mLlStart;
//    @BindView(R.id.tv_fenguanleader)
//    TextView mTvFenguanleader;
//    @BindView(R.id.ll_fenguanleader)
//    LinearLayout mLlFenguanleader;
//    @BindView(R.id.tv_buzhang)
//    TextView mTvBuzhang;
//    @BindView(R.id.ll_buzhang)
//    LinearLayout mLlBuzhang;
    @BindView(R.id.money_rmb)
    EditText mMoneyRmb;
    @BindView(R.id.money_num)
    EditText mMoneyNum;
    @BindView(R.id.baoxiao_yuanyin)
    EditText mBaoxiaoYuanyin;
    @BindView(R.id.add_fujian)
    RelativeLayout mAddFujian;
    @BindView(R.id.pb)
    ProgressBar mPb;
    @BindView(R.id.icon)
    ImageView mIcon;
    @BindView(R.id.file_name)
    TextView mFileName;
    @BindView(R.id.filesize)
    TextView mFilesize;
    @BindView(R.id.btn_uplaod)
    ImageView mBtnUplaod;
    @BindView(R.id.btn_cancel)
    ImageView mBtnCancel;
    @BindView(R.id.rl_fujian)
    RelativeLayout mRlFujian;
    @BindView(R.id.btn_caogao)
    Button mBtnCaogao;
    @BindView(R.id.btn_commit)
    Button mBtnCommit;

    private String mUserName;
    private String mDepartmentName;
    private String mDepartmentId;
    private String mSessionId;
    private String mUserId;
    private String processDefinitionId;

    int REQUESTCODE_FROM_ACTIVITY = 1002;
    private String mFilename = "";
    private String mFilePath = "";

    @Override
    protected int getChildLayoutRes() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return R.layout.activity_baoxiao;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("报销流程");
        initThisView();
    }

    private void initThisView() {
        mSessionId = SPUtils.getString(this, "sessionId");
        mUserName = SPUtils.getString(this, "userName");
        mUserId = SPUtils.getString(this, "userId");
        mDepartmentId = SPUtils.getString(this, "departmentId");
        mDepartmentName = SPUtils.getString(this, "departmentName");
        processDefinitionId = getIntent().getStringExtra("processDefinitionId");
        mName.setText(mUserName);
        mBumen.setText(mDepartmentName);
        checkFormCaoGao();
    }

    /**
     * 检测是否是从草稿箱界面跳转过来
     */
    private void checkFormCaoGao(){
        String businessKey = getIntent().getStringExtra("businessKey");
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

                    //按顺序填写数据
//                    mName.setText(shenheBeen.get(0).getValue());
//                    mBumen.setText(shenheBeen.get(1).getValue());
//                    mStart.setText(shenheBeen.get(3).getValue());
//                    mMoneyRmb.setText(shenheBeen.get(4).getValue());
//                    mMoneyNum.setText(shenheBeen.get(5).getValue());
//                    mBaoxiaoYuanyin.setText(shenheBeen.get(2).getValue());
                    for (QingjiaShenheBean bean : shenheBeen) {
                        Log.d("Caogao", bean.getLabel());
                        Log.d("Caogao", bean.getValue());
                        //当有type为userpicker的时候说明是可以发起会签的节点
                        String label = bean.getLabel();
                        String value = bean.getValue();
                        switch (label) {
                            case "id":
//                                mBianhao.setText(value);
                                break;
//                            case "address":
//                                mAddress.setText(value);
//                                break;
//                            case "startTime":
//                                mDateStart.setText(value);
//                                break;
//                            case "endTime":
//                                mDateStop.setText(value);
//                                break;
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<QingjiaShenheResponse> response) {
                Toast.makeText(BaoxiaoActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }


    // R.id.ll_fenguanleader, R.id.ll_buzhang,
    @OnClick({R.id.ll_start, R.id.add_fujian, R.id.btn_uplaod, R.id.btn_cancel, R.id.rl_fujian, R.id.btn_caogao, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_start:
                selectDate(mStart, "");
                break;
//            case R.id.ll_fenguanleader:
//                RequestServerGetZuzhi("请选择分管领导审核", mTvFenguanleader);
//                break;
//            case R.id.ll_buzhang:
//                List<String> datas_buzhang = new ArrayList();
//                datas_buzhang.add("是");
//                datas_buzhang.add("否");
//                chooseDate(datas_buzhang, mTvBuzhang, "是否需要部长审核");
//                break;
            case R.id.add_fujian:
                if("0".equals(mAddFujian.getTag())) {
                    new LFilePicker()
                            .withActivity(this)
                            .withRequestCode(REQUESTCODE_FROM_ACTIVITY)
                            .start();
                }
                break;
            case R.id.btn_uplaod:
                if (!TextUtils.isEmpty(mFilePath) && !TextUtils.isEmpty(mFilename)) {
                    RequestServer(mFilePath, mFilename);
                }
                break;
            case R.id.btn_cancel:
                break;
            case R.id.rl_fujian:
                break;
            case R.id.btn_caogao:
                // 如果有附件但是没有上传
                if(!TextUtils.isEmpty(mFilePath) && TextUtils.isEmpty(mFilePathReturn)){
                    Toast.makeText(BaoxiaoActivity.this, "请先提交附件",Toast.LENGTH_LONG).show();
                } else {
                    RequestServerGoodsLingqu_Save(mBumen.getText().toString().trim(),
                            mStart.getText().toString().trim(),
//                    mTvBuzhang.getText().toString().trim(),
                            mMoneyRmb.getText().toString().trim(),
                            mMoneyNum.getText().toString().trim(),
                            mBaoxiaoYuanyin.getText().toString().trim()
//                    mTvFenguanleader.getText().toString().trim()
                    );
                }
                break;
            case R.id.btn_commit:
                // 如果有附件但是没有上传
                if(!TextUtils.isEmpty(mFilePath) && TextUtils.isEmpty(mFilePathReturn)){
                    Toast.makeText(BaoxiaoActivity.this, "请先提交附件",Toast.LENGTH_LONG).show();
                } else {
                    jianYanshuju();
                }
                break;
        }
    }

    /**
     * 填写的数据进行校验
     */
    private void jianYanshuju() {

        if (TextUtils.isEmpty(mUserId)) {
            Toast.makeText(this, "报销人缺失", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mBumen.getText().toString().trim())) {
            Toast.makeText(this, "报销部门缺失", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mStart.getText().toString().trim())) {
            Toast.makeText(this, "请选择报销时间", Toast.LENGTH_SHORT).show();
//        } else if (TextUtils.isEmpty(mTvBuzhang.getText().toString().trim())) {
//            Toast.makeText(this, "请选择是否需要部长审核", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mMoneyRmb.getText().toString().trim())) {
            Toast.makeText(this, "请填写报销金额(大写)", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mMoneyNum.getText().toString().trim())) {
            Toast.makeText(this, "请填写报销金额(数字)", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mBaoxiaoYuanyin.getText().toString().trim())) {
            Toast.makeText(this, "请填写报销事由", Toast.LENGTH_SHORT).show();
//        } else if (TextUtils.isEmpty(mTvFenguanleader.getText().toString().trim())) {
//            Toast.makeText(this, "请选择分管领导审核", Toast.LENGTH_SHORT).show();
        } else {
            RequestServerGoodsLingqu(mBumen.getText().toString().trim(),
                    mStart.getText().toString().trim(),
//                    mTvBuzhang.getText().toString().trim(),
                    mMoneyRmb.getText().toString().trim(),
                    mMoneyNum.getText().toString().trim(),
                    mBaoxiaoYuanyin.getText().toString().trim()
//                    mTvFenguanleader.getText().toString().trim()
            );
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE_FROM_ACTIVITY) {
                //List<String> list = data.getStringArrayListExtra(Constant.RESULT_INFO);//Constant.RESULT_INFO == "paths"
                List<String> list = data.getStringArrayListExtra("paths");
                if (list.size() == 1) {
                    mRlFujian.setVisibility(View.VISIBLE);
                    mFilePath = list.get(0);
                    String[] strings = mFilePath.split("/");
                    int count = strings.length;
                    mFilename = strings[count - 1];
                    mFileName.setText(mFilename);
                    File file = new File(mFilePath);
                    mFilesize.setText(ShowLongFileSzie(file.length()));
                    if (mFilename.contains(".")) {
                        // 如果有两个后缀，则是非法文档
                        if(mFilename.split("\\.").length > 2){
                            Toast.makeText(this, "文档名格式不对", Toast.LENGTH_SHORT).show();
                            mFilePath = "";
                            mFilename = "";
                            mRlFujian.setVisibility(View.GONE);
                            return;
                        }
                        switch (mFilename.split("\\.")[1]) {
                            case "TXT":
                            case "txt":
                                mIcon.setImageResource(R.drawable.file_txt);
                                break;
                            case "xlsx":
                            case "XLSX":
                                mIcon.setImageResource(R.drawable.icon_official_excel);
                                break;
                            case "docx":
                            case "DOCX":
                                mIcon.setImageResource(R.drawable.file_word);
                                break;
                            case "png":
                            case "PNG":
                            case "jpg":
                            case "JPG":
                            case "jpeg":
                            case "JPEG":
                                mIcon.setImageResource(R.drawable.picture);
                                break;
                            default:
                                mIcon.setImageResource(R.drawable.unknow_type);
                        }
                    }

                } else if (list.size() == 0) {
                    Toast.makeText(this, "请重新选择附件", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "只能上传一个附件", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    String mFilePathReturn = "";

    /**
     * 请求网络接口
     */
    private void RequestServer(String picpath, String filename) {
        //创建请求队列
        RequestQueue ProcessQueue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_UPLOAD,
                RequestMethod.POST, ProcessJieguoResponse.class);
        //String picpath = CommonUtil2.saveBitmapToSD(BanwenActivity.this, "/CoolImage/");
        FileBinary fileBinary = new FileBinary(new File(picpath), filename);
        // 设置一个上传监听器。
        fileBinary.setUploadListener(0, mOnUploadListener);
        request.add("data", fileBinary);

        ProcessQueue.add(1, request, new OnResponseListener<ProcessJieguoResponse>() {
            @Override
            public void onStart(int what) {
            }

            @Override
            public void onSucceed(int what, Response<ProcessJieguoResponse> response) {
                Log.w("workConectionActivity", "response:" + response);
                if (null != response && null != response.get() && null != response.get().getData()) {
                    Toast.makeText(BaoxiaoActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
//                mBtnCancel.setVisibility(View.VISIBLE);
                    mBtnUplaod.setVisibility(View.GONE);
                    mBtnUplaod.setEnabled(false);
                    mAddFujian.setTag("1");
                    mFilePathReturn = response.get().getData();
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(BaoxiaoActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
            }
        });

    }

    /**
     * 上传文件进度的监听器
     */
    private OnUploadListener mOnUploadListener = new OnUploadListener() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onCancel(int what) {

        }

        @Override
        public void onProgress(int what, int progress) {
            Log.w("2BanwenActivity", "progress:" + progress);
            mPb.setProgress(progress);
        }

        @Override
        public void onFinish(int what) {
//            Toast.makeText(BaoxiaoActivity.this, "ok", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(int what, Exception exception) {

        }
    };

    /**
     * 请求网络，提交数据
     */
    private void RequestServerGoodsLingqu(String bumen, String date,
                                          String money1, String money2, String reason) {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_STARTPROCESS,
                RequestMethod.POST, ProcessJieguoResponse.class);

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"departments_name\":" + "\"" + bumen + "\",")
                .append("\"departments\":" + "\"" + mDepartmentId + "\",")
                .append("\"name\":" + "\"" + mUserName + "\",")
                .append("\"reason\":" + "\"" + reason + "\",")
                .append("\"date\":" + "\"" + date + "\",")
                .append("\"RMB\":" + "\"" + money1 + "\",")
                .append("\"money\":" + "\"" + money2 + "\",")
//                .append("\"manager_name\":" + "\"" + leader + "\",")
//                .append("\"manager\":" + "\"" + mZuzhiUserBean.getId() + "\",")
//                .append("\"comment\":" + "\"" + comment + "\"")
                .append("\"enclosure\":" + "\"" + mFilePathReturn + "\"")
                .append("}");
        //添加url?key=value形式的参数
        request.addHeader("sessionId", mSessionId);
        request.add("processDefinitionId", processDefinitionId);
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
                if (null != response && null != response.get() && null != response.get().getData()) {
                    if (response.get().getCode() == 200) {
                        Toast.makeText(BaoxiaoActivity.this, "流程发起成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(BaoxiaoActivity.this, "流程发起失败", Toast.LENGTH_SHORT).show();
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
     * 保存草稿
     */
    private void RequestServerGoodsLingqu_Save(String bumen, String date,
                                          String money1, String money2, String reason) {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_SAVEDRAFT,
                RequestMethod.POST, ProcessJieguoResponse.class);

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"departments_name\":" + "\"" + bumen + "\",")
                .append("\"departments\":" + "\"" + mDepartmentId + "\",")
                .append("\"name\":" + "\"" + mUserName + "\",")
                .append("\"reason\":" + "\"" + reason + "\",")
                .append("\"date\":" + "\"" + date + "\",")
                .append("\"RMB\":" + "\"" + money1 + "\",")
                .append("\"money\":" + "\"" + money2 + "\",")
//                .append("\"manager_name\":" + "\"" + leader + "\",")
//                .append("\"manager\":" + "\"" + mZuzhiUserBean.getId() + "\",")
//                .append("\"comment\":" + "\"" + comment + "\"")
                .append("\"enclosure\":" + "\"" + mFilePathReturn + "\"")
                .append("}");
        //添加url?key=value形式的参数
        request.addHeader("sessionId", mSessionId);
        request.add("processDefinitionId", processDefinitionId);
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
                if (null != response && null != response.get()) {
                    if (response.get().getCode() == 200) {
                        Toast.makeText(BaoxiaoActivity.this, "已保存至草稿箱", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(BaoxiaoActivity.this, "保存草稿失败", Toast.LENGTH_SHORT).show();
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
