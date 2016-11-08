package com.dorothy.v2ex.http;

import android.content.Context;

import com.dorothy.v2ex.utils.Misc;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by dorothy on 2016/11/8.
 */

public class NetWorkStateIntercepter implements Interceptor {
    private Context mContext;

    public NetWorkStateIntercepter(Context context) {
        mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!Misc.isNetworkAvailable(mContext)) {
            throw new NetWorkUnavaliableException("NetWork is unconnected.");
        }
        return chain.proceed(chain.request());
    }
}
