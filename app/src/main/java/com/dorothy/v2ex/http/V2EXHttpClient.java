package com.dorothy.v2ex.http;

import android.content.Context;

import com.dorothy.v2ex.utils.V2EXStringConverter;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dorothy on 16/8/30.
 */
public class V2EXHttpClient {
    private static Context context;

    public static final String BASE_URL = "http://www.v2ex.com";
    private static OkHttpClient mOkhttpClient;
    private static Retrofit mRetrofit;

    private static void initHttpClient() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        if (httpClientBuilder.interceptors() != null) {
            httpClientBuilder.interceptors().clear();
        }
       httpClientBuilder.addNetworkInterceptor(new AddCookieIntercepter(context));
        httpClientBuilder.addInterceptor(new RecivedCookieIntercepter(context));
//        httpClientBuilder
//                .addNetworkInterceptor(new AddCookieIntercepter(context));
//        httpClientBuilder.addNetworkInterceptor(new RecivedCookieIntercepter(context)).addNetworkInterceptor(new AddCookieIntercepter(context));

        httpClientBuilder.connectTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS);

        mOkhttpClient = httpClientBuilder.build();
    }

    public static Retrofit retrofit(Context cxt) {
        context = cxt;
        if (mRetrofit == null) {
            initHttpClient();
            mRetrofit = new Retrofit.Builder().baseUrl(BASE_URL).client(mOkhttpClient)
                    .addConverterFactory(V2EXStringConverter.create())
                    .addConverterFactory(GsonConverterFactory.create()).build();
        }
        return mRetrofit;
    }


}
