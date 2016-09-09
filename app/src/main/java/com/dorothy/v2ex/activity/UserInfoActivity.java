package com.dorothy.v2ex.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dorothy.v2ex.R;
import com.dorothy.v2ex.View.CircleImageView;
import com.dorothy.v2ex.adapter.BaseRecyclerAdapter;
import com.dorothy.v2ex.adapter.TopicsAdapter;
import com.dorothy.v2ex.http.V2EXApiService;
import com.dorothy.v2ex.models.Member;
import com.dorothy.v2ex.models.MemberDetail;
import com.dorothy.v2ex.models.Topic;
import com.dorothy.v2ex.utils.URLSpanNoUnderline;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserInfoActivity extends AppCompatActivity implements BaseRecyclerAdapter
        .OnItemClickListener {

    private Member member;
    private MemberDetail memberDetail;
    private Toolbar mToolbar;
    private CircleImageView mCiAvatar;
    private TextView mTvV2exInfo;
    private TextView mTvHomePage;
    private TextView mTvGitHubPage;
    private ImageView mIvHomeArrow;
    private ImageView mIvGithubArrow;
    private RecyclerView mRvTopics;
    private TopicsAdapter mTopicsAdapter;
    private List<Topic> mTopicList = new ArrayList<Topic>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCiAvatar = (CircleImageView) findViewById(R.id.avatar);
        mTvV2exInfo = (TextView) findViewById(R.id.member_info);
        mTvHomePage = (TextView) findViewById(R.id.text_home);
        mTvGitHubPage = (TextView) findViewById(R.id.text_github);
        mIvHomeArrow = (ImageView) findViewById(R.id.home_arrow);
        mIvGithubArrow = (ImageView) findViewById(R.id.github_arrow);
        mRvTopics = (RecyclerView) findViewById(R.id.topics);
        mTopicsAdapter = new TopicsAdapter(this, mTopicList);
        mTopicsAdapter.setOnItemClickListener(this);


        Intent intent = getIntent();
        member = (Member) intent.getSerializableExtra("member");
        if (member == null)
            return;

        /*
         * 1.Html 解析只能得到 avatar normal
         * 2.Html 解析得不到 member id
         */
        if (TextUtils.isEmpty(member.getAvatarLarge())) {
            Glide.with(this).load("http:" + member.getAvatarNormal()).into(mCiAvatar);
        } else {
            Glide.with(this).load("http:" + member.getAvatarLarge()).into(mCiAvatar);
        }
        if (member.getId() != null) {
            mTvV2exInfo.setText(getString(R.string.member_number).replace("{id}", member.getId()
                    + ""));

        }
        mToolbar.setTitle(member.getUsername());
        mRvTopics.setLayoutManager(new LinearLayoutManager(this));
        mRvTopics.setAdapter(mTopicsAdapter);

        fetchMemberDetail(member.getUsername());
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

    private void fetchMemberDetail(String username) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(V2EXApiService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        V2EXApiService apiService = retrofit.create(V2EXApiService.class);
        Call<MemberDetail> call = apiService.getMemberDetail(username);
        call.enqueue(new Callback<MemberDetail>() {
            @Override
            public void onResponse(Call<MemberDetail> call, Response<MemberDetail> response) {
                if (response!= null && response.isSuccessful()) {
                    memberDetail = response.body();
                    String homeInfo = memberDetail.getWebsite();
                    String githubInfo = memberDetail.getGithub();

                    if (!TextUtils.isEmpty(homeInfo)) {
                        SpannableString homeSpan = new SpannableString(homeInfo);
                        homeSpan.setSpan(new URLSpanNoUnderline(homeInfo), 0, homeInfo.length(),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mTvHomePage.setText(homeSpan);
                        mTvHomePage.setMovementMethod(LinkMovementMethod.getInstance());
                        mIvHomeArrow.setVisibility(View.VISIBLE);
                    } else {
                        mTvHomePage.setText(getString(R.string.no_settings));
                    }
                    if (!TextUtils.isEmpty(githubInfo)) {
                        SpannableString githubSpan = new SpannableString(githubInfo);
                        githubSpan.setSpan(new URLSpanNoUnderline("https://github.com/" +
                                githubInfo), 0, githubInfo.length(), Spanned
                                .SPAN_EXCLUSIVE_EXCLUSIVE);
                        mTvGitHubPage.setText(githubSpan);
                        mTvGitHubPage.setMovementMethod(LinkMovementMethod.getInstance());
                        mIvGithubArrow.setVisibility(View.VISIBLE);
                    } else {
                        mTvGitHubPage.setText(getString(R.string.no_settings));
                    }

                    // Html 解析得不到数据的情况
                    Glide.with(UserInfoActivity.this).load("http:" + memberDetail.getAvatarLarge())
                            .into(mCiAvatar);
                    mTvV2exInfo.setText(getString(R.string.member_number).replace("{id}",
                            memberDetail
                                    .getId()
                                    + ""));

                    fetchUserTopics(memberDetail.getUsername());
                } else {
                    Toast.makeText(UserInfoActivity.this, getString(R.string.http_error), Toast
                            .LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MemberDetail> call, Throwable t) {
                if (t != null) {
                    Toast.makeText((UserInfoActivity.this), t.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                }

            }
        });
    }

    private void fetchUserTopics(String username) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(V2EXApiService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        V2EXApiService apiService = retrofit.create(V2EXApiService.class);
        Call<List<Topic>> call = apiService.getTopicsByUsername(username);
        call.enqueue(new Callback<List<Topic>>() {
            @Override
            public void onResponse(Call<List<Topic>> call, Response<List<Topic>> response) {
                if (response.code() == 200) {
                    int start = mTopicList.size();
                    mTopicList.addAll(response.body());
                    mTopicsAdapter.notifyItemRangeInserted(start, mTopicList.size());
                } else {
                    Toast.makeText(UserInfoActivity.this, getString(R.string.http_error), Toast
                            .LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Topic>> call, Throwable t) {
                if (t != null) {
                    Toast.makeText((UserInfoActivity.this), t.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    @Override
    public void onItemClick(int pos) {
        Topic topic = mTopicList.get(pos);
        startActivity(TopicDetailActivity.newIntent(this, topic.getId()));
    }

    public static Intent newIntent(Activity activity, Member member) {
        Intent intent = new Intent(activity, UserInfoActivity.class);
        intent.putExtra("member", member);
        return intent;
    }
}
