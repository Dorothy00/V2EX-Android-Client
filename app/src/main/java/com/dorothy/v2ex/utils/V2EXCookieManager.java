package com.dorothy.v2ex.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by dorothy on 16/9/2.
 */
public class V2EXCookieManager {

    private static final String COOKIE_KEY = "Cookie";
    private static final String REFER_KEY = "refer";

    public static void storeCookie(Context context, Map<String, String> cookies) {
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        SharedPreferences sharedPreferences = context.getSharedPreferences(COOKIE_KEY, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String oldCookiesStr = sharedPreferences.getString(COOKIE_KEY, "");
        Map<String, String> oldCookiesMap = gson.fromJson(oldCookiesStr, new
                TypeToken<Map<String, String>>() {
                }.getType());

        if (oldCookiesMap == null)
            oldCookiesMap = new HashMap<>();

        for (String key : cookies.keySet()) {
            oldCookiesMap.put(key, cookies.get(key));
        }
        editor.putString(COOKIE_KEY, gson.toJson(oldCookiesMap));
        editor.commit();
    }


    public static String getCookie(Context context) {
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        SharedPreferences sharedPreferences = context.getSharedPreferences(COOKIE_KEY, Context
                .MODE_PRIVATE);
        String cookie = sharedPreferences.getString(COOKIE_KEY, "");
        if (TextUtils.isEmpty(cookie))
            return "";

        Map<String, String> cookieMap = gson.fromJson(cookie, new TypeToken<Map<String, String>>() {
        }.getType());
        StringBuilder sb = new StringBuilder();
        for (String key : cookieMap.keySet()) {
            String cookieStr = key + "=" + cookieMap.get(key) + ";";
            sb.append(cookieStr);
        }
        return sb.toString();
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

    public static boolean isExpired(Context context) {
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        SharedPreferences sharedPreferences = context.getSharedPreferences(COOKIE_KEY, Context
                .MODE_PRIVATE);
        String cookie = sharedPreferences.getString(COOKIE_KEY, "");
        if (TextUtils.isEmpty(cookie))
            return true;

        Map<String, String> cookieMap = gson.fromJson(cookie, new TypeToken<Map<String, String>>() {
        }.getType());
        String expires = cookieMap.get(" expires");
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

