package com.example.administrator.oa.view.utils.viewutils;

/**
 * Created by Administrator on 2016/11/11.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.FileNotFoundException;

/**
 * 选择本地图片工具类
 * <br>
 * 因为直接获取图片容易崩溃，所以直接存入SD卡，再获取
 * <br>
 * 又因为写法不正确容易导致部分机型无法使用，所以封装起来复用
 * <br>
 * 使用方法：
 * <br>
 * 1、调用getByAlbum、getByCamera去获取图片
 * <br>
 * 2、在onActivityResult中调用本工具类的onActivityResult方法进行处理
 * <br>
 * 3、onActivityResult返回的Bitmap记得空指针判断
 * <p/>
 * <br><br>
 * PS：本工具类只能处理裁剪图片，如果不想裁剪，不使用本工具类的onActivityResult，自己做处理即可
 *
 * @author linin630
 */
public class SelectPicUtil {

    /**
     * 临时存放图片的地址，如需修改，请记得创建该路径下的文件夹
     */
    private static final String lsimg = "file:///sdcard/temp.jpg";

    public static final int GET_BY_ALBUM = 801;//如果有冲突，记得修改
    public static final int GET_BY_CAMERA = 802;//如果有冲突，记得修改
    public static final int CROP = 803;//如果有冲突，记得修改
    private Activity act;
    /**
     * 从相册获取图片
     */

    public static void getByAlbum(Activity act) {
        Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
        getAlbum.addCategory(Intent.CATEGORY_OPENABLE);
        getAlbum.setType("image/*");
        act.startActivityForResult(getAlbum, GET_BY_ALBUM);
    }

    /**
     * 通过拍照获取图片
     */
    public static void getByCamera(Activity act) {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent getImageByCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse(lsimg));
            getImageByCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            act.startActivityForResult(getImageByCamera, GET_BY_CAMERA);
        } else {
            Toast.makeText(act, "请确定已经插入sd卡", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 处理获取的图片，注意判断空指针，默认大小480*480，比例1:1
     */
    public static Bitmap onActivityResult(Activity act, int requestCode, int resultCode, Intent data) {
        return onActivityResult(act, requestCode, resultCode, data, 0, 0, 0, 0);
    }

    /**
     * 处理获取的图片，注意判断空指针
     */
    public static Bitmap onActivityResult(Activity act, int requestCode, int resultCode, Intent data,
                                          int w, int h, int aspectX, int aspectY) {
        Bitmap bm = null;
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            switch (requestCode) {
                case GET_BY_ALBUM:
                    uri = data.getData();
                    act.startActivityForResult(crop(uri, w, h, aspectX, aspectY), CROP);
                    break;
                case GET_BY_CAMERA:
                    uri = Uri.parse(lsimg);
                    act.startActivityForResult(crop(uri, w, h, aspectX, aspectY), CROP);
                    break;
                case CROP:
                    bm = dealCrop(act);
                    break;
            }
        }
        return bm;
    }


    /**
     * 默认裁剪输出320*480，比例2:3
     */
    public static Intent crop(Uri uri) {
        return crop(uri, 320, 480, 2, 3);
    }

    /**
     * 裁剪，例如：输出100*100大小的图片，宽高比例是1:1
     *
     * @param w       输出宽
     * @param h       输出高
     * @param aspectX 宽比例
     * @param aspectY 高比例
     */
    public static Intent crop(Uri uri, int w, int h, int aspectX, int aspectY) {
        if (w == 0 && h == 0) {
            w = 320;
            h = 480;
        }
        if (aspectX == 0 && aspectY == 0) {
            aspectX  = 2;
            aspectY = 3;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        // 照片URL地址
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", w);
        intent.putExtra("outputY", h);
        intent.putExtra("circleCrop", false);
        // 输出路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse(lsimg));
        // 输出格式
        intent.putExtra("outputFormat", "JPEG");
        // 不启用人脸识别
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", false);
        return intent;
    }

    /**
     * 处理裁剪，获取裁剪后的图片
     */
    public static Bitmap dealCrop(Context context) {
        // 裁剪返回
        Uri uri = Uri.parse(lsimg);
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
            Log.w("SelectPicUtil", "uri:" + uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}