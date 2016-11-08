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
import com.dorothy.v2ex.models.Node;

import java.util.ArrayList;
import java.util.List;

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

        V2EXHttpClient.getCollectedNodes(this, new V2EXSubscriberAdapter<List<Node>>(this) {
            @Override
            public void onNext(List<Node> nodes) {
                super.onNext(nodes);
                mNodeList.clear();
                mNodeList.addAll(nodes);
                mAdapter.notifyItemRangeInserted(0, mNodeList.size());
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
