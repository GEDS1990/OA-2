package com.example.administrator.oa.view.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.activity.LoginActivity;
import com.example.administrator.oa.view.constance.UrlConstance;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.download.DownloadQueue;
import com.yanzhenjie.nohttp.download.DownloadRequest;

import java.io.File;
import java.io.IOException;


public class UpdateService extends Service {
    private static String down_url; // = "http://192.168.1.112:8080/360.apk";
    private static final int DOWN_OK = 1; // 下载完成
    private static final int DOWN_ERROR = 0;

    private String app_name = "NJSmartOA";

    private NotificationManager notificationManager;
    private Notification notification;

    private Intent updateIntent;
    private PendingIntent pendingIntent;
    private File updateFile;

    private int notification_id = 0;
    long totalSize = 0;// 文件总大小
    /***
     * 更新UI
     */
    final Handler handler = new Handler() {
        @SuppressWarnings("deprecation")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_OK:
                    // 下载完成，点击安装
                    mBuilder.setContentTitle("下载完成");
                    notification = mBuilder.getNotification();
                    Intent installApkIntent = getFileIntent(UpdateService.this, updateFile);
                    pendingIntent = PendingIntent.getActivity(UpdateService.this, 0, installApkIntent, 0);
                    notification.contentIntent = pendingIntent;
                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                    notificationManager.notify(notification_id, notification);
                    stopService(updateIntent);
                    break;
                default:
                    stopService(updateIntent);
                    break;
            }
        }
    };
    private Notification.Builder mBuilder;
    private ProgressDialog pBar;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            try {
                down_url = UrlConstance.URL_HOST + "/app-debug-unaligned.apk";
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) // 判断是否可以对SDcard进行操作
                {    // 获取SDCard指定目录下
                    File sdCardDir = Environment.getExternalStorageDirectory();
                    File updateFile = new File(sdCardDir,
                            "NJSmartOA.apk");
                    if (!updateFile.exists()) {
                        try {
                            updateFile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        updateFile.delete();
                        updateFile.createNewFile();
                    }
                    // 创建通知
                    createNotification();
                    // 开始下载
                    downloadUpdateFile(down_url, updateFile, sdCardDir.getAbsolutePath());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /***
     * 创建通知栏
     */
    RemoteViews contentView;

    public void createNotification() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//		notification = new Notification();
        mBuilder = new Notification.Builder(this);
//		notification.icon = R.drawable.ic_launcher;
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setTicker("开始下载");
        notification = mBuilder.getNotification();
        // notificationManager.notify(notification_id, notification);
//        notificationManager.notify(notification_id, notification);
        /***
         * 在这里我们用自定的view来显示Notification
         */

//		View view = LayoutInflater.from(this).inflate(R.layout.notification_item, null);
        contentView = new RemoteViews(getPackageName(), R.layout.notification_item);
        contentView.setTextViewText(R.id.notificationTitle, "正在下载");
        contentView.setTextViewText(R.id.notificationPercent, "0%");
        contentView.setProgressBar(R.id.notificationProgress, 100, 0, false);

        notification.contentView = contentView;

        updateIntent = new Intent(this, LoginActivity.class);
        updateIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);

        notification.contentIntent = pendingIntent;
        notificationManager.notify(notification_id, notification);

    }

    /***
     * 下载文件
     */
    private void downloadUpdateFile(final String down_url, final File updateFile, String folderpath) {
        DownloadQueue downloadQueue = NoHttp.newDownloadQueue();
        //下载文件
        DownloadRequest downloadRequest = NoHttp.createDownloadRequest(down_url,
                RequestMethod.POST, folderpath, "NJSmartOA.apk", true, false);
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
                contentView.setProgressBar(R.id.notificationProgress, 100, progress, false);
                notificationManager.notify(notification_id, notification);
            }

            @Override
            public void onFinish(int what, String filePath) {
                Log.w("2333", "onFinish" + filePath);
                Message message = handler.obtainMessage();
                message.what = DOWN_OK;
                handler.sendMessage(message);
                update(UpdateService.this, updateFile);
            }

            @Override
            public void onCancel(int what) {

            }
        });
    }

    public static Intent getFileIntent(Context context, File file) {

        Intent intent = new Intent("android.intent.action.VIEW");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri uriForFile = FileProvider.getUriForFile(context, "com.example.administrator.oa.fileprovider", file);
            String type = getMIMEType(file);
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uriForFile, type);
        } else {
            Uri uri = Uri.fromFile(file);
            String type = getMIMEType(file);
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uri, type);
        }
        return intent;
    }

    public static String getMIMEType(File f) {
        String type = "";
        String fName = f.getName();
        // 取得扩展名
        String end = fName
                .substring(fName.lastIndexOf(".") + 1, fName.length());
        if (end.equals("apk")) {
            type = "application/vnd.android.package-archive";
        } else {
            // /*如果无法直接打开，就跳出软件列表给用户选择 */
            type = "*/*";
        }
        return type;
    }

    /**
     * 安装 apk 文件
     *
     * @param apkFile
     */
    public static void update(Context context, File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, "com.example.administrator.oa.fileprovider", apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (context.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
            context.startActivity(intent);
        }
    }
}