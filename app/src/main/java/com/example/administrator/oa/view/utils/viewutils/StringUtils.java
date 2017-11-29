package com.example.administrator.oa.view.utils.viewutils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 作者： itheima
 * 时间：2016-10-15 15:40
 * 网址：http://www.itheima.com
 */

public class StringUtils {

    public static boolean checkUsername(String username) {
        if (TextUtils.isEmpty(username)) {
            return false;
        }

        return username.matches("^[a-zA-Z]\\w{2,19}$");
    }

    public static boolean checkPwd(String pwd) {
        if (TextUtils.isEmpty(pwd)) {
            return false;
        }
        return pwd.matches("^[0-9]{3,20}$");
    }

    public static String getInitial(String contact) {
        if (TextUtils.isEmpty(contact)) {
            return contact;
        } else {
            return contact.substring(0, 1).toUpperCase();
        }
    }

    public static String getFirstLetter(String contact) {
        return contact.substring(0, 1);
    }

    /**
     * 判断首字符是否为汉字
     *
     * @param str
     * @return
     */
    public static boolean isChinese(String str) {
        char c = str.charAt(0);
        String str1 = String.valueOf(c);
        String regEx = "[\u4e00-\u9fa5]";
        Pattern pat = Pattern.compile(regEx);
        Matcher matcher = pat.matcher(str1);
        boolean flg = false;
        if (matcher.find())
            flg = true;

        return flg;
    }


    /**
     * 判断首字符是否是字母
     *
     * @param s
     * @return
     */
    public static boolean isZimu(String s) {
        char c = s.charAt(0);
        int i = (int) c;
        if ((i >= 65 && i <= 90) || (i >= 97 && i <= 122)) {
            return true;
        } else {
            return false;
        }
    }

}
