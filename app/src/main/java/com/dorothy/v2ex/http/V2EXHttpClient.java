package com.dorothy.v2ex.http;

import android.content.Context;

import com.dorothy.v2ex.fragment.TopicListFragment;
import com.dorothy.v2ex.models.MemberDetail;
import com.dorothy.v2ex.models.Node;
import com.dorothy.v2ex.models.NodeDetail;
import com.dorothy.v2ex.models.Notification;
import com.dorothy.v2ex.models.Topic;
import com.dorothy.v2ex.utils.V2EXHtmlParser;
import com.dorothy.v2ex.utils.V2EXStringConverter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by dorothy on 16/8/30.
 */
public class V2EXHttpClient {
    private static Context context;

    public static final String BASE_URL = "https://www.v2ex.com";
    private static OkHttpClient mOkhttpClient;
    private static Retrofit mStringRetrofit;
    private static Retrofit mGsonRetrofit;

    private static void initHttpClient() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        if (httpClientBuilder.interceptors() != null) {
            httpClientBuilder.interceptors().clear();
        }
        httpClientBuilder.addInterceptor(new NetWorkStateIntercepter(context))
                .addNetworkInterceptor(new RecivedCookieIntercepter(context))
                .addNetworkInterceptor(new AddCookieIntercepter(context));

        httpClientBuilder.connectTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS);
        mOkhttpClient = httpClientBuilder.build();
    }

    public static Retrofit stringRetrofit(Context cxt) {
        context = cxt;
        if (mStringRetrofit == null) {
            initHttpClient();
            mStringRetrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(V2EXStringConverter.create())
                    .addCallAdapterFactory
                            (RxJavaCallAdapterFactory.create()).client(mOkhttpClient).build();
        }
        return mStringRetrofit;
    }

    public static Retrofit gsonRetrofit(Context cxt) {
        context = cxt;
        if (mGsonRetrofit == null) {
            initHttpClient();
            mGsonRetrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                    .addCallAdapterFactory
                            (RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create()).client(mOkhttpClient)
                    .build();
        }
        return mGsonRetrofit;
    }

    public static void getTopicsByUsername(Context context, String username,
                                           Subscriber<List<Topic>> subscriber) {
        V2EXApiService apiService = V2EXHttpClient.gsonRetrofit(context).create(V2EXApiService
                .class);
        apiService.getTopicsByUsername(username).subscribeOn(Schedulers.io()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(subscriber);
    }

    public static void getTopicsByTab(Context context, String tab, Subscriber<String> subscriber) {
        V2EXApiService apiService = V2EXHttpClient.stringRetrofit(context).create(V2EXApiService
                .class);
        apiService.getTopicsByTab(tab).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers
                .mainThread()).subscribe(subscriber);
    }

    public static void getTopicByAPI(Context context, String type, Subscriber<List<Topic>>
            subscriber) {
        V2EXApiService apiService = V2EXHttpClient.gsonRetrofit(context).create(V2EXApiService
                .class);
        Observable<List<Topic>> observable;
        if (type.equals(TopicListFragment.TOPIC_LATEST)) {
            observable = apiService.getLatestTopics();
        } else {
            observable = apiService.getHotTopics();
        }
        observable.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(subscriber);
    }

    public static void getTopicsByNode(Context context, String node, Subscriber<List<Topic>>
            subscriber) {
        V2EXApiService apiService = V2EXHttpClient.stringRetrofit(context).create(V2EXApiService
                .class);
        apiService.getTopicsByNode(node).flatMap(new Func1<String, Observable<List<Topic>>>() {
            @Override
            public Observable<List<Topic>> call(String s) {
                List<Topic> topicList = V2EXHtmlParser.parseTopicList(s, V2EXHtmlParser.FROM_NODE);
                return Observable.just(topicList);
            }
        }).subscribeOn(Schedulers.io()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers
                .mainThread()).subscribe(subscriber);
    }

    public static void getMemberDetail(final Context context, String username, final
    Subscriber<MemberDetail> subscriber) {
        final V2EXApiService apiService = V2EXHttpClient.gsonRetrofit(context).create(V2EXApiService
                .class);
        apiService.getMemberDetail(username).subscribeOn(Schedulers.io()).unsubscribeOn
                (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
    }

    public static void getTopicById(Context context, long id, Subscriber<String> subscriber) {
        V2EXApiService apiService = V2EXHttpClient.stringRetrofit(context).create(V2EXApiService
                .class);
        apiService.getTopicById(id).subscribeOn(Schedulers.io()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers
                        .mainThread()).subscribe(subscriber);
    }

    public static void commentTopic(Context context, long id, Map<String, String> params,
                                    Subscriber<String> subscriber) {
        V2EXApiService apiService = V2EXHttpClient.stringRetrofit(context).create(V2EXApiService
                .class);
        apiService.commentTopic(id, params).subscribeOn(Schedulers.io()).unsubscribeOn
                (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
    }

    public static void getCollectedNodes(Context context, Subscriber<List<Node>> subscriber) {
        V2EXApiService apiService = V2EXHttpClient.stringRetrofit(context).create(V2EXApiService
                .class);
        apiService.getCollectedNodes().flatMap(new Func1<String, Observable<List<Node>>>() {
            @Override
            public Observable<List<Node>> call(String s) {
                return Observable.just(V2EXHtmlParser.parseCollectedNode(s));
            }
        }).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(subscriber);
    }

    public static void getAllNodes(Context context, Subscriber<List<NodeDetail>> subscriber) {
        final V2EXApiService apiService = V2EXHttpClient.gsonRetrofit(context).create(V2EXApiService
                .class);
        apiService.getAllNodes().subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
    }

    public static void postNewTopic(Context context, Map<String, String> params,
                                    Subscriber<String> subscriber) {
        V2EXApiService apiService = V2EXHttpClient.stringRetrofit(context).create(V2EXApiService
                .class);
        apiService.postNewTopic(params).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io
                ()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
    }

    public static void getNewTopicPage(Context context, Subscriber<String> subscriber) {
        V2EXApiService apiService = V2EXHttpClient.stringRetrofit(context).create(V2EXApiService
                .class);
        apiService.getNewTopicPage().subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io
                ()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
    }

    public static void getNotification(Context context, Subscriber<List<Notification>> subscriber) {
        V2EXApiService apiService = V2EXHttpClient.stringRetrofit(context).create(V2EXApiService
                .class);
        apiService.getNotification().flatMap(new Func1<String, Observable<List<Notification>>>() {
            @Override
            public Observable<List<Notification>> call(String s) {
                return Observable.just(V2EXHtmlParser.parseNotification(s));
            }
        }).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(subscriber);
    }

    public static void getLoginPage(Context context, Subscriber<String> subscriber) {
        V2EXApiService apiService = V2EXHttpClient.stringRetrofit(context).create(V2EXApiService
                .class);
        apiService.getLoginPage().subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io
                ()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
    }

    public static void login(Context context, Map<String, String> params, Subscriber<String>
            subscriber) {
        V2EXApiService apiService = V2EXHttpClient.stringRetrofit(context).create(V2EXApiService
                .class);
        apiService.login(params).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io
                ()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
    }

    public static void getUserProfile(Context context, Subscriber<String> subscriber) {
        V2EXApiService apiService = V2EXHttpClient.stringRetrofit(context).create(V2EXApiService
                .class);
        apiService.getUserProfile().subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io
                ()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
    }

}
