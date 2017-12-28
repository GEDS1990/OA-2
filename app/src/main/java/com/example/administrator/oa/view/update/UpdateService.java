package com.example.administrator.oa.view.update;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.common.MyApp;
import com.example.administrator.oa.view.utils.CommonUtil;
import com.thin.downloadmanager.DownloadManager;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.File;


/**
 * created by dreamcoder on 16/4/26 19:19
 *
 * 更新服务
 */
public class UpdateService extends IntentService {


	/**
	 * 下载管理
	 */
	private ThinDownloadManager downloadManager;
	private String app_name = "NJSPOA.apk";
	/**
	 * 下载到本地后的文件
	 */
	private File updateFile;

	/**
	 * 服务端apk路径
	 */
	private static String url;

	/**
	 * 下载请求
	 */
	private DownloadRequest request;

	/**
	 * 通知栏管理工具
	 */
	private DownloadNotificationUtil notificationControl;

	/**
	 * Handler
	 */
	private Handler mHandler;

	/**
	 * Context
	 */
	private Context context;

	public UpdateService() {
		super("UpdateService");
	}

	@Override
	public void onCreate() {
		context = this;
		super.onCreate();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		mHandler = new Handler(getMainLooper());
		downloadManager = ((MyApp) getApplicationContext()).getThinDownloadManager();
		//根据downloadId判断是否存在相同的任务
		if (downloadManager.query(MyApp.downloadId) == DownloadManager.STATUS_NOT_FOUND) {
			// 创建文件这里要注意，很容易导致文件解析失败
			updateFile = CommonUtil.createUpdateDir(app_name);
			// 这里是尝试将下载的apk放在app内部的/data下面
//            updateFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), app_name);
			url = intent.getStringExtra("downUrl");
			notificationControl = new DownloadNotificationUtil(updateFile.getPath(), context);
			initDownload();
			((MyApp) getApplication()).downloadId = downloadManager.add(request);
			notificationControl.showProgressNotify();
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					CommonUtil.showToast(context, getResources().getString(R.string.update));
				}
			});
		} else {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					CommonUtil.showToast(context, getResources().getString(R.string.downloading));
				}
			});
		}

	}

	/**
	 * 初始化下载
	 */
	private void initDownload() {
		Uri downloadUri = Uri.parse(url);
		Uri destinationUri = Uri.parse(updateFile.getPath());
		request = new DownloadRequest(downloadUri)
				.setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
				.setStatusListener(new DownloadStatusListenerV1() {
					@Override
					public void onDownloadComplete(DownloadRequest downloadRequest) {
						if (request.getDownloadId() == ((MyApp) getApplication()).downloadId) {
							notificationControl.cancleProgressNotify();
							// 启动安装
							CommonUtil.installApk(context, updateFile);
						}
					}

					@Override
					public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
						if (downloadRequest.getDownloadId() == ((MyApp) getApplication()).downloadId) {
							notificationControl.cancleProgressNotify();
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									CommonUtil.showToast(context, getResources().getString(R.string.download_fail));
								}
							});
						}
					}

					@Override
					public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
						if (request.getDownloadId() == ((MyApp) getApplication()).downloadId) {
							notificationControl.updateNotification(progress);
						}
					}
				});


	}

	@Override
	public void onDestroy() {
		Log.i("XXXX","onDestroy");
		super.onDestroy();
	}
}
