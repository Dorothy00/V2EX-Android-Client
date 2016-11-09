package com.dorothy.v2ex.http;

import com.dorothy.v2ex.models.MemberDetail;
import com.dorothy.v2ex.models.NodeDetail;
import com.dorothy.v2ex.models.Reply;
import com.dorothy.v2ex.models.Topic;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by dorothy on 16/8/5.
 */
public interface V2EXApiService {

    @GET("api/topics/latest.json")
    Observable<List<Topic>> getLatestTopics();

    @GET("api/topics/show.json")
    Observable<List<Topic>> getTopicsByUsername(@Query("username") String username);

    @GET("/go/{node}")
    Observable<String> getTopicsByNode(@Path("node") String nodeName);

    @GET("api/topics/hot.json")
    Observable<List<Topic>> getHotTopics();

    @GET("/api/members/show.json")
    Observable<MemberDetail> getMemberDetail(@Query("username") String username);

    @GET("/")
    Observable<String> getTopicsByTab(@Query("tab") String tab);

    @GET("/t/{id}")
    Observable<String> getTopicById(@Path("id") long id);

    @GET("/signin")
    Observable<String> getLoginPage();

    @FormUrlEncoded
    @POST("/signin")
    Observable<String> login(@FieldMap Map<String, String> params);

    @GET("/")
    Observable<String> getUserProfile();

    @GET("/notifications")
    Observable<String> getNotification();

    @GET("/api/nodes/all.json")
    Observable<List<NodeDetail>> getAllNodes();

    @FormUrlEncoded
    @POST("/t/{id}")
    Observable<String> commentTopic(@Path("id") long id, @FieldMap Map<String, String> params);

    @GET("/my/nodes")
    Observable<String> getCollectedNodes();

    @GET("{path}")
    Call<String> collectNode(@Path("path") String path, @Query("once") String once);

    @GET("/new")
    Observable<String> getNewTopicPage();

    @FormUrlEncoded
    @POST("/new")
    Observable<String> postNewTopic(@FieldMap Map<String, String> params);

    @GET("/my/topics")
    Observable<String> getCollectedTopic();

}
