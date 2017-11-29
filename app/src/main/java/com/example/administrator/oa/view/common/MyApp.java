package com.example.administrator.oa.view.common;

import android.app.Application;

import com.yanzhenjie.nohttp.NoHttp;

/**
 * Created by Administrator on 2017/6/27.
 */

public class MyApp extends Application {
    private static MyApp application;
    public String mainAction = "com.example.administrator.oa.view.activity.MainActivity.broadcast";
    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        NoHttp.initialize(this, new NoHttp.Config()
                .setConnectTimeout(30 * 1000) // 全局连接超时时间，单位毫秒。
                .setReadTimeout(30 * 1000) // 全局服务器响应超时时间，单位毫秒。
        );
    }
}
