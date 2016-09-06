package com.dorothy.v2ex.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.dorothy.v2ex.R;
import com.dorothy.v2ex.View.WrapContentLinearLayoutManager;
import com.dorothy.v2ex.adapter.NotificationAdapter;
import com.dorothy.v2ex.http.V2EXApiService;
import com.dorothy.v2ex.http.V2EXHttpClient;
import com.dorothy.v2ex.interfaces.NotifyItemClickListener;
import com.dorothy.v2ex.models.Notification;
import com.dorothy.v2ex.utils.V2EXHtmlParser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NotificationActivity extends AppCompatActivity implements SwipeRefreshLayout
        .OnRefreshListener, NotifyItemClickListener {

    private Toolbar mToolbar;
    private RecyclerView mRvNotification;
    private SwipeRefreshLayout mSwipeView;
    private NotificationAdapter mAdapter;
    private List<Notification> mNotifyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("消息系统");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSwipeView = (SwipeRefreshLayout) findViewById(R.id.swpie_view);
        mSwipeView.setColorSchemeColors(Color.RED);
        mSwipeView.setOnRefreshListener(this);
        mSwipeView.setDistanceToTriggerSync(300);

        mRvNotification = (RecyclerView) findViewById(R.id.recycle_view);
        mRvNotification.setLayoutManager(new WrapContentLinearLayoutManager(this));
        mAdapter = new NotificationAdapter(this, this, mNotifyList);
        mRvNotification.setAdapter(mAdapter);

        mSwipeView.post(new Runnable() {
            @Override
            public void run() {
                mSwipeView.setRefreshing(true);
            }
        });
        onRefresh();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void fetchNotifications() {
        Retrofit retrofit = V2EXHttpClient.retrofit(this);
        V2EXApiService apiService = retrofit.create(V2EXApiService.class);
        Call<String> call = apiService.getNotification();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null && response.isSuccessful()) {
                    List<Notification> notificationList = V2EXHtmlParser.parseNotification
                            (response.body());
                    mNotifyList.clear();
                    mNotifyList.addAll(notificationList);
                    mAdapter.notifyItemRangeInserted(0, notificationList.size());

                } else {
                    // TODO
                }
                mSwipeView.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // TODO
                mSwipeView.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        fetchNotifications();
    }

    @Override
    public void notifyItemOnClick(Notification notification) {
        startActivity(TopicDetailActivity.newIntent(this, notification.getTopicId()));
    }
}
