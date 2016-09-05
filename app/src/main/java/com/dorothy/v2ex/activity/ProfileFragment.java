package com.dorothy.v2ex.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dorothy.v2ex.R;
import com.dorothy.v2ex.View.CircleImageView;
import com.dorothy.v2ex.models.UserProfile;
import com.dorothy.v2ex.utils.UserCache;

public class ProfileFragment extends Fragment {

    private Toolbar mToolbar;
    private CircleImageView mCiAvatar;
    private TextView mTvUsername;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        mToolbar = (Toolbar) root.findViewById(R.id.toolbar);
        mToolbar.setTitle("");

        mCiAvatar = (CircleImageView) root.findViewById(R.id.avatar);
        mTvUsername = (TextView) root.findViewById(R.id.username);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        mToolbar.setTitle("test");
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        UserProfile userProfile = UserCache.getUser(getActivity());
        if (userProfile != null) {
            Glide.with(getActivity()).load("http:" + userProfile.getAvatar()).into(mCiAvatar);
            mTvUsername.setText(userProfile.getUsername());
        }
    }
}
