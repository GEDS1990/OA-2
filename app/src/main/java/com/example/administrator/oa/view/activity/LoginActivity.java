package com.example.administrator.oa.view.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.bean.LoginResponse;
import com.example.administrator.oa.view.constance.UrlConstance;
import com.example.administrator.oa.view.net.JavaBeanRequest;
import com.example.administrator.oa.view.utils.DeviceUtils;
import com.example.administrator.oa.view.utils.SPUtils;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by Administrator on 2017/3/21.
 */

public class LoginActivity extends HeadBaseActivity {

    @BindView(R.id.username)
    EditText mUsername;
    @BindView(R.id.user_phonenum)
    EditText mUserPhonenum;
    @BindView(R.id.btn_login)
    Button mBtnLogin;
//    @BindView(R.id.set_IP)
//    EditText mSetIP;
    private RxPermissions mRxPermissions;

    @Override
    protected int getChildLayoutRes() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        return R.layout.activity_login;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        headView.setVisibility(View.GONE);
        statubar.setVisibility(View.GONE);
        String userName = SPUtils.getString(this, "userName");
        if (!TextUtils.isEmpty(userName)) {
            readyGoThenKill(MainActivity.class);
        }
    }

    @OnClick(R.id.btn_login)
    public void onClick() {
        //手机号验证
        mRxPermissions = new RxPermissions(this);
        mRxPermissions.request(Manifest.permission.READ_PHONE_STATE)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) { // 在android 6.0之前会默认返回true

                            LoglinJiaoYan();
                        } else {
                            Toast.makeText(LoginActivity.this, "权限获取失败，请到设置中开启", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        //登录请求数据
        //readyGoThenKill(MainActivity.class);
    }

    /**
     * 登录校验
     */
    private void LoglinJiaoYan() {

//        String ip = mSetIP.getText().toString().trim();
//        if (!TextUtils.isEmpty(ip) && mSetIp != null) {
//            mSetIp = "http://"+ip;
//        } else {
//            Toast.makeText(this, "请设置IP", Toast.LENGTH_SHORT).show();
//            return;
//        }

        //设备id
        String deviceId = DeviceUtils.getDeviceId(this);
        long currentTime = System.currentTimeMillis();
        //设备品牌
        String phoneBrand = DeviceUtils.getPhoneBrand();
        //设备安卓版本号
        String buildVersion = DeviceUtils.getBuildVersion();
        //测试使用

        //用户名
        String phoneNum = mUsername.getText().toString().trim();
        //String phoneNum = "lingo";
        //密码
        String pwd = mUserPhonenum.getText().toString().trim();
        //String pwd = "1";

        //String okpwdMD5 = "";
        if (TextUtils.isEmpty(phoneNum) || TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, "手机号和密码不能为空", Toast.LENGTH_SHORT).show();
        } else {
            //okpwdMD5 = CommonUtil2.MD5(pwd);//对密码继续md5加密
            //执行联网请求
            RequestServerLogin2(phoneNum, pwd, deviceId, buildVersion, phoneBrand);
            //readyGoThenKill(MainActivity.class);
        }
    }


    /**
     * 请求网络接口
     */
    private void RequestServerLogin2(String phoneNum, String okpwdMD5, String deviceId, String buildVersion, String phoneBrand) {

        RequestQueue Queue = NoHttp.newRequestQueue();
        //创建请求
        Request<LoginResponse> request = new JavaBeanRequest<>(UrlConstance.URL_LOGIN,RequestMethod.POST, LoginResponse.class);
        //添加url?key=value形式的参数
        request.add("username", phoneNum);
        request.add("password", okpwdMD5);
        request.add("code", deviceId);
        request.add("type", "android" + buildVersion);
        request.add("name", phoneBrand);

        Queue.add(1, request, new OnResponseListener<LoginResponse>() {
            @Override
            public void onStart(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSucceed(int what, Response<LoginResponse> response) {
                if (null != response && null != response.get() && null != response.get().getData()) {
                    Log.w("6626", response.get().getData().toString());
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    SPUtils.put(LoginActivity.this, "sessionId", response.get().getData().getSessionId());
                    SPUtils.put(LoginActivity.this, "userName", response.get().getData().getUserName());
                    SPUtils.put(LoginActivity.this, "departmentId", response.get().getData().getDepartmentId() + "");
                    SPUtils.put(LoginActivity.this, "userId", response.get().getData().getUserId());
                    if(TextUtils.isEmpty(response.get().getData().getDepartmentName())) {
                        SPUtils.put(LoginActivity.this, "departmentName", "无");
                    } else {
                        SPUtils.put(LoginActivity.this, "departmentName", response.get().getData().getDepartmentName());
                    }
                    SPUtils.put(LoginActivity.this, "departments", response.get().getData().getDepartments());
                    SPUtils.put(LoginActivity.this, "userType", response.get().getData().getUserType());
                    SPUtils.put(LoginActivity.this, "email", response.get().getData().getEmail());
                    SPUtils.put(LoginActivity.this, "mobile", response.get().getData().getMobile());
                    SPUtils.put(LoginActivity.this, "loginName", mUsername.getText().toString().trim());
                    readyGoThenKill(MainActivity.class);
                } else {
                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(int what, Response<LoginResponse> response) {
                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(int what) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        });

//        StartRequest(0, request, new HttpListener<LoginResponse>() {
//            @Override
//            public void onSucceed(int what, Response<LoginResponse> response) {
//                if (response != null && response.get() != null && response.get().getData() != null) {
//                    Log.w("6626", response.get().getData().toString());
//                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
//                    SPUtils.put(LoginActivity.this, "sessionId", response.get().getData().getSessionId());
//                    SPUtils.put(LoginActivity.this, "userName", response.get().getData().getUserName());
//                    SPUtils.put(LoginActivity.this, "departmentId", response.get().getData().getDepartmentId() + "");
//                    SPUtils.put(LoginActivity.this, "userId", response.get().getData().getUserId());
//                    SPUtils.put(LoginActivity.this, "departmentName", response.get().getData().getDepartmentName());
//                    SPUtils.put(LoginActivity.this, "departments", response.get().getData().getDepartments());
//                    SPUtils.put(LoginActivity.this, "userType", response.get().getData().getUserType());
//                    readyGoThenKill(MainActivity.class);
//                }
//            }
//
//            @Override
//            public void onFailed(int what, Response<LoginResponse> response) {
//                Toast.makeText(LoginActivity.this, "登录失败" + response, Toast.LENGTH_SHORT).show();
//            }
//        }, true, true);


    }
}
