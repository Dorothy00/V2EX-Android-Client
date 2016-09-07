package com.dorothy.v2ex.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dorothy.v2ex.R;
import com.dorothy.v2ex.View.CircleImageView;
import com.dorothy.v2ex.View.WrapLinearLayoutManager;
import com.dorothy.v2ex.adapter.BaseRecyclerAdapter;
import com.dorothy.v2ex.adapter.RecyclerViewHolder;
import com.dorothy.v2ex.http.V2EXApiService;
import com.dorothy.v2ex.http.V2EXHttpClient;
import com.dorothy.v2ex.models.Notification;
import com.dorothy.v2ex.utils.V2EXHtmlParser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NotificationActivity extends AppCompatActivity implements SwipeRefreshLayout
        .OnRefreshListener, BaseRecyclerAdapter.OnItemClickListener {

    private Toolbar mToolbar;
    private RecyclerView mRvNotification;
    private SwipeRefreshLayout mSwipeView;
    private NotifyAdapter mAdapter;
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
        mRvNotification.setLayoutManager(new WrapLinearLayoutManager(this));
        mAdapter = new NotifyAdapter(this, mNotifyList);
        mAdapter.setOnItemClickListener(this);
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
    public void onItemClick(int pos) {
        Notification notification = mNotifyList.get(pos);
        startActivity(TopicDetailActivity.newIntent(this, notification.getTopicId()));
    }


    class NotifyAdapter extends BaseRecyclerAdapter<Notification> {

        public NotifyAdapter(Context context, List<Notification> dataList) {
            super(context, dataList);
        }

        @Override
        public void bindData(RecyclerViewHolder viewHolder, Notification data, int position) {
            CircleImageView ciAvatar = viewHolder.getCircleImageView(R.id.avatar);
            TextView tvTitle = viewHolder.getTextView(R.id.title);
            TextView tvReplyContent = viewHolder.getTextView(R.id.reply_content);
            TextView tvTime = viewHolder.getTextView(R.id.reply_time);

            Notification notification = mNotifyList.get(position);
            Glide.with(context).load("http:" + notification.getMember().getAvatarMini()).into
                    (ciAvatar);
            tvTitle.setText(notification.getReplyTitle());
            tvReplyContent.setText(notification.getReplyContent());
            tvTime.setText(notification.getTime());
        }

        @Override
        public int getLayoutResId() {
            return R.layout.item_notification;
        }
    }
}
