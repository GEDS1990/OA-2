package com.example.administrator.oa.view.activity;

import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.example.administrator.oa.view.utils.SPUtils;
import com.lsh.XXRecyclerview.CommonRecyclerAdapter;
import com.lsh.XXRecyclerview.CommonViewHolder;
import com.lsh.XXRecyclerview.XXRecycleView;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.NoHttp;
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
 * Created by Administrator on 2017/8/7.
 */

public class BaoXiaoApplyActivity_shenhe extends HeadBaseActivity {
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.bumen)
    TextView mBumen;
    @BindView(R.id.start)
    TextView mStart;
    @BindView(R.id.ll_start)
    LinearLayout mLlStart;
    @BindView(R.id.money_rmb)
    TextView mMoneyRmb;
    @BindView(R.id.money_num)
    TextView mMoneyNum;
    @BindView(R.id.baoxiao_yuanyin)
    TextView mBaoxiaoYuanyin;
    @BindView(R.id.add_fujian)
    RelativeLayout mAddFujian;
    @BindView(R.id.rl_fujian)
    RelativeLayout mRlFujian;
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
    @BindView(R.id.xxre)
    XXRecycleView mXxre;
    @BindView(R.id.shenheyijian)
    TextView mShenheyijian;
    @BindView(R.id.ll_shenheyijian)
    LinearLayout mLlShenheyijian;
    @BindView(R.id.btn_caogao)
    Button mBtnCaogao;
    @BindView(R.id.btn_commit)
    Button mBtnCommit;
    private String mTaskId;
    private String mProcessTaskType;
    private String mUserType;
    private String mDepartmentName;
    private String mDepartmentId;
    private String mSessionId;
    private List<ProcessShenheHistoryBean> datas = new ArrayList<>();
    private CommonRecyclerAdapter<ProcessShenheHistoryBean> mAdapter;
    // 附件
    private String mFilePathReturn = "";
    private String mFileNameReturn = "";
    // 文件总大小
    private long fileSize;
    // 已下载的大小
    private long downLoadFileSize;

    @Override
    protected int getChildLayoutRes() {
        return R.layout.acitivity_baoxiao_shenhe;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("报销审核单");
        initThisView();
    }

    private void initThisView() {
        mTaskId = getIntent().getStringExtra("taskId");
        mProcessTaskType = getIntent().getStringExtra("processTaskType");
        mUserType = SPUtils.getString(this, "userType");
        mDepartmentName = SPUtils.getString(this, "departmentName");
        mDepartmentId = SPUtils.getString(this, "departmentId");
        Log.w("6666", mProcessTaskType + "/,mUserType=" + mUserType);
        mSessionId = SPUtils.getString(this, "sessionId");

        //获取服务器数据，填充表单数据
        RequestServer();
        //流程记录的view
        mXxre.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CommonRecyclerAdapter<ProcessShenheHistoryBean>(this, datas, R.layout.item_myprocess_shenhejilu) {
            @Override
            public void convert(CommonViewHolder holder, ProcessShenheHistoryBean item, int i, boolean b) {
                holder.setText(R.id.processNameContent, item.getName());
                holder.setText(R.id.name, item.getAssignee());
                holder.setText(R.id.startTimeContent, item.getCreateTime());
                holder.setText(R.id.completeTimeContent, item.getCompleteTime());
            }
        };
        mXxre.setAdapter(mAdapter);
    }

    @OnClick({ R.id.btn_uplaod, R.id.btn_caogao, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_uplaod:
                mBtnUplaod.setVisibility(View.GONE);
                mBtnUplaod.setEnabled(false);
                RequestServerDownLoad();
                break;
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
                Toast.makeText(BaoXiaoApplyActivity_shenhe.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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
                        if(!TextUtils.isEmpty(bean.getFormName()) && !TextUtils.isEmpty(bean.getFormCode())) {
                            Log.d("FormName", bean.getFormName());
                            Log.d("FormCode", bean.getFormCode());
                            switch (bean.getFormCode()) {
                                // 报销流程审核单
                                case "money-comment":
                                    mBtnCaogao.setText("不同意");
                                    mBtnCommit.setText("同意");
                                    break;
                                // 报销流程通知
                                case "money-notice":
                                    mLlShenheyijian.setVisibility(View.VISIBLE);
                                    mShenheyijian.setFocusable(false);
                                    mBtnCaogao.setVisibility(View.GONE);
                                    mBtnCommit.setText("完成");
                                    break;
                            }
                        }
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
                                case "comment":
                                    mShenheyijian.setText(value);
                                    break;
                                case "enclosure":
                                    if (!TextUtils.isEmpty(bean.getLabel())) {
                                        // 实时请求权限
                                        CommonUtil.verifyStoragePermissions(BaoXiaoApplyActivity_shenhe.this);
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
                Toast.makeText(BaoXiaoApplyActivity_shenhe.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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
            return true;
        } else {
            // 将文件显示在界面
            getFileInfo(filePath);
            mFilesize.setText(ShowLongFileSzie(fileSize));
            mPb.setProgress(0);
            mBtnUplaod.setVisibility(View.VISIBLE);
            mBtnUplaod.setEnabled(true);
            mBtnUplaod.setTag("down");
            return false;
        }
    }

    /**
     * 界面显示附件信息
     * @param filePath
     */
    private void getFileInfo(String filePath){
        mRlFujian.setVisibility(View.VISIBLE);
        String[] strings = filePath.split("/");
        int count = strings.length;
        String mFilename = strings[count - 1];
        mFileName.setText(mFilename);
        File file = new File(filePath);
        mFilesize.setText(ShowLongFileSzie(file.length()));
        if (mFilename.contains(".")) {
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
                Toast.makeText(BaoXiaoApplyActivity_shenhe.this, "附件下载失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long allCount) {
//                fileSize = allCount;
//                Toast.makeText(BaoXiaoApplyActivity_shenhe.this, "开始下载附件", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(BaoXiaoApplyActivity_shenhe.this, "附件下载完成", Toast.LENGTH_SHORT).show();
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
     * 提交页面数据，完成当前审核
     */
    private void RequestServerCommit(String comment) {
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
                        Toast.makeText(BaoXiaoApplyActivity_shenhe.this, "流程审核成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(BaoXiaoApplyActivity_shenhe.this, "流程审核失败", Toast.LENGTH_SHORT).show();
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
