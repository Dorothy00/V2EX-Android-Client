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
import com.dorothy.v2ex.http.V2EXHttpClient;
import com.dorothy.v2ex.http.V2EXSubscriberAdapter;
import com.dorothy.v2ex.models.Notification;

import java.util.ArrayList;
import java.util.List;

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

        V2EXHttpClient.getNotification(this, new V2EXSubscriberAdapter<List<Notification>>(this) {
            @Override
            public void onNext(List<Notification> notifications) {
                super.onNext(notifications);
                mNotifyList.clear();
                mNotifyList.addAll(notifications);
                mAdapter.notifyItemRangeInserted(0, notifications.size());
                mSwipeView.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
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
