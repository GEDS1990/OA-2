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
import com.yanzhenjie.nohttp.rest.StringRequest;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/7/6.
 */

public class NewsfabuActivity extends HeadBaseActivity {
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.fagao_bumen)
    TextView mFagaoBumen;
    @BindView(R.id.date)
    TextView mDate;
    @BindView(R.id.ll_start)
    LinearLayout mLlStart;
    @BindView(R.id.title)
    EditText mTitle;
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
//    @BindView(R.id.tv_fenguanleader)
//    TextView mTvFenguanleader;
//    @BindView(R.id.ll_fenguanleader)
//    LinearLayout mLlFenguanleader;
    private String mFilename = "";
    private String mFilePath = "";
    private String mSessionId;
    private String mUserName;
    private String mDepartmentName;
    private String processDefinitionId;
    private String mUserId;
    private String mDepartmentId;

    @Override
    protected int getChildLayoutRes() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return R.layout.activity_newsfabu;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("新闻发布");
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
        mFagaoBumen.setText(mDepartmentName);
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

                    for (QingjiaShenheBean bean : shenheBeen) {
                        Log.d("Caogao", bean.getLabel());
                        Log.d("Caogao", bean.getValue());
                        //当有type为userpicker的时候说明是可以发起会签的节点
                        String label = bean.getLabel();
                        String value = bean.getValue();
                        switch (label) {
                            case "date":
                                mDate.setText(value);
                                break;
                            case "title":
                                mTitle.setText(value);
                                break;
                            // 附件
                            case "enclosure1":
                                break;
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<QingjiaShenheResponse> response) {
                Toast.makeText(NewsfabuActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }

    int REQUESTCODE_FROM_ACTIVITY = 1001;

    // , R.id.ll_fenguanleader
    @OnClick({R.id.ll_start, R.id.add_fujian, R.id.btn_uplaod, R.id.btn_cancel, R.id.rl_fujian, R.id.btn_caogao,
            R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_start:
                selectDate(mDate, "");
                break;
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
                if(!TextUtils.isEmpty(mFilePath) && TextUtils.isEmpty(mFilePathReturn)){
                    Toast.makeText(NewsfabuActivity.this, "请先提交附件",Toast.LENGTH_LONG).show();
                } else {
                    RequestServerNewsFabu_Save(mDate.getText().toString().trim(),
                            mTitle.getText().toString().trim()
                    );
                }
                break;
            case R.id.btn_commit:
                if(!TextUtils.isEmpty(mFilePath) && TextUtils.isEmpty(mFilePathReturn)){
                    Toast.makeText(NewsfabuActivity.this, "请先提交附件",Toast.LENGTH_LONG).show();
                } else {
                    jianYanshuju();
                }
                break;
//            case R.id.ll_fenguanleader:
//                RequestServerGetZuzhi("请选择分管领导",mTvFenguanleader);
//                break;
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
                    } else {
//                        Toast.makeText(this, "不支持的类型", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(NewsfabuActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
//                mBtnCancel.setVisibility(View.VISIBLE);
                    mBtnUplaod.setVisibility(View.GONE);
                    mBtnUplaod.setEnabled(false);
                    mAddFujian.setTag("1");
                    mFilePathReturn = response.get().getData();
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(NewsfabuActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
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
//            Toast.makeText(NewsfabuActivity.this, "ok", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(int what, Exception exception) {

        }
    };

    /**
     * 填写的数据进行校验
     */
    private void jianYanshuju() {

        if (TextUtils.isEmpty(mUserId)) {
            Toast.makeText(this, "发稿人缺失", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mFagaoBumen.getText().toString().trim())) {
            Toast.makeText(this, "发稿部门缺失", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mDate.getText().toString().trim())) {
            Toast.makeText(this, "请选择发稿时间", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mTitle.getText().toString().trim())) {
            Toast.makeText(this, "请填写新闻标题", Toast.LENGTH_SHORT).show();
//        } else if (TextUtils.isEmpty(mTvFenguanleader.getText().toString().trim())) {
//            Toast.makeText(this, "请填写新闻标题？", Toast.LENGTH_SHORT).show();
        } else {
            //, mTvFenguanleader.getText().toString().trim()
            RequestServerNewsFabu(mDate.getText().toString().trim(),
                    mTitle.getText().toString().trim()
            );
        }

    }

    /**
     * 请求您网络，提交数据
     */
    //, String managerName
    private void RequestServerNewsFabu(String time, String title) {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_STARTPROCESS,
                RequestMethod.POST, ProcessJieguoResponse.class);

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"departments_name\":" + "\"" + mDepartmentName + "\",")
                .append("\"departments\":" + "\"" + mDepartmentId + "\",")
                .append("\"name\":" + "\"" + mUserName + "\",")
                .append("\"date\":" + "\"" + time + "\",")
                .append("\"title\":" + "\"" + title + "\",")
//                .append("\"manager\":" + "\"" + mZuzhiUserBean.getId() + "\",")
//                .append("\"manager_name\":" + "\"" + managerName + "\"")
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
                        Toast.makeText(NewsfabuActivity.this, "流程发起成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(NewsfabuActivity.this, "流程发起失败", Toast.LENGTH_SHORT).show();
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
    //, String managerName
    private void RequestServerNewsFabu_Save(String time, String title) {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_SAVEDRAFT,
                RequestMethod.POST, ProcessJieguoResponse.class);

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"departments_name\":" + "\"" + mDepartmentName + "\",")
                .append("\"departments\":" + "\"" + mDepartmentId + "\",")
                .append("\"name\":" + "\"" + mUserName + "\",")
                .append("\"date\":" + "\"" + time + "\",")
                .append("\"title\":" + "\"" + title + "\",")
//                .append("\"manager\":" + "\"" + mZuzhiUserBean.getId() + "\",")
//                .append("\"manager_name\":" + "\"" + managerName + "\"")
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
                        Toast.makeText(NewsfabuActivity.this, "已保存至草稿箱", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(NewsfabuActivity.this, "保存草稿失败", Toast.LENGTH_SHORT).show();
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
