package com.example.administrator.oa.view.update;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.utils.CommonUtil;


/**
 * 下载通知定制
 */
public class DownloadNotificationUtil {

    NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;
    String urlPath;

    int progress;
    final int NOTIFYCATIONID = 1001;
    Context context;

    public DownloadNotificationUtil(String urlPath, Context context) {
        this.urlPath = urlPath;
        this.context = context;
        initNotifycation();
    }

    public DownloadNotificationUtil(Context context) {
        this.context = context;
        initNotifycation();
    }

    /**
     * 初始化一个builder
     */
    private void initNotifycation() {
        mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setWhen(System.currentTimeMillis()).setSmallIcon(
                R.drawable.logo_launch);
    }

    /**
     * 首次展示通知栏
     */
    public void showProgressNotify() {
        // 展开通知栏
        CommonUtil.expandNotification(context);
        // 通知首次出现在通知栏，带上升动画效果的,在小米上有问题
        mBuilder.setContentTitle("南京研创园OA软件更新").setContentText("等待下载")
                .setTicker("开始下载");
        Notification mNotification = mBuilder.build();
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;
        // 确定进度的
        mBuilder.setProgress(100, progress, false); // 这个方法是显示进度条 设置为true就是不确定的那种进度条效果
        mNotificationManager.notify(NOTIFYCATIONID, mNotification);
    }

    /**
     * 取消展示通知栏
     */
    public void cancleProgressNotify() {
        Notification mNotification = mBuilder.build();
        mNotificationManager.cancel(NOTIFYCATIONID);// 删除一个特定的通知ID对应的通知
        mNotificationManager.cancelAll();
        // 展开通知栏
        CommonUtil.collapsingNotification(context);
    }

    int oldProgress = 0;

    /**
     * 设置下载进度
     *
     * @param progress
     */
    public void updateNotification(int progress) {
        //onProgress方法被回调的很频繁,所以加了此处的处理,防止过度的更新Notification
        if (oldProgress == progress) {
            return;
        }
        oldProgress = progress;
//        防止过度的更新Notification,
        if (progress % 1 == 0) {
            Notification mNotification = mBuilder.build();
            mNotification.flags = Notification.FLAG_ONGOING_EVENT;//
            mBuilder.setProgress(100, progress, false); // 这个方法是显示进度条
            mBuilder.setContentText("已更新: " + progress + "%").setContentTitle("南京研创园OA软件更新");
            if (progress >= 100) {
                mNotificationManager.cancel(NOTIFYCATIONID);// 删除一个特定的通知ID对应的通知
                mNotificationManager.cancelAll();
            } else {
                mNotificationManager.notify(NOTIFYCATIONID, mNotification);
            }
        }

    }

}
