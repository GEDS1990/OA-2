package com.example.administrator.oa.view.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.UUID;


/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/8/1
 *     desc  : 设备相关工具类
 * </pre>
 */
public class DeviceUtils {

    /**
     * 获取当前设置的电话号码
     */
    public static String getNativePhoneNumber(Context context) {
        String nativePhoneNumber = "";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            nativePhoneNumber = telephonyManager.getLine1Number();
        } catch (Exception e) {
            nativePhoneNumber = "";
        }
        return nativePhoneNumber;
    }

    /**
     * 获取设备序列号
     */
    public static String getDeviceId(Context context) {
        String deviceId = "";
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = telephonyManager.getDeviceId();
        if (deviceId == null || "".equals(deviceId)) {
            deviceId = "0000000000";
        }
        return deviceId;
    }

    /**
     * @description：获取设备唯一标识
     */
    public static String getDeviceUni(Context context) {
        String deviceUni = "";
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        deviceUni = tm.getDeviceId();
        if (TextUtils.isEmpty(deviceUni)) {
            deviceUni = getMacAddress(context);
        }
        return deviceUni;
    }

    /**
     * @description：获取wifimac地址
     */
    public static String getMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String wifiMac = info.getMacAddress();
        return wifiMac;
    }

    /**
     * 获得一个去掉"-"符号的UUID
     */
    public static String getUUID() {
        String s = UUID.randomUUID().toString();
        // 去掉"-"符号
        return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23) + s.substring(24);
    }

    /**
     * 判断是否已连接到网络.
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断手机是否处于飞行模式.
     *
     * @param context
     * @return
     */
    public static boolean isAirplaneModeOn(Context context) {
        return Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
    }

    /**
     * 获取手机品牌
     *
     * @return
     */
    public static String getPhoneBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取手机Android 版本（4.4、5.0、5.1 ...）
     *
     * @return
     */
    public static String getBuildVersion() {
        return android.os.Build.VERSION.RELEASE;
    }
}