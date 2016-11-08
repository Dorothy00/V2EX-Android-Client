package com.dorothy.v2ex.http;

import android.content.Context;
import android.text.TextUtils;

import com.dorothy.v2ex.utils.Misc;
import com.dorothy.v2ex.utils.V2EXCookieManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by dorothy on 16/9/2.
 */
public class AddCookieIntercepter implements Interceptor {
    private Context context;

    public AddCookieIntercepter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request.Builder builder = chain.request().newBuilder();
        String requestUrl = chain.request().url().toString();
        String refer = V2EXCookieManager.getReferer(context);
        V2EXCookieManager.storeReferer(context, requestUrl);
        if (!TextUtils.isEmpty(refer)) {
            builder.addHeader("Referer", refer);
        }
        builder.addHeader("Host", "www.v2ex.com");
        builder.addHeader("Origin", "https://www.v2ex.com");
        builder.addHeader("Upgrade-Insecure-Requests", "1");
        builder.addHeader("Connection", "keep-alive");
        builder.addHeader("Cache-Control", "max-age=0");

        String cookie = V2EXCookieManager.getCookie(context);
        if (!TextUtils.isEmpty(cookie) && cookie.contains("PB3_SESSION")) {
            builder.addHeader("Cookie", cookie);
        }

        return chain.proceed(builder.build());
    }
}
