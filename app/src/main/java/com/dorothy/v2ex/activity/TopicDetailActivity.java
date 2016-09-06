package com.dorothy.v2ex.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dorothy.v2ex.R;
import com.dorothy.v2ex.View.CircleImageView;
import com.dorothy.v2ex.http.V2EXApiService;
import com.dorothy.v2ex.http.V2EXHttpClient;
import com.dorothy.v2ex.interfaces.MemberClickListener;
import com.dorothy.v2ex.models.Member;
import com.dorothy.v2ex.models.Reply;
import com.dorothy.v2ex.models.Topic;
import com.dorothy.v2ex.utils.V2EXHtmlParser;
import com.dorothy.v2ex.utils.V2EXImageGetter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TopicDetailActivity extends AppCompatActivity implements MemberClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private Toolbar mToolbar;
    private TextView mTvContent;
    private TextView mTvTitle;
    private CircleImageView mCiAvatar;
    private TextView mTvAuthor;
    private RecyclerView mRvRepliesView;
    private SwipeRefreshLayout mSwipeView;
    private long mTopicId;
    private List<Reply> mReplyList = new ArrayList<Reply>();
    private RepliesAdapter mRepliesAdapter;

    public static Intent newIntent(Activity activity, long topicId) {
        Intent intent = new Intent(activity, TopicDetailActivity.class);
        intent.putExtra("id", topicId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mTvTitle = (TextView) findViewById(R.id.topic_title);
        mTvContent = (TextView) findViewById(R.id.topic_content);
        mTvAuthor = (TextView) findViewById(R.id.topic_author);

        mCiAvatar = (CircleImageView) findViewById(R.id.topic_author_avatar);


        mSwipeView = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeView.setColorSchemeColors(Color.RED);
        mSwipeView.setOnRefreshListener(this);
        mSwipeView.setDistanceToTriggerSync(300);

        mRvRepliesView = (RecyclerView) findViewById(R.id.reply_recycler_view);
        mRvRepliesView.setLayoutManager(new LinearLayoutManager(this));
        mRepliesAdapter = new RepliesAdapter(mReplyList, this);
        mRvRepliesView.setAdapter(mRepliesAdapter);


        Intent intent = getIntent();
        if (intent != null) {
            mTopicId = intent.getLongExtra("id", -1);

            mSwipeView.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeView.setRefreshing(true);
                }
            });
            onRefresh();
        }
    }

    private void fetchTopic(long topicId) {
        if (mTopicId < 0)
            return;

        Retrofit retrofit = V2EXHttpClient.retrofit(this);
        V2EXApiService apiService = retrofit.create(V2EXApiService.class);
        Call<String> call = apiService.getTopicById(topicId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null && response.isSuccessful()) {
                    Topic topic = V2EXHtmlParser.parseTopic(response.body());
                    renderContent(topic);
                    fetchTopicReplies(mTopicId);

                } else {
                    //TODO
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (t != null) {
                    Toast.makeText(TopicDetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                    mSwipeView.setRefreshing(false);
                }
            }
        });
    }

    private void fetchTopicReplies(long topicId) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(V2EXApiService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        V2EXApiService apiService = retrofit.create(V2EXApiService.class);
        Call<List<Reply>> call = apiService.getTopicReplies(topicId);
        call.enqueue(new Callback<List<Reply>>() {
            @Override
            public void onResponse(Call<List<Reply>> call, Response<List<Reply>> response) {
                if (response.code() == 200) {
                    mReplyList = response.body();
                    mRepliesAdapter.setData(mReplyList);
                } else {
                    // TODO
                }
                mSwipeView.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Reply>> call, Throwable t) {
                Toast.makeText(TopicDetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT)
                        .show();
                mSwipeView.setRefreshing(false);
            }
        });
    }

    private void renderContent(final Topic topic) {
        V2EXImageGetter v2exImageParser = new V2EXImageGetter(mTvContent,
                TopicDetailActivity.this);

        mTvContent.setText(Html.fromHtml(topic.getContentRendered() + " ",
                v2exImageParser,
                null));
        mTvContent.setMovementMethod(LinkMovementMethod.getInstance());
        mTvAuthor.setText(topic.getMember().getUsername());
        mTvTitle.setText(topic.getTitle());
        Glide.with(this).load("http:" + topic.getMember().getAvatarLarge()).into(mCiAvatar);
        if (!mCiAvatar.hasOnClickListeners()) {
            mCiAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(UserInfoActivity.newIntent(TopicDetailActivity.this, topic
                            .getMember()));
                }
            });
        }
    }

    @Override
    public void onMemberClick(Member member) {
        startActivity(UserInfoActivity.newIntent(this, member));
    }

    @Override
    public void onRefresh() {
        fetchTopic(mTopicId);
    }

    public class RepliesAdapter extends RecyclerView.Adapter<RepliesAdapter.ReplyViewHolder> {
        private List<Reply> replyList;
        private MemberClickListener memberClickListener;

        public RepliesAdapter(List<Reply> replyList, MemberClickListener listener) {
            this.replyList = replyList;
            this.memberClickListener = listener;
        }

        public void setData(List<Reply> replyList) {
            int start = this.replyList.size();
            this.replyList.addAll(replyList);
            notifyItemRangeInserted(start, this.replyList.size());
        }

        @Override
        public ReplyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(TopicDetailActivity.this).inflate(R.layout
                            .item_topic_reply, parent,
                    false);
            ReplyViewHolder viewHolder = new ReplyViewHolder(view, memberClickListener);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ReplyViewHolder holder, int position) {
            Reply reply = replyList.get(position);
            Glide.with(TopicDetailActivity.this).load("http:" + reply.getMember().getAvatarNormal
                    ()).into
                    (holder.ciAvatar);
            holder.tvAuthor.setText(reply.getMember().getUsername());
            V2EXImageGetter imageGetter = new V2EXImageGetter(holder.tvContent,
                    TopicDetailActivity.this);
            holder.tvContent.setText(Html.fromHtml(reply.getContentRendered(), imageGetter, null));
            holder.tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        }

        @Override
        public int getItemCount() {
            return replyList.size();
        }

        class ReplyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            CircleImageView ciAvatar;
            TextView tvAuthor;
            TextView tvContent;
            MemberClickListener memberClickListener;

            public ReplyViewHolder(View v, MemberClickListener listener) {
                super(v);
                memberClickListener = listener;
                ciAvatar = (CircleImageView) v.findViewById(R.id.reply_avatar);
                ciAvatar.setOnClickListener(this);
                tvAuthor = (TextView) v.findViewById(R.id.reply_author);
                tvContent = (TextView) v.findViewById(R.id.reply_content);
            }

            @Override
            public void onClick(View v) {
                if (memberClickListener != null) {
                    memberClickListener.onMemberClick(replyList.get(getAdapterPosition())
                            .getMember());
                }
            }
        }
    }
}


