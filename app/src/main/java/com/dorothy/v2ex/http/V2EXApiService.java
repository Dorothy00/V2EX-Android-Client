package com.dorothy.v2ex.http;

import com.dorothy.v2ex.models.MemberDetail;
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

/**
 * Created by dorothy on 16/8/5.
 */
public interface V2EXApiService {
    String BASE_URL = "http://www.v2ex.com";

    @GET("api/topics/latest.json")
    Call<List<Topic>> getLatestTopics();

    @GET("api/topics/show.json")
    Call<List<Topic>> getTopicsByUsername(@Query("username") String username);

    @GET("api/topics/hot.json")
    Call<List<Topic>> getHotTopics();

    @GET("/api/replies/show.json")
    Call<List<Reply>> getTopicReplies(@Query("topic_id") long topicId);

    @GET("/api/members/show.json")
    Call<MemberDetail> getMemberDetail(@Query("username") String username);

    @GET("/")
    Call<String> getTopicsByTab(@Query("tab") String tab);

    @GET("/t/{id}")
    Call<String> getTopicById(@Path("id") long id);

    @GET("/signin")
    Call<String> getLoginPage();

    @FormUrlEncoded
    @POST("/signin")
    Call<String> login(@FieldMap Map<String, String> params);

    @GET("/")
    Call<String> getUserProfile();

    @GET("/notifications")
    Call<String> getNotification();

}
