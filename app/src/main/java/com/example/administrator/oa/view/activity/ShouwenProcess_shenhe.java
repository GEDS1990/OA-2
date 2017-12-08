package com.example.administrator.oa.view.activity;

import android.os.Environment;
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
import com.example.administrator.oa.view.bean.ProcessJieguoResponse;
import com.example.administrator.oa.view.bean.ProcessShenheHistoryBean;
import com.example.administrator.oa.view.bean.ProcessShenheHistoryRes;
import com.example.administrator.oa.view.bean.QingjiaShenheBean;
import com.example.administrator.oa.view.bean.QingjiaShenheResponse;
import com.example.administrator.oa.view.bean.ZuzhiUserBean;
import com.example.administrator.oa.view.bean.ZuzhiUserListResponse;
import com.example.administrator.oa.view.bean.organization_structure.ChildrenBean;
import com.example.administrator.oa.view.bean.organization_structure.OrganizationResponse;
import com.example.administrator.oa.view.constance.UrlConstance;
import com.example.administrator.oa.view.net.JavaBeanRequest;
import com.example.administrator.oa.view.utils.CommonUtil;
import com.example.administrator.oa.view.utils.SPUtils;
import com.lsh.XXRecyclerview.CommonRecyclerAdapter;
import com.lsh.XXRecyclerview.CommonViewHolder;
import com.lsh.XXRecyclerview.XXRecycleView;
import com.luoshihai.xxdialog.DialogViewHolder;
import com.luoshihai.xxdialog.XXDialog;
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
 * Created by Administrator on 2017/8/29.
 */

public class ShouwenProcess_shenhe extends HeadBaseActivity {

    @BindView(R.id.textView3)
    TextView mTextView3;
    @BindView(R.id.danwei)
    TextView mDanwei;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.bianhao)
    TextView mBianhao;
    @BindView(R.id.beizhu)
    TextView mBeizhu;
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
    @BindView(R.id.tv_huiqian)
    TextView mTvHuiqian;
    @BindView(R.id.ll_huiqianren)
    LinearLayout mLlHuiqianren;
    @BindView(R.id.xxre_huiqianren)
    XXRecycleView mXxreHuiqianren;
    @BindView(R.id.huiqianyijian)
    EditText mHuiqianyijian;
    @BindView(R.id.ll_huiqianyijian)
    LinearLayout mLlHuiqianyijian;
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
    private String mSessionId;
    private CommonRecyclerAdapter<ProcessShenheHistoryBean> mAdapter;
    private List<ProcessShenheHistoryBean> datas = new ArrayList<>();
    private CommonRecyclerAdapter<ZuzhiUserBean> mHuiqianAdapter;
    private List<ZuzhiUserBean> datas2 = new ArrayList<>();

    // 附件
    private String mFilePathReturn = "";
    private String mFileNameReturn = "";
    // 文件总大小
    private long fileSize;
    // 已下载的大小
    private long downLoadFileSize;

    @Override
    protected int getChildLayoutRes() {
        return R.layout.activity_shouwen_shenhe;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("收文流程审核");
        initThisView();
    }

    private void initThisView() {
        mTaskId = getIntent().getStringExtra("taskId");
        mProcessTaskType = getIntent().getStringExtra("processTaskType");
        mUserType = SPUtils.getString(this, "userType");
        Log.w("6666", mProcessTaskType + "/,mUserType=" + mUserType);
        mSessionId = SPUtils.getString(this, "sessionId");

        //获取服务器数据，填充表单数据
        RequestServer();
        //判断是否是发起会签节点
        if ("vote".equals(mProcessTaskType)) {
            mBtnCaogao.setText("退回发起人");
            mLlHuiqianyijian.setVisibility(View.VISIBLE);
        } else {
            mBtnCaogao.setText("不同意");
            mLlHuiqianyijian.setVisibility(View.GONE);
        }
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
    }

    @OnClick({R.id.btn_uplaod, R.id.ll_huiqianren, R.id.btn_caogao, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_uplaod:
                mBtnUplaod.setVisibility(View.GONE);
                mBtnUplaod.setEnabled(false);
                RequestServerDownLoad();
                break;
            case R.id.ll_huiqianren:
//                RequestServerGetZuzhi("选择会签人");
                if("0".equals(mLlHuiqianren.getTag())) {
                    mLlHuiqianren.setTag("1");
                    RequestServerGetZuzhi(mLlHuiqianren, mTvHuiqian, "请选择会签人", mHuiqianAdapter);
                }
                break;
            case R.id.btn_caogao:
                switch (mBtnCaogao.getText().toString()) {
                    case "不同意":
                        RequestServerCommit("不同意");
                        break;
                    case "退回发起人":
                        RequestServerTuihui();
                        break;
                }
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
                if (null != response && null != response.get() && null != response.get().getData()) {
                    List<ProcessShenheHistoryBean> data = response.get().getData();
                    if (mAdapter != null) {
                        mAdapter.replaceAll(data);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessShenheHistoryRes> response) {
                Toast.makeText(ShouwenProcess_shenhe.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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
                                // 投资协议会签审核
                                case "collect-comment":
//                                    mLlNiban.setVisibility(View.VISIBLE);
//                                    mBtnCaogao.setText("不同意");
//                                    mBtnCommit.setText("同意");
                                    break;
                                // 投资协议会签
                                case "collect-leader":
//                                    mBtnCaogao.setText("不同意");
//                                    mBtnCommit.setText("同意");
                                    break;
                                // 投资协议通知
                                case "collect-department":
                                    break;
                                // 投资协议通知
                                case "collect-notice":
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
                                    if (TextUtils.isEmpty(bean.getLabel())) {
                                        return;
                                    }
                                    String[] ids = value.split(",");
                                    String[] names = bean.getLabel().split(",");
                                    for (int i=0; i<ids.length;i++) {
                                        mHuiqianAdapter.add(new ZuzhiUserBean(ids[i], names[i]));
                                    }
                                    break;
                                case "comment":
                                    mShenheyijian.setText(value);
                                    break;
//                                case "comment":
//                                    mZhaunfa.setText(value);
//                                    break;
                                // 附件
                                case "enclosure":
                                    if(!TextUtils.isEmpty(bean.getLabel())) {
                                        // 实时请求权限
                                        CommonUtil.verifyStoragePermissions(ShouwenProcess_shenhe.this);
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
                        if ("userpicker".equals(bean.getType())) {
                            mLlHuiqianren.setVisibility(View.VISIBLE);
                            mXxreHuiqianren.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<QingjiaShenheResponse> response) {
                Toast.makeText(ShouwenProcess_shenhe.this, "请求数据失败", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ShouwenProcess_shenhe.this, "附件下载失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long allCount) {
//                fileSize = allCount;
//                Toast.makeText(Touzixieyi_shenhe.this, "开始下载附件", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ShouwenProcess_shenhe.this, "附件下载完成", Toast.LENGTH_SHORT).show();
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
        String danwei = mDanwei.getText().toString();
        String bianhao = mBianhao.getText().toString();
        String title = mTitle.getText().toString();
        String name = mName.getText().toString();
        String beizhu = mBeizhu.getText().toString();


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
                .append("\"departments\":" + "\"" + danwei + "\",")
                .append("\"title\":" + "\"" + title + "\",")
                .append("\"id\":" + "\"" + bianhao + "\",")
                .append("\"petition\":" + "\"" + name + "\",")
                .append("\"remarks\":" + "\"" + beizhu + "\",")
                .append("\"leader\":" + "\"" + leadersID.toString() + "\",")
                .append("\"leader_name\":" + "\"" + leadersName.toString() + "\",")
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
                        Toast.makeText(ShouwenProcess_shenhe.this, "流程审核成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                Toast.makeText(ShouwenProcess_shenhe.this, "流程审核失败", Toast.LENGTH_SHORT).show();
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
     * 退回发起人
     */
    private void RequestServerTuihui() {
        String yijian = mHuiqianyijian.getText().toString().trim();
        if (!TextUtils.isEmpty(yijian)) {
            //创建请求队列
            RequestQueue Queue = NoHttp.newRequestQueue();
            //创建请求
            Request<ProcessJieguoResponse> request = new JavaBeanRequest<>(UrlConstance.URL_HUIQIAN_TUIHUI,
                    RequestMethod.POST, ProcessJieguoResponse.class);
            //添加url?key=value形式的参数
            request.add("taskId", mTaskId);
            request.add("comment", yijian);
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
                            Toast.makeText(ShouwenProcess_shenhe.this, "退回成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }

                @Override
                public void onFailed(int what, Response<ProcessJieguoResponse> response) {
                    Toast.makeText(ShouwenProcess_shenhe.this, "流程审核失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFinish(int what) {
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                }
            });
        } else {
            Toast.makeText(this, "请填写会签处理意见", Toast.LENGTH_SHORT).show();
        }
    }
}
