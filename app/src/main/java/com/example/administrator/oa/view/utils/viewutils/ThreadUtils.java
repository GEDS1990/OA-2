package com.example.administrator.oa.view.utils.viewutils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lsh on 2016/11/8.
 */
public class ThreadUtils {

    /**
     * 在子线程中执行任务
     */
    //使用线程池来维护线程的重用
    private static ExecutorService threadPool = Executors.newCachedThreadPool();

    public static void runInThread(Runnable task) {
        //执行任务
        threadPool.execute(task);
    }

    public static void runDelayInThread(Runnable task) {
        //执行任务
        threadPool.execute(task);
    }

    /**
     * 创建handler
     * 参数：Looper.getMainLooper()主线程中的任务队列
     */
    private static Handler handler = new Handler(Looper.getMainLooper());

    public static Handler getHandler() {
        return handler;
    }

    /**
     * 在主线程中执行任务
     */
    public static void runOnUiThread(Runnable task) {
        handler.post(task);
    }

    /**
     * 在主线程中执行toast操作
     */
    public static void showToast(final Context context, final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
