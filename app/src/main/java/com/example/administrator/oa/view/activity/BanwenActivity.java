package com.example.administrator.oa.view.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Environment;
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
import com.example.administrator.oa.view.bean.FormBianmaBean;
import com.example.administrator.oa.view.bean.ProcessJieguoResponse;
import com.example.administrator.oa.view.bean.QingjiaShenheBean;
import com.example.administrator.oa.view.bean.QingjiaShenheResponse;
import com.example.administrator.oa.view.constance.UrlConstance;
import com.example.administrator.oa.view.net.JavaBeanRequest;
import com.example.administrator.oa.view.utils.SPUtils;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.tbruyelle.rxpermissions.RxPermissions;
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
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

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
    ImageView mbtnUplaod;
    @BindView(R.id.rl_fujian)
    RelativeLayout mRl_fujian;
    private String mFilename = "";
    private String mFilePath = "";

    private String  processDefinitionId;

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
        processDefinitionId = getIntent().getStringExtra("processDefinitionId");
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
        request.add("processDefinitionId",  processDefinitionId);
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
//                    mDanweiDanwei.setText(shenheBeen.get(0).getValue());
//                    mBianhao.setText(shenheBeen.get(1).getValue());
//                    mTitle.setText(shenheBeen.get(2).getValue());
//                    mBeizhu.setText(shenheBeen.get(4).getValue());

                    for (QingjiaShenheBean bean : shenheBeen) {
                        Log.d("Caogao", bean.getLabel());
                        Log.d("Caogao", bean.getValue());
                        //当有type为userpicker的时候说明是可以发起会签的节点
                        String label = bean.getLabel();
                        String value = bean.getValue();
                        switch (label) {
                            case "id":
                                mBianhao.setText(value);
                                break;
//                            case "reason":
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

    @OnClick({R.id.bwlc_bianhao, R.id.btn_cancel, R.id.btn_caogao, R.id.btn_commit, R.id.add_fujian, R.id.btn_uplaod})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bwlc_bianhao:
                if("0".equals(mBianhao.getTag())) {
                    mBianhao.setTag("1");
                    houQuFormCode();
                }
                break;
            case R.id.btn_cancel:
                mbtnUplaod.setVisibility(View.VISIBLE);
                mBtnCancel.setVisibility(View.GONE);
                mRl_fujian.setVisibility(View.GONE);
                break;
            case R.id.btn_uplaod:
                if (!TextUtils.isEmpty(mFilePath) && !TextUtils.isEmpty(mFilename)) {
                    RequestServer(mFilePath, mFilename);
                }
                break;
            case R.id.add_fujian:
                if("0".equals(mAddFujian.getTag())) {
                    new LFilePicker()
                            .withActivity(this)
                            .withRequestCode(REQUESTCODE_FROM_ACTIVITY)
                            .start();
                }
                break;
            case R.id.btn_caogao:
                // 如果有附件但是没有上传
                if(!TextUtils.isEmpty(mFilePath) && TextUtils.isEmpty(mFilePathReturn)){
                    Toast.makeText(BanwenActivity.this, "请先提交附件",Toast.LENGTH_LONG).show();
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
                // 如果有附件但是没有上传
                if(!TextUtils.isEmpty(mFilePath) && TextUtils.isEmpty(mFilePathReturn)){
                    Toast.makeText(BanwenActivity.this, "请先提交附件",Toast.LENGTH_LONG).show();
                } else {
                    jianYanshuju();
                }
                break;
        }
    }

    int REQUESTCODE_FROM_ACTIVITY = 1000;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE_FROM_ACTIVITY) {
                //List<String> list = data.getStringArrayListExtra(Constant.RESULT_INFO);//Constant.RESULT_INFO == "paths"
                List<String> list = data.getStringArrayListExtra("paths");
                if (list.size() == 1) {
                    mRl_fujian.setVisibility(View.VISIBLE);
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
                            mRl_fujian.setVisibility(View.GONE);
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
                if (response != null && response.get() != null && response.get().getData() != null) {
                    Toast.makeText(BanwenActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
//                    mBtnCancel.setVisibility(View.VISIBLE);
                    mbtnUplaod.setVisibility(View.GONE);
                    mbtnUplaod.setEnabled(false);
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
//            Toast.makeText(BanwenActivity.this, "ok", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(int what, Exception exception) {

        }
    };


    private void downLoadData() {

        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) { // 在android 6.0之前会默认返回true

                            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) // 判断是否可以对SDcard进行操作
                            {    // 获取SDCard指定目录下
                                String sdCardDir = Environment.getExternalStorageDirectory() + "/yc/";
                                File dirFile = new File(sdCardDir);  //目录转化成文件夹
                                if (!dirFile.exists()) {              //如果不存在，那就建立这个文件夹
                                    dirFile.mkdirs();
                                    Log.w("2333", "dirFile.mkdirs():" + dirFile.mkdirs());
                                }  //文件夹有啦，就可以保存图片啦
                                Log.w("2333", "进来" + "sdCardDir=" + sdCardDir);
                                DownloadQueue downloadQueue = NoHttp.newDownloadQueue();
                                //下载文件
                                DownloadRequest downloadRequest = NoHttp.createDownloadRequest("http://192.168.1.150:8089/uploadFileController/downloadFile.do",
                                        RequestMethod.POST, sdCardDir, "big_pic", true, false);
                                downloadRequest.add("filePath", "1/form/20170718/2a8406fe-8830-4609-87b0-4ce876ce9b56");
                                downloadQueue.add(0, downloadRequest, new DownloadListener() {
                                    @Override
                                    public void onDownloadError(int what, Exception exception) {
                                        Log.w("2333", "onDownloadError" + exception.toString());
                                    }

                                    @Override
                                    public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long allCount) {
                                        Log.w("2333", "onStart");
                                    }

                                    @Override
                                    public void onProgress(int what, int progress, long fileCount, long speed) {
                                        Log.w("2333", "progress=" + progress);
                                    }

                                    @Override
                                    public void onFinish(int what, String filePath) {
                                        Log.w("2333", "onFinish" + filePath);
                                    }

                                    @Override
                                    public void onCancel(int what) {

                                    }
                                });
                            }
                        } else {
                            // 未获取权限
                            Toast.makeText(BanwenActivity.this, "您没有授权该权限，请在设置中打开授权", Toast.LENGTH_SHORT).show();
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


    private void jianYanshuju() {
        if (TextUtils.isEmpty(mBianhao.getText().toString().trim())) {
            Toast.makeText(this, "请点击获取编号", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mDanweiDanwei.getText().toString().trim())) {
            Toast.makeText(this, "请填写来文单位", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mTitle.getText().toString().trim())) {
            Toast.makeText(this, "请填写文章标题", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mBeizhu.getText().toString().trim())) {
            Toast.makeText(this, "请填写备注信息", Toast.LENGTH_SHORT).show();
        } else {
            RequestServerCommit(mDanweiDanwei.getText().toString().trim(),
                    mTitle.getText().toString().trim(),
                    mBeizhu.getText().toString().trim(),
                    mBianhao.getText().toString().trim(),
                    mFilePathReturn
            );
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
                if (response != null && response.get() != null && response.get().getData() != null) {
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
                if (mLoadingDialog != null) {
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
                if (response != null && response.get() != null) {
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
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }
}
