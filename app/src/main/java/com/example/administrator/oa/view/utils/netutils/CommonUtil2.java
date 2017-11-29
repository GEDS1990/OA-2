package com.example.administrator.oa.view.utils.netutils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;

/**
 * Created by Administrator on 2016/11/10.
 */
public class CommonUtil2 {

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

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * dip 转换成 px
     *
     * @param dip
     * @param context
     * @return
     */
    public static float dip2Dimension(float dip, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, displayMetrics);
    }


    /**
     * 获取图片的方向信息
     *
     * @param filepath
     * @return
     */
    public static int getExifOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;

        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            // MmsLog.e(ISMS_TAG, "getExifOrientation():", ex);
        }

        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                // We only recognize a subset of orientation tag values.
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                    default:
                        break;
                }
            }
        }

        return degree;
    }

//    public static Bitmap getImage(String Url) throws Exception {
//
//        try {
//
//            URL url = new URL(Url);
//
//            String responseCode = url.openConnection().getHeaderField(0);
//
//            if (responseCode.indexOf("200") < 0)
//
//                throw new Exception("图片文件不存在或路径错误，错误代码：" + responseCode);
//
//            return BitmapFactory.decodeStream(url.openStream());
//
//        } catch (IOException e) {
//
//            // TODO Auto-generated catch block
//
//            throw new Exception(e.getMessage());
//
//        }
//
//    }


    public static Drawable loadImageFromNetwork(Context context, String imageUrl) {
        Drawable drawable = null;
        if (imageUrl == null)
            return null;
        String imagePath = "";
        String fileName = "";

        // 获取url中图片的文件名与后缀
        if (imageUrl != null && imageUrl.length() != 0) {
            fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        }

        // 图片在手机本地的存放路径,注意：fileName为空的情况
        imagePath = context.getCacheDir() + "/" + fileName;

        Log.i("test", "imagePath = " + imagePath);
        File file = new File(context.getCacheDir(), fileName);// 保存文件
        Log.i("test", "file.toString()=" + file.toString());
        if (!file.exists() && !file.isDirectory()) {
            try {
                // 可以在这里通过文件名来判断，是否本地有此图片

                FileOutputStream fos = new FileOutputStream(file);
                InputStream is = new URL(imageUrl).openStream();
                int data = is.read();
                while (data != -1) {
                    fos.write(data);
                    data = is.read();
                    ;
                }
                fos.close();
                is.close();
//              drawable = Drawable.createFromStream(
//                      new URL(imageUrl).openStream(), file.toString() ); // (InputStream) new URL(imageUrl).getContent();
                drawable = Drawable.createFromPath(file.toString());
                Log.i("test", "file.exists()不文件存在，网上下载:" + drawable.toString());
            } catch (IOException e) {
                Log.d("test", e.getMessage());
            }
        } else {
            drawable = Drawable.createFromPath(file.toString());
            Log.i("test", "file.exists()文件存在，本地获取");
        }

        if (drawable == null) {
            Log.d("test", "null drawable");
        } else {
            Log.d("test", "not null drawable");
        }

        return drawable;
    }

    public static Bitmap getImageFromAssetsFile(Context context, String fileName) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static String saveBitmapToSD(Context context, String fileName) {
        Bitmap bitmap = getImageFromAssetsFile(context, "ic_launcher.png");
        FileOutputStream out = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) // 判断是否可以对SDcard进行操作
        {    // 获取SDCard指定目录下
            String sdCardDir = Environment.getExternalStorageDirectory() + fileName;
            File dirFile = new File(sdCardDir);  //目录转化成文件夹
            if (!dirFile.exists()) {              //如果不存在，那就建立这个文件夹
                dirFile.mkdirs();
            }                          //文件夹有啦，就可以保存图片啦
            File file = new File(sdCardDir, System.currentTimeMillis() + ".jpg");// 在SDcard的目录下创建图片文,以当前时间为其命名
            try {
                out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file.getAbsolutePath();
        }
        return "";
    }

    public static String saveBitmapToSD2(Context context, Bitmap bitmap, String fileName) {
        FileOutputStream out = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) // 判断是否可以对SDcard进行操作
        {    // 获取SDCard指定目录下
            String sdCardDir = Environment.getExternalStorageDirectory() + fileName;
            File dirFile = new File(sdCardDir);  //目录转化成文件夹
            if (!dirFile.exists()) {              //如果不存在，那就建立这个文件夹
                dirFile.mkdirs();
            }                          //文件夹有啦，就可以保存图片啦
            File file = new File(sdCardDir, System.currentTimeMillis() + ".jpg");// 在SDcard的目录下创建图片文,以当前时间为其命名
            try {
                out = new FileOutputStream(file);
                if (bitmap!=null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file.getAbsolutePath();
        }
        return "";
    }

    /**
     * 递归删除文件和文件夹
     *
     * @param file 要删除的根目录
     */
    public static void RecursionDeleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                RecursionDeleteFile(f);
            }
            file.delete();
        }
    }
}
