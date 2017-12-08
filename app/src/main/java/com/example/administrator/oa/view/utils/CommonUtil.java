package com.example.administrator.oa.view.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;

import com.yanzhenjie.nohttp.OnUploadListener;

import java.io.File;
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

}
