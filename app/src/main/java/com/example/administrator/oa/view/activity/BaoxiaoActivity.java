package com.example.administrator.oa.view.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
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
import com.example.administrator.oa.view.bean.ProcessShenheHistoryBean;
import com.example.administrator.oa.view.bean.ProcessShenheHistoryRes;
import com.example.administrator.oa.view.bean.QingjiaShenheBean;
import com.example.administrator.oa.view.bean.QingjiaShenheResponse;
import com.example.administrator.oa.view.constance.UrlConstance;
import com.example.administrator.oa.view.net.JavaBeanRequest;
import com.example.administrator.oa.view.utils.CommonUtil;
import com.example.administrator.oa.view.utils.FileUtils;
import com.example.administrator.oa.view.utils.SPUtils;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.lsh.XXRecyclerview.CommonRecyclerAdapter;
import com.lsh.XXRecyclerview.CommonViewHolder;
import com.lsh.XXRecyclerview.XXRecycleView;
import com.yanzhenjie.nohttp.FileBinary;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.OnUploadListener;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.download.DownloadQueue;
import com.yanzhenjie.nohttp.download.DownloadRequest;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
    // 流程审核记录
    @BindView(R.id.txtProcess)
    TextView txtProcess;
    @BindView(R.id.xxreProcess)
    XXRecycleView mProcessXxre;
    private CommonRecyclerAdapter<ProcessShenheHistoryBean> mAdapter;
    private List<ProcessShenheHistoryBean> datas = new ArrayList<>();
    // 审核信息
    private String formCode = "";
    private String mTaskId = "";
    // 草稿信息
    private String processDefinitionId = "";
    private String businessKey = "";
    // 附件信息
    private String mFilename = "";
    private String mFilePath = "";
    private String mFilePathReturn = "";
    private String mFileNameReturn = "";
    // 文件总大小
    long fileSize;
    // 已下载的大小
    long downLoadFileSize;

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

        formCode = getIntent().getStringExtra("formCode");
        // 如果formcode不是空的，则进入草稿
        if(!TextUtils.isEmpty(formCode)) {
            // 草稿
            if("caogao".equals(formCode)) {
                businessKey = getIntent().getStringExtra("businessKey");
                // 获取草稿信息或者是审核信息
                RequestServerGetInfoFromCaoGao();
            }
            // 否则就是审核的重新提交
            else {
                mTaskId = getIntent().getStringExtra("taskId");
                txtProcess.setVisibility(View.VISIBLE);
                mProcessXxre.setVisibility(View.VISIBLE);
                mBtnCaogao.setVisibility(View.GONE);
                mBtnCommit.setText("提交");
                RequestServerGetInfoFromShenHe();
                // 实现流程记录的adapter
                mProcessXxre.setLayoutManager(new LinearLayoutManager(this));
                mAdapter = new CommonRecyclerAdapter<ProcessShenheHistoryBean>(this, datas, R.layout.item_myprocess_shenhejilu) {
                    @Override
                    public void convert(CommonViewHolder holder, ProcessShenheHistoryBean item, int i, boolean b) {
                        holder.setText(R.id.processNameContent, item.getName());
                        holder.setText(R.id.name, item.getAssignee());
                        holder.setText(R.id.startTimeContent, item.getCreateTime());
                        holder.setText(R.id.completeTimeContent, item.getCompleteTime());
                    }
                };
                mProcessXxre.setAdapter(mAdapter);
            }
        }
    }

    /**
     * 获取草稿信息
     */
    private void RequestServerGetInfoFromCaoGao() {
        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<QingjiaShenheResponse> request = new JavaBeanRequest<>(UrlConstance.URL_CAOGAOXIANG_INFO,
                RequestMethod.POST, QingjiaShenheResponse.class);
        //添加url?key=value形式的参数
        request.addHeader("sessionId", mSessionId);
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
                                case "reason":
                                    mBaoxiaoYuanyin.setText(value);
                                    break;
                                case "date":
                                    mStart.setText(value);
                                    break;
                                case "RMB":
                                    mMoneyRmb.setText(value);
                                    break;
                                case "money":
                                    mMoneyNum.setText(value);
                                    break;
                                case "enclosure":
                                    if (!TextUtils.isEmpty(bean.getLabel())) {
                                        // 实时请求权限
                                        CommonUtil.verifyStoragePermissions(BaoxiaoActivity.this);
                                        mFilePathReturn = value;
                                        mFileNameReturn = bean.getLabel();
                                        if (!TextUtils.isEmpty(bean.getSize())) {
                                            fileSize = Integer.parseInt(bean.getSize());
                                        }
                                        // 如果文件没有下载过，则开始下载
                                        checkFileExisted();
                                    }
                                    break;
                            }
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

    /**
     * 获取审核信息
     */
    private void RequestServerGetInfoFromShenHe() {
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
                Toast.makeText(BaoxiaoActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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
                    for (QingjiaShenheBean bean : shenheBeen) {
                        if(!TextUtils.isEmpty(bean.getName()) && !TextUtils.isEmpty(bean.getValue())) {
                            Log.d("Caogao", bean.getName());
                            Log.d("Caogao", bean.getValue());
                            //当有type为userpicker的时候说明是可以发起会签的节点
                            String label = bean.getName();
                            String value = bean.getValue();
                            switch (label) {
                                case "name":
                                    mName.setText(value);
                                    break;
                                case "departments":
                                    mBumen.setText(value);
                                    break;
                                case "reason":
                                    mBaoxiaoYuanyin.setText(value);
                                    break;
                                case "date":
                                    mStart.setText(value);
                                    break;
                                case "RMB":
                                    mMoneyRmb.setText(value);
                                    break;
                                case "money":
                                    mMoneyNum.setText(value);
                                    break;
                                case "enclosure":
                                    if (!TextUtils.isEmpty(bean.getLabel())) {
                                        // 实时请求权限
                                        CommonUtil.verifyStoragePermissions(BaoxiaoActivity.this);
                                        mFilePathReturn = value;
                                        mFileNameReturn = bean.getLabel();
                                        if (!TextUtils.isEmpty(bean.getSize())) {
                                            fileSize = Integer.parseInt(bean.getSize());
                                        }
                                        // 如果文件没有下载过，则开始下载
                                        checkFileExisted();
                                    }
                                    break;
                            }
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
                selectDate("", mStart, "");
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
//                    new LFilePicker()
//                            .withActivity(this)
//                            .withRequestCode(REQUESTCODE_FROM_ACTIVITY)
//                            .start();
//                    showFileChooser();
                    // 检测是否有存储权限
                    applyForPermission();
                }
                break;
            case R.id.btn_uplaod:
                if("down".equals(mBtnUplaod.getTag().toString())) {
                    mBtnUplaod.setVisibility(View.GONE);
                    mBtnUplaod.setEnabled(false);
                    RequestServerDownLoad();
                } else {
                    if (!TextUtils.isEmpty(mFilePath) && !TextUtils.isEmpty(mFilename)) {
                        RequestServer(mFilePath, mFilename);
                    }
                }
                break;
            case R.id.btn_cancel:
                break;
            case R.id.rl_fujian:
                break;
            case R.id.btn_caogao:
                // 如果有附件但是没有上传
                if(!TextUtils.isEmpty(mFilePath) && TextUtils.isEmpty(mFilePathReturn)){
                    Toast.makeText(BaoxiaoActivity.this, "您已选择附件，请先上传附件",Toast.LENGTH_LONG).show();
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
                jianYanshuju();
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
        } else if(TextUtils.isEmpty(mFilePathReturn)){
            Toast.makeText(this, "请上传附件",Toast.LENGTH_SHORT).show();
        } else {
            if(!TextUtils.isEmpty(formCode) && !"caogao".equals(formCode)){
                RequestServerReCommit();
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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE_FROM_ACTIVITY) {
                Uri uri = data.getData();
                String path = "";
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                    path = FileUtils.getPath(BaoxiaoActivity.this, uri);
                } else {//4.4以下下系统调用方法
                    path = FileUtils.getRealPathFromURI(BaoxiaoActivity.this, uri);
                }

                if(!TextUtils.isEmpty(path)){
                    getFileInfo(path);
                }
            } else if (requestCode == REQUESTCODE_SETTION_PERMISSION) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 检查该权限是否已经获取
                    int i = ContextCompat.checkSelfPermission(this, permissions[0]);
                    // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                    if (i != PackageManager.PERMISSION_GRANTED) {
                        // 提示用户应该去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting();
                    } else {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        showFileChooser();
                    }
                }
            }
        }
    }

    /**
     * 界面显示附件信息
     * @param filePath
     */
    private void getFileInfo(String filePath){
        mRlFujian.setVisibility(View.VISIBLE);
        mFilePath = filePath;
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

    }

    /**
     * 上传附件
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
     * 判断文件是否已下载过
     * @return
     */
    private boolean checkFileExisted(){
        // 实时请求权限
        CommonUtil.verifyStoragePermissions(this);
        // 判断sd卡是否可读写
        String sdStatus = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(sdStatus)) {
            // 检测sd是否可用
            Log.d("TestFile", "SD card is not avaiable/writeable right now.");
            return false;
        }
        // 创建根目录文件夹
        String rootPath = CommonUtil.createRootFile(this);
        // 拼接成本地路径
        String filePath = rootPath + mFileNameReturn;
        // 如果文件存在，则直接显示
        File file = new File(rootPath, mFileNameReturn);
        if(file.exists()) {
            // 将文件显示在界面
            getFileInfo(filePath);
            mPb.setProgress(100);
            mBtnUplaod.setVisibility(View.GONE);
            mBtnUplaod.setEnabled(false);
            mAddFujian.setTag("1");
            return true;
        } else {
            // 将文件显示在界面
            getFileInfo(filePath);
            mFilesize.setText(ShowLongFileSzie(fileSize));
            mPb.setProgress(0);
            mBtnUplaod.setVisibility(View.VISIBLE);
            mBtnUplaod.setImageDrawable(getResources().getDrawable(R.drawable.download));
            mBtnUplaod.setEnabled(true);
            mBtnUplaod.setTag("down");
            mAddFujian.setTag("1");
            return false;
        }
    }

    /**
     * 下载附件
     */
    private void RequestServerDownLoad(){
        // 实时请求权限
        CommonUtil.verifyStoragePermissions(this);
        String rootPath = CommonUtil.createRootFile(this);
        DownloadQueue downloadQueue = NoHttp.newDownloadQueue();
        //下载文件
        DownloadRequest downloadRequest = NoHttp.createDownloadRequest(UrlConstance.URL_DOWNLOAD,
                RequestMethod.POST, rootPath, mFileNameReturn, true, false);
        downloadRequest.add("filePath", mFilePathReturn);
        downloadQueue.add(0, downloadRequest, new DownloadListener() {
            @Override
            public void onDownloadError(int what, Exception exception) {
                Log.w("2333", "onDownloadError" + exception.toString());
                Toast.makeText(BaoxiaoActivity.this, "附件下载失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long allCount) {
//                fileSize = allCount;
//                Toast.makeText(BaoxiaoActivity.this, "开始下载附件", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(int what, int progress, long fileCount, long speed) {
                downLoadFileSize = fileCount;
                if(0 < fileSize) {
                    long mProgress = downLoadFileSize * 100 / fileSize;
//                Log.w("2333", "mProgress=" + mProgress);
                    mPb.setProgress((int)mProgress);
                }
                mFilesize.setText(ShowProcessFileSzie(downLoadFileSize));
            }

            @Override
            public void onFinish(int what, String filePath) {
                Log.w("2333", "onFinish" + filePath);
                Toast.makeText(BaoxiaoActivity.this, "附件下载完成", Toast.LENGTH_SHORT).show();
                mFilesize.setText(ShowLongFileSzie(downLoadFileSize));
                mPb.setProgress(100);
            }

            @Override
            public void onCancel(int what) {
                Log.w("2333", "onCancel");
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
                .append("\"departments\":" + "\"" + bumen + "\",")
                .append("\"departments_id\":" + "\"" + mDepartmentId + "\",")
                .append("\"name\":" + "\"" + mUserName + "\",")
                .append("\"reason\":" + "\"" + reason + "\",")
                .append("\"date\":" + "\"" + date + "\",")
                .append("\"RMB\":" + "\"" + money1 + "\",")
                .append("\"money\":" + "\"" + money2 + "\",")
//                .append("\"manager_name\":" + "\"" + leader + "\",")
//                .append("\"manager\":" + "\"" + mZuzhiUserBean.getId() + "\",")
//                .append("\"comment\":" + "\"" + comment + "\"")
                .append("\"businessKey\":" + "\"" + businessKey + "\",")
                .append("\"enclosure\":" + "\"" + mFilePathReturn + "\"")
                .append("}");
        //添加url?key=value形式的参数
        request.addHeader("sessionId", mSessionId);
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
                .append("\"departments\":" + "\"" + bumen + "\",")
                .append("\"departments_id\":" + "\"" + mDepartmentId + "\",")
                .append("\"businessKey\":" + "\"" + businessKey + "\",")
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

    /**
     * 提交页面数据，完成当前审核
     */
    private void RequestServerReCommit() {
        //拼接data的json
        String bumen = mBumen.getText().toString();
        String name = mName.getText().toString();
        String starttime = mStart.getText().toString();
        String moneyRmb = mMoneyRmb.getText().toString();
        String moneyNum = mMoneyNum.getText().toString();
        String reason = mBaoxiaoYuanyin.getText().toString();

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"departments_name\":" + "\"" + bumen + "\",")
                .append("\"name\":" + "\"" + name + "\",")
                .append("\"date\":" + "\"" + starttime + "\",")
                .append("\"RMB\":" + "\"" + moneyRmb + "\",")
                .append("\"money\":" + "\"" + moneyNum + "\",")
                .append("\"reason\":" + "\"" + reason + "\",")
                .append("\"enclosure\":" + "\"" + mFilePathReturn + "\"")
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
                        Toast.makeText(BaoxiaoActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(BaoxiaoActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
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
