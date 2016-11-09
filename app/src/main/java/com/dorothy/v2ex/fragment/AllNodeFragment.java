package com.dorothy.v2ex.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dorothy.v2ex.R;
import com.dorothy.v2ex.View.WrapStaggeredGridLayoutManager;
import com.dorothy.v2ex.activity.NodeTopicsActivity;
import com.dorothy.v2ex.adapter.BaseRecyclerAdapter;
import com.dorothy.v2ex.adapter.RecyclerViewHolder;
import com.dorothy.v2ex.http.V2EXHttpClient;
import com.dorothy.v2ex.http.V2EXSubscriberAdapter;
import com.dorothy.v2ex.models.NodeDetail;
import com.dorothy.v2ex.utils.FileUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class AllNodeFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener, BaseRecyclerAdapter.OnItemClickListener {

    private RecyclerView mNodeRecyclerView;
    private List<NodeDetail> mNodeList = new ArrayList<>();
    private WrapStaggeredGridLayoutManager layoutManager;
    private SwipeRefreshLayout mSwipeView;
    private NodeAdapter mAdapter;
    private boolean mIsFirstLoad = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_all_node, container, false);
        mNodeRecyclerView = (RecyclerView) root.findViewById(R.id.recycle_view);
        mSwipeView = (SwipeRefreshLayout) root.findViewById(R.id.swpie_view);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        layoutManager = new WrapStaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        mNodeRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new NodeAdapter(getActivity(), mNodeList);
        mAdapter.setOnItemClickListener(this);
        mNodeRecyclerView.setAdapter(mAdapter);

        mSwipeView.setDistanceToTriggerSync(300);
        mSwipeView.setColorSchemeColors(Color.RED);
        mSwipeView.setOnRefreshListener(this);
        mSwipeView.post(new Runnable() {
            @Override
            public void run() {
                mSwipeView.setRefreshing(true);
            }
        });
        onRefresh();
    }

    private void fetchAllNode(boolean isRefresh) {
        String nodesStr = (String) FileUtil.readObject(getActivity());
        if (!isRefresh && nodesStr != null) {
            mSwipeView.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeView.setRefreshing(false);
                }
            });
            List<NodeDetail> nodeDetails = new Gson().fromJson(nodesStr, new
                    TypeToken<List<NodeDetail>>() {
                    }.getType());
            renderView(nodeDetails);
            return;
        }

        V2EXHttpClient.getAllNodes(getActivity(), new V2EXSubscriberAdapter<List<NodeDetail>>
                (getActivity()) {
            @Override
            public void onNext(List<NodeDetail> nodeDetails) {
                renderView(nodeDetails);
                FileUtil.deleteObject(getActivity());
                FileUtil.saveObject(getActivity(), new Gson().toJson(nodeDetails));
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
        if (mIsFirstLoad) {
            mIsFirstLoad = false;
            fetchAllNode(false);
        } else {
            fetchAllNode(true);
        }

    }

    @Override
    public void onItemClick(int pos) {
        NodeDetail nodeDetail = mNodeList.get(pos);
        startActivity(NodeTopicsActivity.newIntent(getActivity(), nodeDetail.getName(),
                nodeDetail.getTitle()));
    }

    private void renderView(List<NodeDetail> nodeDetails) {
        mSwipeView.setRefreshing(false);
        mNodeList.clear();
        mNodeList.addAll(nodeDetails);
        mAdapter.notifyItemRangeInserted(0, mNodeList.size());
        mNodeRecyclerView.setHasFixedSize(true);
    }

    class NodeAdapter extends BaseRecyclerAdapter<NodeDetail> {

        public NodeAdapter(Context context, List<NodeDetail> nodeDetailList) {
            super(context, nodeDetailList);
        }

        @Override
        public void bindData(RecyclerViewHolder viewHolder, NodeDetail data, int position) {
            TextView tvTitle = viewHolder.getTextView(R.id.title);
            TextView tvTopics = viewHolder.getTextView(R.id.topics);
            TextView tvFooter = viewHolder.getTextView(R.id.footer);
            NodeDetail nodeDetail = dataList.get(position);
            tvTitle.setText(nodeDetail.getTitle());
            tvTopics.setText(nodeDetail.getTopics() + "个话题");
            if (!TextUtils.isEmpty(nodeDetail.getFooter())) {
                tvFooter.setText(Html.fromHtml(nodeDetail.getFooter()));
                tvFooter.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }

        @Override
        public int getLayoutResId() {
            return R.layout.item_node;
        }
    }

}
