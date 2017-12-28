package com.example.administrator.oa.view.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Method;
import java.security.MessageDigest;

/**
 * Created by Administrator on 2017/6/27.
 */

public class CommonUtil {

    /**
     * MD5加密工具类
     * <功能详细描述>
     *
     * @author chenlujun
     * @version [版本号, 2014年10月1日]
     * @see [相关类/方法]
     * @since [产品/模块版本]
     */

    public final static String MD5(String pwd) {
        //用于加密的字符
        char md5String[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            //使用平台的默认字符集将此 String 编码为 byte序列，并将结果存储到一个新的 byte数组中
            byte[] btInput = pwd.getBytes();

            //信息摘要是安全的单向哈希函数，它接收任意大小的数据，并输出固定长度的哈希值。
            MessageDigest mdInst = MessageDigest.getInstance("MD5");

            //MessageDigest对象通过使用 update方法处理数据， 使用指定的byte数组更新摘要
            mdInst.update(btInput);

            // 摘要更新之后，通过调用digest（）执行哈希计算，获得密文
            byte[] md = mdInst.digest();

            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {   //  i = 0
                byte byte0 = md[i];  //95
                str[k++] = md5String[byte0 >>> 4 & 0xf];    //    5
                str[k++] = md5String[byte0 & 0xf];   //   F
            }
            //返回经过加密后的字符串
            return new String(str);

        } catch (Exception e) {
            return null;
        }
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };

    /**
     * 实时请求存储读写权限
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to
     * grant permissions
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // 在API23+以上也就是安卓6.0以上的，进行了权限管理， 不止要在AndroidManifest.xml里面添加权限
        // <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
        // <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
        // 还要在JAVA代码中运行时实时请求权限：
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    /**
     * 实时请求存储读写权限
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to
     * grant permissions
     * @param activity
     */
    private static String[] PERMISSIONS_STATUSBAR = {
            Manifest.permission.EXPAND_STATUS_BAR};
    public static void verifyStatusBarPermissions(Context context) {
        // 在API23+以上也就是安卓6.0以上的，进行了权限管理， 不止要在AndroidManifest.xml里面添加权限
        // <uses-permission android:name="android.permission.EXPAND_STATUS_BAR"/>
        // 还要在JAVA代码中运行时实时请求权限：
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.EXPAND_STATUS_BAR);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions((Activity) context, PERMISSIONS_STATUSBAR,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    /**
     * 创建OA的文件夹
     */
    public static String createRootFile(Context context){
        String rootPath = getSDPath(context) + File.separator + "NJSPOA"
                + File.separator;
        File fileRoot = new File(rootPath);
        try {
            if (!fileRoot.exists()) {
                if (fileRoot.mkdirs()) {
                    return rootPath;
                } else {
                    return null;
                }
            } else {
                return rootPath;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取SD卡的根目录
     * @param context
     * @return
     */
    public static String getSDPath(Context context) {
        File sdDir = null;
        // 判断sd卡是否存在
        boolean sdCardExist = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        // 如果SD卡存在，则获取跟目录
        if (sdCardExist){
            // 获取根目录
            sdDir = Environment.getExternalStorageDirectory();
            System.out.println("ExternalStorageDirectory " + sdDir);
        }
        else {
            sdDir = context.getFilesDir();
            System.out.println("FilesDir " + sdDir);
        }
        return sdDir.toString();
    }

    /**
     * 显示toast
     * @param content
     *            显示的内容
     */
    private static Toast mToast;
    public static void showToast(Context mContext, String content) {
//        if (null != mContext) {
//            Toast.makeText(mContext, content, Toast.LENGTH_LONG).show();
//        }
        if(null == mToast) {
            mToast = Toast.makeText(mContext, content, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(content);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    /**
     * sd卡中创建一个目标文件
     *
     * @param name
     * @return
     */
    public static File createUpdateDir(String name) {
        File sdcardDir = Environment.getExternalStorageDirectory();
        String path = sdcardDir.getPath() + "/NJSPOA_UPDATE";
        File file = null;
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment
                    .getExternalStorageState())) {

                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                file = new File(dir + File.separator + name);
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
            }
        } catch (Exception e) {
        }
        if (null != file) {
            return file;
        } else {
            return null;
        }
    }

    /**
     * 安装apk
     * @param apkFile
     * @param context
     */
    public static void installApk(Context context, File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 7.0需要在manifest里注册provider开放权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, "com.example.administrator.oa.fileprovider", apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        if (context.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
            context.startActivity(intent);
        }
    }

    /**
     * 收缩通知栏
     * @param context
     */
    public static void collapsingNotification(Context context) {
        // 申请通知栏的权限，没多大用处
//        verifyStatusBarPermissions(context);
        Object service = context.getSystemService("statusbar");
        if (null == service)
            return;
        try {
            Class<?> clazz = Class.forName("android.app.StatusBarManager");
            int sdkVersion = android.os.Build.VERSION.SDK_INT;
            Method collapse = null;
            if (sdkVersion <= 16) {
                collapse = clazz.getMethod("collapse");
            } else {
                collapse = clazz.getMethod("collapsePanels");
            }

            collapse.setAccessible(true);
            collapse.invoke(service);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 展开通知栏
     * @param context
     */
    public static void expandNotification(Context context) {
        Object service = context.getSystemService("statusbar");
        if (null == service)
            return;
        try {
            Class<?> clazz = Class.forName("android.app.StatusBarManager");
            int sdkVersion = android.os.Build.VERSION.SDK_INT;
            Method expand = null;
            if (sdkVersion <= 16) {
                expand = clazz.getMethod("expand");
            } else {
          /*
           * Android SDK 16之后的版本展开通知栏有两个接口可以处理
           * expandNotificationsPanel()
           * expandSettingsPanel()
           */
                //expand =clazz.getMethod("expandNotificationsPanel");
                expand = clazz.getMethod("expandSettingsPanel");
            }

            expand.setAccessible(true);
            expand.invoke(service);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
