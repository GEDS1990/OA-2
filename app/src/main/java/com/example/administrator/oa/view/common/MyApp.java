package com.example.administrator.oa.view.common;

import android.app.Application;

import com.thin.downloadmanager.ThinDownloadManager;
import com.yanzhenjie.nohttp.NoHttp;

/**
 * Created by Administrator on 2017/6/27.
 */

public class MyApp extends Application {
    private static MyApp application;
    public String mainAction = "com.example.administrator.oa.view.activity.MainActivity.broadcast";
    /**
     * 下载管理
     */
    private volatile ThinDownloadManager thinDownloadManager;
    /**
     * 下载id
     */
    public static int downloadId;

    /**
     * 下载线程数
     */
    private int DOWNLOAD_THREAD_POOL_SIZE = 3;
    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        NoHttp.initialize(this, new NoHttp.Config()
                .setConnectTimeout(30 * 1000) // 全局连接超时时间，单位毫秒。
                .setReadTimeout(30 * 1000) // 全局服务器响应超时时间，单位毫秒。
        );
    }

    /**
     * 获取下载管理
     *
     * @return
     */
    public ThinDownloadManager getThinDownloadManager() {
        if (thinDownloadManager == null) {
            synchronized (this) {
                if (thinDownloadManager == null) {
                    thinDownloadManager = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE);
                }
            }
        }
        return thinDownloadManager;
    }
}
