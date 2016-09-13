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
import com.dorothy.v2ex.models.Node;
import com.dorothy.v2ex.utils.V2EXHtmlParser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CollectedNodesActivity extends AppCompatActivity implements SwipeRefreshLayout
        .OnRefreshListener, BaseRecyclerAdapter.OnItemClickListener {

    private SwipeRefreshLayout mSwipeView;
    private RecyclerView mRecyclerView;
    private NodeRecyclerAdapter mAdapter;
    private List<Node> mNodeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collected_nodes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("收藏节点");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSwipeView = (SwipeRefreshLayout) findViewById(R.id.swipe_view);
        mSwipeView.setColorSchemeColors(Color.RED);
        mSwipeView.setOnRefreshListener(this);
        mSwipeView.setDistanceToTriggerSync(300);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new WrapLinearLayoutManager(this));
        mAdapter = new NodeRecyclerAdapter(this, mNodeList);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

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
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;

        }

        return true;
    }

    private void fetchCollectedNodes() {
        Retrofit retrofit = V2EXHttpClient.retrofit(this);
        V2EXApiService apiService = retrofit.create(V2EXApiService.class);
        Call<String> call = apiService.getCollectedNodes();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null && response.isSuccessful()) {
                    List<Node> nodeList = V2EXHtmlParser.parseCollectedNode(response.body());
                    mNodeList.clear();
                    mNodeList.addAll(nodeList);
                    mAdapter.notifyItemRangeInserted(0, mNodeList.size());
                } else {
                    //TODO
                }
                mSwipeView.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //TODO
                mSwipeView.setRefreshing(false);
            }
        });

    }

    @Override
    public void onRefresh() {
        fetchCollectedNodes();
    }

    @Override
    public void onItemClick(int pos) {
        if (pos < 0 || pos > mNodeList.size() - 1)
            return;
        Node node = mNodeList.get(pos);
        startActivity(NodeTopicsActivity.newIntent(this, node.getName(), node.getTitle()));
    }


    class NodeRecyclerAdapter extends BaseRecyclerAdapter<Node> {

        public NodeRecyclerAdapter(Context context, List<Node> dataList) {
            super(context, dataList);
        }

        @Override
        public void bindData(RecyclerViewHolder viewHolder, Node data, int position) {
            CircleImageView ivNodeimg = viewHolder.getCircleImageView(R.id.node_logo);
            TextView ivNodeName = viewHolder.getTextView(R.id.node_name);
            TextView ivTopicNums = viewHolder.getTextView(R.id.topic_nums);

            Glide.with(context).load("http:" + data.getImgUrl()).override(30, 30).into(ivNodeimg);
            ivNodeName.setText(data.getTitle());
            ivTopicNums.setText(data.getTopics() + "");
        }

        @Override
        public int getLayoutResId() {
            return R.layout.item_collected_node;
        }
    }
}
