package com.dorothy.v2ex.http;

import android.content.Context;
import android.widget.Toast;

import com.dorothy.v2ex.activity.UserInfoActivity;

import rx.Subscriber;

/**
 * Created by dorothy on 2016/11/8.
 */

public class V2EXSubscriberAdapter<T> extends Subscriber<T> {
    private Context context;

    public V2EXSubscriberAdapter(Context context){
        this.context = context;
    }
    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof NetWorkUnavaliableException) {
            Toast.makeText(context, "网络未连接", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "网络请求错误", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNext(T t) {

    }
}
