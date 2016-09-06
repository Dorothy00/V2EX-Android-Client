package com.dorothy.v2ex.http;

import android.content.Context;

import com.dorothy.v2ex.utils.V2EXCookieManager;

import java.io.IOException;
import java.util.List;

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
        String oldCookie = V2EXCookieManager.getCookie(context);
//        if (!TextUtils.isEmpty(oldCookie)) {
//            int end = oldCookie.indexOf(";");
//            oldCookie = oldCookie.substring(0, end) + ";";
//        }

        List<String> cookies = response.headers().values("Set-Cookie");
        StringBuilder sb = new StringBuilder();
        sb.append(oldCookie);
        if(oldCookie.contains("V2EX_LANG")){
            sb.append(cookies.get(0) + ";");
        }else{
            for (String cookie : cookies) {
                sb.append(cookie + ";");
            }
        }

        V2EXCookieManager.storeCookie(context, sb.toString());

        return response;
    }
}
