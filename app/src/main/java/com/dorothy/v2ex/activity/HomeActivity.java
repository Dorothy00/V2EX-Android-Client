package com.dorothy.v2ex.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.dorothy.v2ex.R;
import com.dorothy.v2ex.fragment.TopicFragment;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TOPICFRAGMENT_TAG = "topic";
    public static final String PROFILEFRAGMENT_TAG = "profile";

    private TextView mTvHome;
    private TextView mTvNode;
    private TextView mTvMy;
    private TopicFragment mTopicFragment;
    private ProfileFragment profileFragment;
    private Fragment mCurFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvHome = (TextView) findViewById(R.id.home);
        mTvNode = (TextView) findViewById(R.id.node);
        mTvMy = (TextView) findViewById(R.id.my);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (mTopicFragment == null) {
            mTopicFragment = TopicFragment.newInstance();
        }
        mCurFragment = mTopicFragment;
        ft.add(R.id.fragment, mTopicFragment, TOPICFRAGMENT_TAG);
        ft.commit();


        mTvHome.setOnClickListener(this);
        mTvNode.setOnClickListener(this);
        mTvMy.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (v.getId() == R.id.home) {
            if (mTopicFragment == null) {
                mTopicFragment = TopicFragment.newInstance();
                ft.hide(mCurFragment);
                ft.add(R.id.fragment, mTopicFragment, TOPICFRAGMENT_TAG);
                ft.commit();
            } else if (mTopicFragment.isAdded() && mCurFragment != mTopicFragment) {
                ft.hide(mCurFragment);
                ft.show(mTopicFragment);
                ft.commit();
            }
            mCurFragment = mTopicFragment;
        } else if (v.getId() == R.id.node || v.getId() == R.id.my) {
            if (profileFragment == null) {
                profileFragment = new ProfileFragment();
                ft.hide(mCurFragment);
                ft.add(R.id.fragment, profileFragment, PROFILEFRAGMENT_TAG);
                ft.commit();
            } else if (profileFragment.isAdded() && mCurFragment != profileFragment) {
                ft.hide(mCurFragment);
                ft.show(profileFragment);
                ft.commit();
            }
            mCurFragment = profileFragment;
        }
    }
}