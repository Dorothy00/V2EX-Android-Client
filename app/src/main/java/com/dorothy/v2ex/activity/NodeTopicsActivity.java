package com.dorothy.v2ex.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;

import com.dorothy.v2ex.R;
import com.dorothy.v2ex.View.WrapLinearLayoutManager;
import com.dorothy.v2ex.adapter.BaseRecyclerAdapter;
import com.dorothy.v2ex.http.V2EXApiService;
import com.dorothy.v2ex.models.Topic;
import com.dorothy.v2ex.adapter.TopicsAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NodeTopicsActivity extends AppCompatActivity implements BaseRecyclerAdapter
        .OnItemClickListener {

    private RecyclerView mTopicsRecyclerView;
    private List<Topic> mTopicList = new ArrayList<>();
    private TopicsAdapter mAdapter;
    private Toolbar mToolbar;

    public static Intent newIntent(Activity activity, String nodeName) {
        Intent intent = new Intent(activity, NodeTopicsActivity.class);
        intent.putExtra("node_name", nodeName);
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

        mTopicsRecyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        mTopicsRecyclerView.setLayoutManager(new WrapLinearLayoutManager(this));
        mAdapter = new TopicsAdapter(this, mTopicList);
        mAdapter.setOnItemClickListener(this);
        mTopicsRecyclerView.setAdapter(mAdapter);

        Intent intent = getIntent();
        String nodeName = intent.getStringExtra("node_name");
        if (!TextUtils.isEmpty(nodeName)) {
            mToolbar.setTitle(nodeName);
            fetchTopics(nodeName);
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

    private void fetchTopics(String nodeName) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(V2EXApiService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        V2EXApiService apiService = retrofit.create(V2EXApiService.class);
        Call<List<Topic>> call = apiService.getTopicsByNode(nodeName);
        call.enqueue(new Callback<List<Topic>>() {
            @Override
            public void onResponse(Call<List<Topic>> call, Response<List<Topic>> response) {
                if (response != null && response.isSuccessful()) {
                    mTopicList.clear();
                    mTopicList.addAll(response.body());
                    mAdapter.notifyItemRangeInserted(0, mTopicList.size());
                }
            }

            @Override
            public void onFailure(Call<List<Topic>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onItemClick(int pos) {
        Topic topic = mTopicList.get(pos);
        startActivity(TopicDetailActivity.newIntent(this, topic.getId()));
    }

}
