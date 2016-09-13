package com.dorothy.v2ex.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dorothy.v2ex.R;
import com.dorothy.v2ex.View.CircleImageView;
import com.dorothy.v2ex.activity.CollectedNodesActivity;
import com.dorothy.v2ex.activity.NotificationActivity;
import com.dorothy.v2ex.models.UserProfile;
import com.dorothy.v2ex.utils.UserCache;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private CircleImageView mCiAvatar;
    private TextView mTvUsername;
    private TextView mTvNode;
    private TextView mTvTopic;
    private TextView mTvFollowing;
    private TextView mTvBalance;
    private TextView mTvNotification;
    private RelativeLayout mRlNoticationContainer;
    private RelativeLayout mRlNodeContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        mCiAvatar = (CircleImageView) root.findViewById(R.id.avatar);
        mTvUsername = (TextView) root.findViewById(R.id.username);
        mTvNode = (TextView) root.findViewById(R.id.node_count);
        mTvTopic = (TextView) root.findViewById(R.id.topic_count);
        mTvFollowing = (TextView) root.findViewById(R.id.following_count);
        mTvBalance = (TextView) root.findViewById(R.id.balance_count);
        mTvNotification = (TextView) root.findViewById(R.id.notification_count);
        mRlNoticationContainer = (RelativeLayout) root.findViewById(R.id.notification_container);
        mRlNodeContainer = (RelativeLayout) root.findViewById(R.id.node_container);

        mRlNoticationContainer.setOnClickListener(this);
        mRlNodeContainer.setOnClickListener(this);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        UserProfile userProfile = UserCache.getUser(getActivity());
        if (userProfile != null) {
            Glide.with(getActivity()).load("http:" + userProfile.getAvatar()).into(mCiAvatar);
            mTvUsername.setText(userProfile.getUsername());
            mTvNode.setText(userProfile.getCollectedNodes());
            mTvTopic.setText(userProfile.getCollectTopics());
            mTvFollowing.setText(userProfile.getFocusedTopics());
            mTvBalance.setText(userProfile.getBalance()[0] + " " + userProfile.getBalance()[1]);
            mTvNotification.setText(userProfile.getNotification());
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.notification_container:
                startActivity(new Intent(getActivity(), NotificationActivity.class));
                break;
            case R.id.node_container:
                startActivity(new Intent(getActivity(), CollectedNodesActivity.class));
                break;
        }
    }
}
