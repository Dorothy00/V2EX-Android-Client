package com.dorothy.v2ex.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dorothy.v2ex.R;
import com.dorothy.v2ex.View.WrapStaggeredGridLayoutManager;
import com.dorothy.v2ex.activity.NodeTopicsActivity;
import com.dorothy.v2ex.adapter.BaseRecyclerAdapter;
import com.dorothy.v2ex.adapter.RecyclerViewHolder;
import com.dorothy.v2ex.http.V2EXApiService;
import com.dorothy.v2ex.models.NodeDetail;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AllNodeFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener, BaseRecyclerAdapter.OnItemClickListener {

    private RecyclerView mNodeRecyclerView;
    private List<NodeDetail> mNodeList = new ArrayList<>();
    private WrapStaggeredGridLayoutManager layoutManager;
    private SwipeRefreshLayout mSwipeView;
    private NodeAdapter mAdapter;

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
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setTitle("节点");
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

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

    private void fetchAllNode() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(V2EXApiService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        V2EXApiService apiService = retrofit.create(V2EXApiService.class);
        Call<List<NodeDetail>> call = apiService.getAllNodes();
        call.enqueue(new Callback<List<NodeDetail>>() {
            @Override
            public void onResponse(Call<List<NodeDetail>> call, Response<List<NodeDetail>>
                    response) {
                if (response != null && response.isSuccessful()) {
                    mNodeList.clear();
                    mNodeList.addAll(response.body());
                    mAdapter.notifyItemRangeInserted(0, mNodeList.size());
                    mNodeRecyclerView.setHasFixedSize(true);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.http_error), Toast
                            .LENGTH_SHORT).show();
                }
                mSwipeView.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<NodeDetail>> call, Throwable t) {
                Toast.makeText(getActivity(), getString(R.string.http_error), Toast
                        .LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh() {
        fetchAllNode();
    }

    @Override
    public void onItemClick(int pos) {
        NodeDetail nodeDetail = mNodeList.get(pos);
        startActivity(NodeTopicsActivity.newIntent(getActivity(), nodeDetail.getName()));
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
