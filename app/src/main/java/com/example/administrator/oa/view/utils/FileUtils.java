package com.example.administrator.oa.view.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.net.URL;
import java.util.Locale;

/**
 * Created by Administrator on 2017/12/20.
 */

public class FileUtils {
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if(null!=cursor&&cursor.moveToFirst()){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }
    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
          * @param uri The Uri to check.
          * @return Whether the Uri authority is MediaProvider.
        */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }



    /**
     * 打开一个文件
     *
     * @param filePath
     *            文件的绝对路径
     */
    public static void openLocalFile(Context context, final String filePath)
    {
        String ext = filePath.substring(filePath.lastIndexOf('.')).toLowerCase(Locale.US);
        try
        {
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            String temp = ext.substring(1);
            String mime = mimeTypeMap.getMimeTypeFromExtension(temp);

            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            File file = new File(filePath);
            intent.setDataAndType(Uri.fromFile(file), mime);
            context.startActivity(intent);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(context, "该文件无法打开",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 打开一个web文件
     *
     * @param filePath
     *            文件的绝对路径
     */
    public static void openWebFile(Context context, final String filePath)
    {
        String ext = filePath.substring(filePath.lastIndexOf('.')).toLowerCase(Locale.US);
        try
        {
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            String temp = ext.substring(1);
            String mime = mimeTypeMap.getMimeTypeFromExtension(temp);

            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            // 部分手机存在兼容性问题，这里的url需要以
            Uri content_url = Uri.parse(filePath);
            Log.d("fileurl = ", filePath);
            if(mime.contains("image")) {
                intent.setDataAndType(content_url, mime);
            } else {
                intent.setData(content_url);
            }
            context.startActivity(intent);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(context, "该文件无法打开",
                    Toast.LENGTH_LONG).show();
        }
    }
}
