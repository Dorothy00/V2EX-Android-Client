package com.dorothy.v2ex.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by dorothy on 16/9/2.
 */
public class V2EXCookieManager {

    private static final String COOKIE_KEY = "Cookie";
    private static final String EXPIRES_KEY = "expires";
    private static final String REFER_KEY = "refer";

    public static void storeCookie(Context context, String cookie) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(COOKIE_KEY, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(COOKIE_KEY, cookie);
//        int start = cookie.indexOf("expires") + "expires=".length();
//        if (start < 0) {
//            editor.commit();
//            return;
//        }
//        int end = cookie.indexOf("GMT") + 3;
//        String expires = cookie.substring(start, end);
//        editor.putString(EXPIRES_KEY, expires);
        editor.commit();
    }

    public static String getCookie(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(COOKIE_KEY, Context
                .MODE_PRIVATE);
        String cookie = sharedPreferences.getString(COOKIE_KEY, "");
        return cookie;
    }

    public static void clearCookie(Context context) {
        SharedPreferences cookiePreferences = context.getSharedPreferences(COOKIE_KEY, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor cookieEditor = cookiePreferences.edit();
        cookieEditor.clear();
        cookieEditor.commit();

        SharedPreferences referPreferences = context.getSharedPreferences(REFER_KEY, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor referEditor = referPreferences.edit();
        referEditor.clear();
        referEditor.commit();

        UserCache.clearUserCache(context);
    }

    public static boolean isEXpired(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(COOKIE_KEY, Context
                .MODE_PRIVATE);
        String expires = sharedPreferences.getString(EXPIRES_KEY, "");
        SimpleDateFormat format = new SimpleDateFormat("EEE',' d MMM yyyy HH:mm:ss 'GMT'", Locale
                .ENGLISH);
        if (TextUtils.isEmpty(expires))
            return true;

        try {
            Date expiresDate = format.parse(expires);
            Date nowDate = new Date();
            if (nowDate.before(expiresDate))
                return false;
            else
                return true;
        } catch (ParseException e) {
            Toast.makeText(context, "日期错误", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    public static void storeReferer(Context context, String refer) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(REFER_KEY, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("refer", refer);
        editor.commit();
    }

    public static String getReferer(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(REFER_KEY, Context
                .MODE_PRIVATE);
        return sharedPreferences.getString("refer", "");
    }
}

