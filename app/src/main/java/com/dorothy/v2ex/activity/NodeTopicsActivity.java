package com.dorothy.v2ex.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dorothy.v2ex.R;
import com.dorothy.v2ex.View.WrapLinearLayoutManager;
import com.dorothy.v2ex.adapter.BaseRecyclerAdapter;
import com.dorothy.v2ex.adapter.TopicsAdapter;
import com.dorothy.v2ex.http.V2EXApiService;
import com.dorothy.v2ex.http.V2EXHttpClient;
import com.dorothy.v2ex.models.Topic;
import com.dorothy.v2ex.utils.V2EXHtmlParser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Subscriber;

public class NodeTopicsActivity extends AppCompatActivity implements BaseRecyclerAdapter
        .OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mTopicsRecyclerView;
    private SwipeRefreshLayout mSwipeView;
    private List<Topic> mTopicList = new ArrayList<>();
    private TopicsAdapter mAdapter;
    private Toolbar mToolbar;
    private String mNodeName;
    private boolean mIsCollected;
    private String mCollectUrl;

    public static Intent newIntent(Activity activity, String nodeName, String nodeTitle) {
        Intent intent = new Intent(activity, NodeTopicsActivity.class);
        intent.putExtra("node_name", nodeName);
        intent.putExtra("node_title", nodeTitle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node_topics);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSwipeView = (SwipeRefreshLayout) findViewById(R.id.swipe_view);
        mSwipeView.setColorSchemeColors(Color.RED);
        mSwipeView.setOnRefreshListener(this);
        mSwipeView.setDistanceToTriggerSync(300);

        mTopicsRecyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        mTopicsRecyclerView.setLayoutManager(new WrapLinearLayoutManager(this));
        mAdapter = new TopicsAdapter(this, mTopicList);
        mAdapter.setOnItemClickListener(this);
        mTopicsRecyclerView.setAdapter(mAdapter);

        Intent intent = getIntent();
        mNodeName = intent.getStringExtra("node_name");
        if (!TextUtils.isEmpty(mNodeName)) {
            mToolbar.setTitle(intent.getStringExtra("node_title"));
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!TextUtils.isEmpty(mCollectUrl)) {
            boolean isCollected = isCollected(mCollectUrl);
            MenuItem menuItem = menu.findItem(R.id.action_collect);
            if (isCollected) {
                menuItem.setIcon(R.drawable.ic_collect_red);
            } else {
                menuItem.setIcon(R.drawable.ic_collect);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_collect:
                collectNode();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_node, menu);
        return true;
    }


    private void collectNode() {
        if (TextUtils.isEmpty(mCollectUrl))
            return;

        int index = mCollectUrl.indexOf("=");
        String once = mCollectUrl.substring(index + 1);
        Retrofit retrofit = V2EXHttpClient.stringRetrofit(this);
        V2EXApiService apiService = retrofit.create(V2EXApiService.class);
        Call<String> call = apiService.collectNode(mCollectUrl, once);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null && response.isSuccessful()) {
                    String htmlstr = response.body();
                    mCollectUrl = V2EXHtmlParser.parseCollectUrl(htmlstr);
                    invalidateOptionsMenu();
                } else {
                    Toast.makeText(NodeTopicsActivity.this, "收藏失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(NodeTopicsActivity.this, getString(R.string.http_error), Toast
                        .LENGTH_SHORT).show();
            }
        });
    }

    private void fetchTopics(String nodeName) {
        V2EXHttpClient.getTopicsByNode(this, nodeName, new Subscriber<List<Topic>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mSwipeView.setRefreshing(false);
            }

            @Override
            public void onNext(List<Topic> topics) {
                mTopicList.clear();
                mTopicList.addAll(topics);
                mAdapter.notifyItemRangeInserted(0, mTopicList.size());

                //   mCollectUrl = V2EXHtmlParser.parseCollectUrl(response.body());
                invalidateOptionsMenu();
                mSwipeView.setRefreshing(false);
            }
        });
    }

    private boolean isCollected(String collectUrl) {
        if (collectUrl.startsWith("/favorite"))
            return false;
        else if (collectUrl.startsWith("/unfavorite"))
            return true;
        return false;
    }

    @Override
    public void onItemClick(int pos) {
        Topic topic = mTopicList.get(pos);
        startActivity(TopicDetailActivity.newIntent(this, topic.getId()));
    }

    @Override
    public void onRefresh() {
        fetchTopics(mNodeName);
    }
}
