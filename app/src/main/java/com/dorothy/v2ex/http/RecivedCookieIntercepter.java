package com.dorothy.v2ex.http;

import android.content.Context;

import com.dorothy.v2ex.utils.V2EXCookieManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by dorothy on 16/9/2.
 */
public class RecivedCookieIntercepter implements Interceptor {
    private Context context;

    public RecivedCookieIntercepter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        Map<String, String> cookieMap = new HashMap<>();
        List<String> cookieList = response.headers().values("Set-Cookie");
        for (String cookies : cookieList) {
            String[] cookieSet = cookies.split(";");
            for (int i = 0; i < cookieSet.length; i++) {
                String cookieStr = cookieSet[i];
                if (!cookieStr.contains("="))
                    continue;
                if (cookieStr.startsWith("_gat"))
                    continue;
                if (cookieStr.startsWith("_ga"))
                    continue;
                int index = cookieStr.indexOf("=");
                String key = cookieStr.substring(0, index);
                String value = cookieStr.substring(index + 1);
                cookieMap.put(key, value);
            }
        }

        V2EXCookieManager.storeCookie(context, cookieMap);

        return response;
    }
}
