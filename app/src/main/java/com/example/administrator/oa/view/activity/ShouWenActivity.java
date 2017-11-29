package com.example.administrator.oa.view.activity;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.bean.FormBianmaBean;
import com.example.administrator.oa.view.bean.ProcessJieguoResponse;
import com.example.administrator.oa.view.bean.QingjiaShenheBean;
import com.example.administrator.oa.view.bean.QingjiaShenheResponse;
import com.example.administrator.oa.view.bean.ZuzhiUserBean;
import com.example.administrator.oa.view.bean.ZuzhiUserListResponse;
import com.example.administrator.oa.view.bean.organization_structure.ChildrenBean;
import com.example.administrator.oa.view.bean.organization_structure.OrganizationResponse;
import com.example.administrator.oa.view.constance.UrlConstance;
import com.example.administrator.oa.view.net.JavaBeanRequest;
import com.example.administrator.oa.view.utils.SPUtils;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.lsh.XXRecyclerview.CommonRecyclerAdapter;
import com.lsh.XXRecyclerview.CommonViewHolder;
import com.lsh.XXRecyclerview.XXRecycleView;
import com.luoshihai.xxdialog.DialogViewHolder;
import com.luoshihai.xxdialog.XXDialog;
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
 * Created by Administrator on 2017/7/11.
 */

public class ShouWenActivity extends HeadBaseActivity {
    @BindView(R.id.textView3)
    TextView mTextView3;
    @BindView(R.id.danwei)
    EditText mDanwei;
    @BindView(R.id.title)
    EditText mTitle;
    @BindView(R.id.name)
    EditText mName;
    @BindView(R.id.bianhao)
    TextView mBianhao;
    @BindView(R.id.ll_zhuanfa)
    LinearLayout mLlZhuanfa;
    @BindView(R.id.beizhu)
    EditText mBeizhu;
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
    @BindView(R.id.tv_zhuanfa)
    TextView mZhaunfa;
    @BindView(R.id.tv_huiqian)
    TextView mTvHuiqian;
    @BindView(R.id.ll_huiqianren)
    LinearLayout mLlHuiqianren;
    @BindView(R.id.xxre_huiqianren)
    XXRecycleView mXxreHuiqianren;
    private String mFilename = "";
    private String mFilePath = "";
    private String mSessionId;
    private String mUserName;
    private String mUserId;
    private String mDepartmentId;
    private String mDepartmentName;
    private String processDefinitionId;
    private CommonRecyclerAdapter<ZuzhiUserBean> mHuiqianAdapter;
    private List<ZuzhiUserBean> datas2 = new ArrayList<>();
    private XXDialog mxxDialog2;
    private XXDialog mxxUsersDialog;

    @Override
    protected int getChildLayoutRes() {
        return R.layout.activity_shouwen;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("文件阅办单");
        initThisView();
    }

    private void initThisView() {
        mSessionId = SPUtils.getString(this, "sessionId");
        mUserName = SPUtils.getString(this, "userName");
        mUserId = SPUtils.getString(this, "userId");
        mDepartmentId = SPUtils.getString(this, "departmentId");
        mDepartmentName = SPUtils.getString(this, "departmentName");
        processDefinitionId = getIntent().getStringExtra("processDefinitionId");

        //添加会签人
        mXxreHuiqianren.setLayoutManager(new GridLayoutManager(this, 4));
        mHuiqianAdapter = new CommonRecyclerAdapter<ZuzhiUserBean>(this, datas2, R.layout.item_add_person) {
            @Override
            public void convert(CommonViewHolder holder, final ZuzhiUserBean item, int i, boolean b) {
                holder.setText(R.id.name, item.getName());
                holder.getView(R.id.delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mHuiqianAdapter.remove(item);
                    }
                });
            }
        };
        mXxreHuiqianren.setAdapter(mHuiqianAdapter);

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
                            case "title":
                                mTitle.setText(value);
                                break;
                            case "id":
                                mBianhao.setText(value);
                                break;
                            case "departments":
                                mDanwei.setText(value);
                                break;
                            case "petition":
                                mName.setText(value);
                                break;
                            case "remarks":
                                mBeizhu.setText(value);
                                break;
                            // 会签人  739133491527680,739136820166656
                            case "leader":
                                mZhaunfa.setText(value);
                                break;
                            case "comment":
                                mZhaunfa.setText(value);
                                break;
                            // 附件
                            case "enclosure":
                                break;
                        }
                        if ("userpicker".equals(bean.getType())) {
                            mLlHuiqianren.setVisibility(View.VISIBLE);
                            mXxreHuiqianren.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<QingjiaShenheResponse> response) {
                Toast.makeText(ShouWenActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }


    int REQUESTCODE_FROM_ACTIVITY = 1000;

    @OnClick({R.id.wjybd_bianhao, R.id.ll_zhuanfa, R.id.add_fujian, R.id.btn_uplaod, R.id.btn_cancel,
            R.id.rl_fujian, R.id.btn_caogao, R.id.btn_commit, R.id.ll_huiqianren})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.wjybd_bianhao:
                if("0".equals(mBianhao.getTag())) {
                    mBianhao.setTag("1");
                    houQuFormCode();
                }
                break;
            case R.id.ll_zhuanfa:
                List<String> datas_gongju = new ArrayList();
                datas_gongju.add("是");
                datas_gongju.add("否");
                chooseDate(datas_gongju, mZhaunfa, "是否转发给大领导");
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
                // 如果有附件但是没有上传
                if(!TextUtils.isEmpty(mFilePath) && TextUtils.isEmpty(mFilePathReturn)){
                    Toast.makeText(ShouWenActivity.this, "请先提交附件",Toast.LENGTH_LONG).show();
                } else {
                    RequestServerGoodsLingqu_Save(
                            mTitle.getText().toString().trim(),
                            mName.getText().toString().trim(),
                            mBianhao.getText().toString().trim(),
                            mZhaunfa.getText().toString().trim(),
                            mBeizhu.getText().toString().trim()
                    );
                }
                break;
            case R.id.btn_commit:
                // 如果有附件但是没有上传
                if(!TextUtils.isEmpty(mFilePath) && TextUtils.isEmpty(mFilePathReturn)){
                    Toast.makeText(ShouWenActivity.this, "请先提交附件",Toast.LENGTH_LONG).show();
                } else {
                    jianYanshuju();
                }
                break;
            case R.id.ll_huiqianren:
//                RequestServerGetZuzhi("选择会签人");
                if("0".equals(mLlHuiqianren.getTag())) {
                    mLlHuiqianren.setTag("1");
                    RequestServerGetZuzhi(mLlHuiqianren, mTvHuiqian, "请选择会签人", mHuiqianAdapter);
                }
                break;
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
                if (null != response && null != response.get() && null != response.get().getData()) {
                    Toast.makeText(ShouWenActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
//                    mBtnCancel.setVisibility(View.VISIBLE);
                    mBtnUplaod.setVisibility(View.GONE);
                    mBtnUplaod.setEnabled(false);
                    mAddFujian.setTag("1");
                    mFilePathReturn = response.get().getData();
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(ShouWenActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
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
            //Toast.makeText(ShouWenActivity.this, "ok", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(int what, Exception exception) {

        }
    };

    /**
     * 填写的数据进行校验
     */
    private void jianYanshuju() {
        if (TextUtils.isEmpty(mDanwei.getText().toString().trim())) {
            Toast.makeText(this, "请填写来文单位", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mTitle.getText().toString().trim())) {
            Toast.makeText(this, "请填写文件标题", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mName.getText().toString().trim())) {
            Toast.makeText(this, "请填写呈请", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mBianhao.getText().toString().trim())) {
            Toast.makeText(this, "请点击获取文号", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mZhaunfa.getText().toString().trim())) {
            Toast.makeText(this, "是否转发给大领导", Toast.LENGTH_SHORT).show();
        } else if (0 >= mHuiqianAdapter.getDatas().size()) {
            Toast.makeText(this, "请选择会签人", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mBeizhu.getText().toString().trim())) {
            Toast.makeText(this, "请填写备注", Toast.LENGTH_SHORT).show();
        } else {
            RequestServerGoodsLingqu(
                    mTitle.getText().toString().trim(),
                    mName.getText().toString().trim(),
                    mBianhao.getText().toString().trim(),
                    mZhaunfa.getText().toString().trim(),
                    mBeizhu.getText().toString().trim()
            );
        }
    }

    /**
     * 请求网络，提交数据b
     */
    private void RequestServerGoodsLingqu(String title, String name, String bianhao, String zhuanfayijian, String beizhu) {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_STARTPROCESS,
                RequestMethod.POST, ProcessJieguoResponse.class);

        StringBuffer leadersID = new StringBuffer();
        StringBuffer leadersName = new StringBuffer();

        for (ZuzhiUserBean bean : mHuiqianAdapter.getDatas()) {
            leadersID.append(bean.getId()).append(",");
            leadersName.append(bean.getName()).append(",");
        }

        if (leadersID.toString().endsWith(",")) {
            leadersID.deleteCharAt(leadersID.toString().length() - 1);
        }
        if (leadersName.toString().endsWith(",")) {
            leadersName.deleteCharAt(leadersName.toString().length() - 1);
        }

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"departments\":" + "\"" + mDanwei.getText().toString() + "\",")
//                .append("\"departments_name\":" + "\"" + mDepartmentName + "\",")
                .append("\"id\":" + "\"" + bianhao + "\",")
                .append("\"title\":" + "\"" + title + "\",")
                .append("\"petition\":" + "\"" + name + "\",")
                .append("\"remarks\":" + "\"" + beizhu + "\",")
                .append("\"comment\":" + "\"" + zhuanfayijian + "\",")
                .append("\"leader_name\":" + "\"" + leadersName.toString() + "\",")
                .append("\"leader\":" + "\"" + leadersID.toString() + "\"")
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
                        Toast.makeText(ShouWenActivity.this, "流程发起成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(ShouWenActivity.this, "流程发起失败", Toast.LENGTH_SHORT).show();
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
     * 请求网络接口 获取编号
     */
    private void houQuFormCode() {

        //创建请求队列
        RequestQueue requestQueue = NoHttp.newRequestQueue();
        //创建请求
        Request<FormBianmaBean> request = new JavaBeanRequest<>(UrlConstance.URL_BIAODAN_CODE, RequestMethod.POST, FormBianmaBean.class);
        //添加url?key=value形式的参数
        request.add("typecode", "zjsq");
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
                Toast.makeText(ShouWenActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
                mBianhao.setTag("0");
            }

            @Override
            public void onFinish(int what) {

            }
        });

    }

    /**
     * 保存草稿
     */
    private void RequestServerGoodsLingqu_Save(String title, String name, String bianhao, String zhuanfayijian, String beizhu) {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_SAVEDRAFT,
                RequestMethod.POST, ProcessJieguoResponse.class);

        StringBuffer leadersID = new StringBuffer();
        StringBuffer leadersName = new StringBuffer();

        for (ZuzhiUserBean bean : mHuiqianAdapter.getDatas()) {
            leadersID.append(bean.getId()).append(",");
            leadersName.append(bean.getName()).append(",");
        }

        if (leadersID.toString().endsWith(",")) {
            leadersID.deleteCharAt(leadersID.toString().length() - 1);
        }
        if (leadersName.toString().endsWith(",")) {
            leadersName.deleteCharAt(leadersName.toString().length() - 1);
        }

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"departments\":" + "\"" + mDanwei.getText().toString() + "\",")
//                .append("\"departments_name\":" + "\"" + mDepartmentName + "\",")
                .append("\"id\":" + "\"" + bianhao + "\",")
                .append("\"title\":" + "\"" + title + "\",")
                .append("\"petition\":" + "\"" + name + "\",")
                .append("\"remarks\":" + "\"" + beizhu + "\",")
                .append("\"comment\":" + "\"" + zhuanfayijian + "\",")
                .append("\"leader_name\":" + "\"" + leadersName.toString() + "\",")
                .append("\"leader\":" + "\"" + leadersID.toString() + "\"")
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
                        Toast.makeText(ShouWenActivity.this, "已保存至草稿箱", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(ShouWenActivity.this, "保存草稿失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(ShouWenActivity.this, "保存草稿失败", Toast.LENGTH_SHORT).show();
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
