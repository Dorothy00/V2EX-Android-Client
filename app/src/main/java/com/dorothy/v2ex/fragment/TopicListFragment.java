package com.dorothy.v2ex.fragment;


import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dorothy.v2ex.R;
import com.dorothy.v2ex.View.WrapLinearLayoutManager;
import com.dorothy.v2ex.activity.TopicDetailActivity;
import com.dorothy.v2ex.adapter.BaseRecyclerAdapter;
import com.dorothy.v2ex.adapter.TopicsAdapter;
import com.dorothy.v2ex.http.V2EXApiService;
import com.dorothy.v2ex.http.V2EXHttpClient;
import com.dorothy.v2ex.models.Topic;
import com.dorothy.v2ex.utils.V2EXHtmlParser;
import com.dorothy.v2ex.utils.V2EXStringConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class TopicListFragment extends Fragment implements BaseRecyclerAdapter.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {
    public static final String TOPIC_TYPE = "topic_type";
    public static final String TOPIC_HOT = "hot";
    public static final String TOPIC_LATEST = "latest";
    public static final String TOPIC_APPLE = "apple";
    public static final String TOPIC_TECH = "tech";
    public static final String TOPIC_CREATIVE = "creative";
    public static final String TOPIC_JOB = "jobs";
    public static final String TOPIC_PLAY = "play";
    public static final String TOPIC_DEAL = "deals";
    public static final String TOPIC_CITY = "city";
    public static final String TOPIC_QNA = "qna";
    public static final String TOPIC_ALL = "all";
    public static final String TOPIC_R2 = "R2";
    public static final String TOPIC_FOCUS = "member";

    private RecyclerView mRvTopicsView;
    private List<Topic> mTopicList = new ArrayList<>();
    private TopicsAdapter mTopicsAdapter;
    private SwipeRefreshLayout mSwipeView;
    private String mType;
    private boolean mIsFirstTime;

    public static TopicListFragment newInstance(String type) {
        Bundle bundle = new Bundle();
        bundle.putString(TOPIC_TYPE, type);
        TopicListFragment fragment = new TopicListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topic_list, container, false);
        mRvTopicsView = (RecyclerView) view.findViewById(R.id.topic_list);
        mSwipeView = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mRvTopicsView.setLayoutManager(new WrapLinearLayoutManager(getActivity()));
        mTopicsAdapter = new TopicsAdapter(getActivity(), mTopicList);
        mTopicsAdapter.setOnItemClickListener(this);
        mRvTopicsView.setAdapter(mTopicsAdapter);
        mSwipeView.setDistanceToTriggerSync(300);
        mSwipeView.setColorSchemeColors(Color.RED);
        mSwipeView.setOnRefreshListener(this);
        mSwipeView.post(new Runnable() {
            @Override
            public void run() {
                mIsFirstTime = true;
                mSwipeView.setRefreshing(true);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mType = getArguments().getString(TOPIC_TYPE);
        onRefresh();
    }


    private void fetchTopicsByAPI(String type) {
        Retrofit retrofit = V2EXHttpClient.retrofit(getActivity());
        V2EXApiService apiService = retrofit.create(V2EXApiService.class);
        Call<List<Topic>> call = null;

        switch (type) {
            case TOPIC_LATEST:
                call = apiService.getLatestTopics();
                break;
            case TOPIC_HOT:
                call = apiService.getHotTopics();
                break;
        }

        call.enqueue(new Callback<List<Topic>>() {
            @Override
            public void onResponse(Call<List<Topic>> call, Response<List<Topic>> response) {
                if (response != null && response.isSuccessful()) {
                    renderContent(response.body());
                } else {
                    Toast.makeText(getActivity(), getString(R.string.http_error), Toast
                            .LENGTH_SHORT).show();
                }
                mSwipeView.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Topic>> call, Throwable t) {
                if (t != null) {
                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                }
                if (mSwipeView.isRefreshing())
                    mSwipeView.setRefreshing(false);
            }
        });

    }

    private void fetchTopicsByTag(String type) {
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(V2EXApiService.BASE_URL)
                .addConverterFactory(V2EXStringConverter.create()).build();

        V2EXApiService apiService = retrofit.create(V2EXApiService.class);
        Call<String> call = apiService.getTopicsByTab(type);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null && response.isSuccessful()) {
                    String htmlStr = response.body();
                    List<Topic> topicList = V2EXHtmlParser.parseTopicList(htmlStr);
                    renderContent(topicList);
                } else {
                    try {
                        Toast.makeText(getActivity(), response.errorBody().string(), Toast
                                .LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                mSwipeView.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (t != null) {
                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                }
                if (mSwipeView.isRefreshing())
                    mSwipeView.setRefreshing(false);
            }
        });
    }

    private void renderContent(List<Topic> topicList) {
        mTopicList.clear();
        mTopicList.addAll(topicList);

        if (mSwipeView.isRefreshing() && !mIsFirstTime) {
            mTopicsAdapter.notifyItemRangeChanged(0, mTopicList.size());
        } else {
            mTopicsAdapter.notifyItemRangeInserted(0, mTopicList.size());
        }
        mIsFirstTime = false;
    }


    @Override
    public void onRefresh() {
        if (mType.equals(TOPIC_HOT) | mType.equals(TOPIC_LATEST)) {
            fetchTopicsByAPI(mType);
        } else {
            fetchTopicsByTag(mType);
        }
    }

    @Override
    public void onItemClick(int pos) {
        Topic topic = mTopicList.get(pos);
        startActivity(TopicDetailActivity.newIntent(getActivity(), topic.getId()));
    }
}
