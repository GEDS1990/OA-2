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
import com.example.administrator.oa.view.bean.FilePreviewResponse;
import com.example.administrator.oa.view.bean.FormBianmaBean;
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
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/7/6.
 */

public class BanwenActivity extends HeadBaseActivity {
    @BindView(R.id.bianhao)
    TextView mBianhao;
    @BindView(R.id.danwei_danwei)
    EditText mDanweiDanwei;
    @BindView(R.id.title)
    EditText mTitle;
    @BindView(R.id.beizhu)
    EditText mBeizhu;
    @BindView(R.id.icon)
    ImageView mIcon;
    @BindView(R.id.file_name)
    TextView mFileName;
    @BindView(R.id.filesize)
    TextView mFilesize;
    @BindView(R.id.btn_cancel)
    ImageView mBtnCancel;
    @BindView(R.id.btn_caogao)
    Button mBtnCaogao;
    @BindView(R.id.btn_commit)
    Button mBtnCommit;
    @BindView(R.id.add_fujian)
    RelativeLayout mAddFujian;
    @BindView(R.id.pb)
    ProgressBar mpb;
    @BindView(R.id.btn_uplaod)
    ImageView mBtnUplaod;
    @BindView(R.id.rl_fujian)
    RelativeLayout mRlFujian;

    // 流程审核记录
    @BindView(R.id.txtProcess)
    TextView txtProcess;
    @BindView(R.id.xxreProcess)
    XXRecycleView mProcessXxre;
    private CommonRecyclerAdapter<ProcessShenheHistoryBean> mAdapter;
    private List<ProcessShenheHistoryBean> datas = new ArrayList<>();
    private String mSessionId;
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
    boolean isDownLoading;

    @Override
    protected int getChildLayoutRes() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return R.layout.activity_banwen;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("办文流程");
        initThisView();
    }

    private void initThisView() {
        mSessionId = SPUtils.getString(this, "sessionId");
        processDefinitionId = getIntent().getStringExtra("processDefinitionId");
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
                            //当有type为userpicker的时候说明是可以发起会签的节点
                            String label = bean.getName();
                            String value = bean.getValue();
                            switch (label) {
                                case "id":
                                    mBianhao.setText(value);
                                    break;
                                case "company":
                                    mDanweiDanwei.setText(value);
                                    break;
                                case "title":
                                    mTitle.setText(value);
                                    break;
                                case "remarks":
                                    mBeizhu.setText(value);
                                    break;
                                case "enclosure":
                                    if(!TextUtils.isEmpty(bean.getLabel())) {
                                        // 实时请求权限
                                        CommonUtil.verifyStoragePermissions(BanwenActivity.this);
                                        mFilePathReturn = value;
                                        mFileNameReturn = bean.getLabel();
                                        if(!TextUtils.isEmpty(bean.getSize())) {
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
                Toast.makeText(BanwenActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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
     * 请求网络接口-流程审核记录
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
                    if (null != mAdapter) {
                        mAdapter.replaceAll(data);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessShenheHistoryRes> response) {
                Toast.makeText(BanwenActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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
                    for (QingjiaShenheBean bean : shenheBeen) {
                        if(!TextUtils.isEmpty(bean.getName()) && !TextUtils.isEmpty(bean.getValue())) {
                            //当有type为userpicker的时候说明是可以发起会签的节点
                            String label = bean.getName();
                            String value = bean.getValue();
                            switch (label) {
                                case "id":
                                    mBianhao.setText(value);
                                    break;
                                case "company":
                                    mDanweiDanwei.setText(value);
                                    break;
                                case "title":
                                    mTitle.setText(value);
                                    break;
                                case "remarks":
                                    mBeizhu.setText(value);
                                    break;
                                case "enclosure":
                                    if(!TextUtils.isEmpty(bean.getLabel())) {
                                        // 实时请求权限
                                        CommonUtil.verifyStoragePermissions(BanwenActivity.this);
                                        mFilePathReturn = value;
                                        mFileNameReturn = bean.getLabel();
                                        if(!TextUtils.isEmpty(bean.getSize())) {
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
                Toast.makeText(BanwenActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {

            }
        });
    }

    @OnClick({R.id.bwlc_bianhao, R.id.btn_cancel, R.id.btn_caogao, R.id.btn_commit, R.id.add_fujian, R.id.btn_uplaod, R.id.rl_fujian})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bwlc_bianhao:
                if("0".equals(mBianhao.getTag())) {
                    mBianhao.setTag("1");
                    houQuFormCode();
                }
                break;
            case R.id.btn_cancel:
                mBtnUplaod.setVisibility(View.VISIBLE);
                mBtnCancel.setVisibility(View.GONE);
                mRlFujian.setVisibility(View.GONE);
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
            // 点击预览文件
            case R.id.rl_fujian:
                // 如果是已上传附件，则调用webview查看
                if(!TextUtils.isEmpty(mFilePathReturn)){
                    RequestServerForViewFile();
                } else {
                    FileUtils.openLocalFile(BanwenActivity.this, mFilePath);
                }
                break;
            case R.id.btn_caogao:
                // 如果有附件但是没有上传
                if(!TextUtils.isEmpty(mFilePath) && TextUtils.isEmpty(mFilePathReturn)){
                    Toast.makeText(BanwenActivity.this, "您已选择附件，请先上传附件",Toast.LENGTH_LONG).show();
                } else {
                    RequestServerCommit_Save(mDanweiDanwei.getText().toString().trim(),
                            mTitle.getText().toString().trim(),
                            mBeizhu.getText().toString().trim(),
                            mBianhao.getText().toString().trim(),
                            mFilePathReturn
                    );
                }
                break;
            case R.id.btn_commit:
                jianYanshuju();
                break;
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
                    path = FileUtils.getPath(BanwenActivity.this, uri);
                } else {//4.4以下下系统调用方法
                    path = FileUtils.getRealPathFromURI(BanwenActivity.this, uri);
                }
                if (!TextUtils.isEmpty(path)) {
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
     * 预览附件
     */
    private void RequestServerForViewFile() {
        //创建请求队列
        RequestQueue ProcessQueue = NoHttp.newRequestQueue();
        //创建请求
        Request<FilePreviewResponse> request = new JavaBeanRequest<>(UrlConstance.URL_PREVIEW,
                RequestMethod.POST, FilePreviewResponse.class);
        request.add("filePath", mFilePathReturn);
        ProcessQueue.add(0, request, new OnResponseListener<FilePreviewResponse>() {
            @Override
            public void onStart(int what) {
            }

            @Override
            public void onSucceed(int what, Response<FilePreviewResponse> response) {
                if (null != response && null != response.get()) {
                    if (response.get().getCode() == 200) {
                        if(null != response.get().getData()) {
                            FileUtils.openWebFile(BanwenActivity.this, response.get().getData().getViewurl());
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<FilePreviewResponse> response) {
                Toast.makeText(BanwenActivity.this, "预览失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
            }
        });

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
                if (response != null && response.get() != null && response.get().getData() != null) {
                    Toast.makeText(BanwenActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                    mBtnUplaod.setVisibility(View.GONE);
                    mBtnUplaod.setEnabled(false);
                    mAddFujian.setTag("1");
                    mFilePathReturn = response.get().getData();
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(BanwenActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
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
            mpb.setProgress(100);
            mBtnUplaod.setVisibility(View.GONE);
            mBtnUplaod.setEnabled(false);
            mAddFujian.setTag("1");
            return true;
        } else {
            // 将文件显示在界面
            getFileInfo(filePath);
            mFilesize.setText(ShowLongFileSzie(fileSize));
            mpb.setProgress(0);
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
                Toast.makeText(BanwenActivity.this, "附件下载失败", Toast.LENGTH_SHORT).show();
                isDownLoading = false;
            }

            @Override
            public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long allCount) {
                isDownLoading = true;
            }

            @Override
            public void onProgress(int what, int progress, long fileCount, long speed) {
                downLoadFileSize = fileCount;
                if(0 < fileSize) {
                    long mProgress = downLoadFileSize * 100 / fileSize;
                    mpb.setProgress((int)mProgress);
                }
                mFilesize.setText(ShowProcessFileSzie(downLoadFileSize));
                isDownLoading  = true;
            }

            @Override
            public void onFinish(int what, String filePath) {
                Log.w("2333", "onFinish" + filePath);
                Toast.makeText(BanwenActivity.this, "附件下载完成", Toast.LENGTH_SHORT).show();
                mFilesize.setText(ShowLongFileSzie(downLoadFileSize));
                mpb.setProgress(100);
                isDownLoading = false;
            }

            @Override
            public void onCancel(int what) {
                Log.w("2333", "onCancel");
                isDownLoading = false;
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
            mpb.setProgress(progress);
        }

        @Override
        public void onFinish(int what) {
        }

        @Override
        public void onError(int what, Exception exception) {

        }
    };

    /**
     * 请求网络接口 获取编号
     */
    private void houQuFormCode() {

        //创建请求队列
        RequestQueue requestQueue = NoHttp.newRequestQueue();
        //创建请求
        Request<FormBianmaBean> request = new JavaBeanRequest<>(UrlConstance.URL_BIAODAN_CODE, RequestMethod.POST, FormBianmaBean.class);
        //添加url?key=value形式的参数
        request.add("typecode", "wenhao");
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
                Toast.makeText(BanwenActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
                mBianhao.setTag("0");
            }

            @Override
            public void onFinish(int what) {

            }
        });
    }

    /**
     * 检验必填信息
     */
    private void jianYanshuju() {
        if (TextUtils.isEmpty(mBianhao.getText().toString().trim())) {
            Toast.makeText(this, "请点击获取编号", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mDanweiDanwei.getText().toString().trim())) {
            Toast.makeText(this, "请填写来文单位", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mTitle.getText().toString().trim())) {
            Toast.makeText(this, "请填写文章标题", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mBeizhu.getText().toString().trim())) {
            Toast.makeText(this, "请填写备注信息", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(mFilePathReturn)){
            Toast.makeText(this, "请上传附件",Toast.LENGTH_SHORT).show();
        } else {
            if(!TextUtils.isEmpty(formCode) && !"caogao".equals(formCode)){
                RequestServerReCommit();
            } else {
                RequestServerCommit(mDanweiDanwei.getText().toString().trim(),
                        mTitle.getText().toString().trim(),
                        mBeizhu.getText().toString().trim(),
                        mBianhao.getText().toString().trim(),
                        mFilePathReturn
                );
            }
        }
    }

    /**
     * 请求网络接口,发起流程
     */
    private void RequestServerCommit(String dawei, String title, String beizhu, String code, String filePath) {
        String sessionId = SPUtils.getString(this, "sessionId");

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"id\":" + "\"" + code + "\",")
                .append("\"company\":" + "\"" + dawei + "\",")
                .append("\"title\":" + "\"" + title + "\",")
                .append("\"remarks\":" + "\"" + beizhu + "\",")
                .append("\"businessKey\":" + "\"" + businessKey + "\",")
                .append("\"enclosure\":" + "\"" + filePath + "\"")
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
                        Toast.makeText(BanwenActivity.this, "流程发起成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(BanwenActivity.this, "流程发起失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (null != mLoadingDialog) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }

    /**
     * 保存草稿
     */
    private void RequestServerCommit_Save(String dawei, String title, String beizhu, String code, String filePath) {
        String sessionId = SPUtils.getString(this, "sessionId");

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"id\":" + "\"" + code + "\",")
                .append("\"company\":" + "\"" + dawei + "\",")
                .append("\"title\":" + "\"" + title + "\",")
                .append("\"remarks\":" + "\"" + beizhu + "\",")
                .append("\"businessKey\":" + "\"" + businessKey + "\",")
                .append("\"enclosure\":" + "\"" + filePath + "\"")
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
                if (null != mLoadingDialog) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<ProcessJieguoResponse> response) {
                Log.w("2222", response.toString());
                if (null != response && null != response.get()) {
                    if (response.get().getCode() == 200) {
                        Toast.makeText(BanwenActivity.this, "已保存至草稿箱", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(BanwenActivity.this, "保存草稿失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (null != mLoadingDialog) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }

    /**
     * 提交页面数据，完成当前审核
     */
    private void RequestServerReCommit() {
        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"company\":" + "\"" + mDanweiDanwei.getText().toString() + "\",")
                .append("\"title\":" + "\"" + mTitle.getText().toString() + "\",")
                .append("\"id\":" + "\"" + mBianhao.getText().toString() + "\",")
                .append("\"remarks\":" + "\"" + mBeizhu.getText().toString() + "\",")
                .append("\"enclosure\":" + "\"" + mFilePathReturn + "\"")
                .append("}");

        //创建请求队列
        RequestQueue Queue = NoHttp.newRequestQueue();
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
                        Toast.makeText(BanwenActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(BanwenActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (null != mLoadingDialog) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }
}
