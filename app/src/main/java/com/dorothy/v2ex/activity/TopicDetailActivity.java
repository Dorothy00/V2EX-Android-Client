package com.dorothy.v2ex.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dorothy.v2ex.R;
import com.dorothy.v2ex.View.CircleImageView;
import com.dorothy.v2ex.View.WrapLinearLayoutManager;
import com.dorothy.v2ex.adapter.BaseRecyclerAdapter;
import com.dorothy.v2ex.adapter.RecyclerViewHolder;
import com.dorothy.v2ex.http.V2EXApiService;
import com.dorothy.v2ex.http.V2EXHttpClient;
import com.dorothy.v2ex.models.Member;
import com.dorothy.v2ex.models.Reply;
import com.dorothy.v2ex.models.Topic;
import com.dorothy.v2ex.models.UserProfile;
import com.dorothy.v2ex.utils.UserCache;
import com.dorothy.v2ex.utils.V2EXHtmlParser;
import com.dorothy.v2ex.utils.V2EXImageGetter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TopicDetailActivity extends AppCompatActivity implements BaseRecyclerAdapter
        .OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private Toolbar mToolbar;
    private TextView mTvContent;
    private TextView mTvTitle;
    private CircleImageView mCiAvatar;
    private TextView mTvAuthor;
    private RecyclerView mRvRepliesView;
    private SwipeRefreshLayout mSwipeView;
    private ImageView mBtnReply;
    private EditText mEtReplyContent;
    private Topic mTopic;
    private long mTopicId;
    private List<Reply> mReplyList = new ArrayList<>();
    private ReplyAdapter mRepliesAdapter;

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
        mToolbar.setTitle("话题详情");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTvTitle = (TextView) findViewById(R.id.topic_title);
        mTvContent = (TextView) findViewById(R.id.topic_content);
        mTvAuthor = (TextView) findViewById(R.id.topic_author);
        mCiAvatar = (CircleImageView) findViewById(R.id.topic_author_avatar);
        mBtnReply = (ImageView) findViewById(R.id.btn_reply);
        mEtReplyContent = (EditText) findViewById(R.id.reply);
        mBtnReply.setOnClickListener(mReplyBtnClickListener);

        mSwipeView = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeView.setColorSchemeColors(Color.RED);
        mSwipeView.setOnRefreshListener(this);
        mSwipeView.setDistanceToTriggerSync(300);

        mRvRepliesView = (RecyclerView) findViewById(R.id.reply_recycler_view);
        mRvRepliesView.setLayoutManager(new WrapLinearLayoutManager(this));
        mRepliesAdapter = new ReplyAdapter(this, mReplyList);
        mRepliesAdapter.setOnItemClickListener(this);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                    mTopic = V2EXHtmlParser.parseTopic(response.body());
                    renderContent(mTopic);
                    List<Reply> replyList = V2EXHtmlParser.parseReply(response.body());
                    mReplyList.clear();
                    mReplyList.addAll(replyList);
                    mRepliesAdapter.notifyItemRangeInserted(0, mReplyList.size());

                } else {
                    //TODO
                }
                mSwipeView.setRefreshing(false);
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

    private View.OnClickListener mReplyBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String replyContent = mEtReplyContent.getText().toString();

            if (TextUtils.isEmpty(replyContent)) {
                Toast.makeText(TopicDetailActivity.this, "回复内容不能为空!", Toast.LENGTH_SHORT).show();
                mEtReplyContent.requestFocus();
            } else {
                Retrofit retrofit = V2EXHttpClient.retrofit(TopicDetailActivity.this);
                V2EXApiService apiService = retrofit.create(V2EXApiService.class);
                Map<String, String> params = new HashMap<>();
                params.put("once", mTopic.getOnce());
                params.put("content", replyContent);
                Call<String> call = apiService.commentTopic(mTopicId, params);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response != null && response.isSuccessful()) {
                            Reply reply = new Reply();
                            Member member = new Member();
                            reply.setMember(member);
                            reply.setContentRendered(mEtReplyContent.getText().toString());
                            UserProfile userProfile = UserCache.getUser(TopicDetailActivity.this);
                            member.setUsername(userProfile.getUsername());
                            member.setAvatarNormal(userProfile.getAvatar());
                            int pos = mReplyList.size();
                            mReplyList.add(reply);
                            mRepliesAdapter.notifyItemInserted(pos);

                            mEtReplyContent.setText("");
                            mEtReplyContent.clearFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService
                                    (Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(mEtReplyContent.getWindowToken(), 0);
                        } else {
                            // TODO
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });

            }
        }
    };

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
    public void onRefresh() {
        fetchTopic(mTopicId);
    }

    @Override
    public void onItemClick(int pos) {
        startActivity(UserInfoActivity.newIntent(this, mReplyList.get(pos).getMember()));
    }

    class ReplyAdapter extends BaseRecyclerAdapter<Reply> implements View.OnClickListener {

        public ReplyAdapter(Context context, List<Reply> replyList) {
            super(context, replyList);
        }

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
            viewHolder.setOnClickListener(R.id.reply, this);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
            holder.getTextView(R.id.reply).setTag(holder.getLayoutPosition());
        }

        @Override
        public void bindData(RecyclerViewHolder viewHolder, Reply data, int position) {
            CircleImageView ciAvatar = viewHolder.getCircleImageView(R.id.reply_avatar);
            TextView tvAuthor = viewHolder.getTextView(R.id.reply_author);
            TextView tvContent = viewHolder.getTextView(R.id.reply_content);

            Glide.with(TopicDetailActivity.this).load("http:" + data.getMember().getAvatarNormal
                    ()).into(ciAvatar);
            tvAuthor.setText(data.getMember().getUsername());
            V2EXImageGetter imageGetter = new V2EXImageGetter(tvContent,
                    TopicDetailActivity.this);
            tvContent.setText(Html.fromHtml(data.getContentRendered(), imageGetter, null));
            tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        }

        @Override
        public int getLayoutResId() {
            return R.layout.item_topic_reply;
        }

        @Override
        public void onClick(View v) {
            if (v.getTag() instanceof Integer) {
                int pos = (Integer) v.getTag();
                String reply = "@" + dataList.get(pos).getMember().getUsername();
                mEtReplyContent.setText(reply);
                mEtReplyContent.setSelection(reply.length());
                mEtReplyContent.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            }
        }
    }
}


