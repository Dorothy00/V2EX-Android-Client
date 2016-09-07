package com.dorothy.v2ex.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.dorothy.v2ex.R;
import com.dorothy.v2ex.fragment.AllNodeFragment;
import com.dorothy.v2ex.fragment.TopicFragment;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTvHome;
    private TextView mTvNode;
    private TextView mTvMy;
    private TopicFragment mTopicFragment;
    private ProfileFragment mProfileFragment;
    private AllNodeFragment mAllNodeFragment;
    private Fragment mCurFragment;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

        mTvHome = (TextView) findViewById(R.id.home);
        mTvNode = (TextView) findViewById(R.id.node);
        mTvMy = (TextView) findViewById(R.id.my);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (mTopicFragment == null) {
            mTopicFragment = TopicFragment.newInstance();
        }
        mCurFragment = mTopicFragment;
        ft.add(R.id.fragment, mTopicFragment);
        ft.commit();


        mTvHome.setOnClickListener(this);
        mTvNode.setOnClickListener(this);
        mTvMy.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        int id = v.getId();
        if (id == R.id.home) {
            if (mTopicFragment == null) {
                mTopicFragment = TopicFragment.newInstance();
                ft.hide(mCurFragment);
                ft.add(R.id.fragment, mTopicFragment);
            } else if (mTopicFragment.isAdded() && mCurFragment != mTopicFragment) {
                ft.hide(mCurFragment);
                ft.show(mTopicFragment);
            }
            mCurFragment = mTopicFragment;
        } else if (id == R.id.my) {
            if (mProfileFragment == null) {
                mProfileFragment = new ProfileFragment();
                ft.hide(mCurFragment);
                ft.add(R.id.fragment, mProfileFragment);
            } else if (mProfileFragment.isAdded() && mCurFragment != mProfileFragment) {
                ft.hide(mCurFragment);
                ft.show(mProfileFragment);
            }
            mCurFragment = mProfileFragment;
        } else if (id == R.id.node) {
            if (mAllNodeFragment == null) {
                mAllNodeFragment = new AllNodeFragment();
                ft.hide(mCurFragment);
                ft.add(R.id.fragment, mAllNodeFragment);
            } else if (mAllNodeFragment.isAdded() && mCurFragment != mAllNodeFragment) {
                ft.hide(mCurFragment);
                ft.show(mAllNodeFragment);
            }
            mCurFragment = mAllNodeFragment;
        }
        ft.commit();
    }
}