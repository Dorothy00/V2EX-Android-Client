package com.dorothy.v2ex.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;
import android.widget.Toast;

import com.dorothy.v2ex.R;
import com.dorothy.v2ex.View.WrapLinearLayoutManager;
import com.dorothy.v2ex.activity.TopicDetailActivity;
import com.dorothy.v2ex.adapter.BaseRecyclerAdapter;
import com.dorothy.v2ex.adapter.TopicsAdapter;
import com.dorothy.v2ex.http.NetWorkUnavaliableException;
import com.dorothy.v2ex.http.V2EXHttpClient;
import com.dorothy.v2ex.http.V2EXSubscriberAdapter;
import com.dorothy.v2ex.models.Topic;
import com.dorothy.v2ex.utils.V2EXHtmlParser;

import java.util.ArrayList;
import java.util.List;

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
    public static final String TOPIC_Collected = "collected";

    private RecyclerView mRvTopicsView;
    private List<Topic> mTopicList = new ArrayList<>();
    private TopicsAdapter mTopicsAdapter;
    private SwipeRefreshLayout mSwipeView;
    private ViewStub mVbError;
    private TextView mTvError;
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
        mVbError = (ViewStub) view.findViewById(R.id.error_view);
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
        V2EXHttpClient.getTopicByAPI(getActivity(), type, new V2EXSubscriberAdapter<List<Topic>>
                (getActivity()) {
            @Override
            public void onError(Throwable e) {
                handleError(e);
            }

            @Override
            public void onNext(List<Topic> topics) {
                mSwipeView.setRefreshing(false);
                renderContent(topics);
            }
        });
    }

    private void fetchTopicsByTag(String type) {

        V2EXHttpClient.getTopicsByTab(getActivity(), type, new V2EXSubscriberAdapter<String>
                (getActivity()) {
            @Override
            public void onError(Throwable e) {
                handleError(e);
            }

            @Override
            public void onNext(String s) {
                if (mTvError != null && mTvError.getVisibility() == View.VISIBLE) {
                    mTvError.setVisibility(View.GONE);
                }
                List<Topic> topicList = V2EXHtmlParser.parseTopicList(s, V2EXHtmlParser.FROM_TAB);
                renderContent(topicList);
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

    private void handleError(Throwable e) {
        mSwipeView.post(new Runnable() {
            @Override
            public void run() {
                mSwipeView.setRefreshing(false);
            }
        });
        if (mTvError == null) {
            mTvError = (TextView) mVbError.inflate();
        } else {
            mTvError.setVisibility(View.VISIBLE);
        }
    }
}
