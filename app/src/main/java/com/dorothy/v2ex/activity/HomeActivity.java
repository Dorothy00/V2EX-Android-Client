package com.dorothy.v2ex.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.dorothy.v2ex.R;
import com.dorothy.v2ex.fragment.AllNodeFragment;
import com.dorothy.v2ex.fragment.ProfileFragment;
import com.dorothy.v2ex.fragment.TopicFragment;
import com.dorothy.v2ex.models.UserProfile;
import com.dorothy.v2ex.utils.UserCache;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mIvHome;
    private ImageView mIvNode;
    private ImageView mIvMy;
    private TopicFragment mTopicFragment;
    private ProfileFragment mProfileFragment;
    private AllNodeFragment mAllNodeFragment;
    private Fragment mCurFragment;
    private Toolbar mToolbar;
    private UserProfile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("首页");
        setSupportActionBar(mToolbar);

        mIvHome = (ImageView) findViewById(R.id.home);
        mIvNode = (ImageView) findViewById(R.id.node);
        mIvMy = (ImageView) findViewById(R.id.my);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (mTopicFragment == null) {
            mTopicFragment = new TopicFragment();
        }
        mCurFragment = mTopicFragment;
        ft.add(R.id.fragment, mTopicFragment);
        ft.commit();
        mIvHome.setBackgroundResource(R.drawable.ic_home_primary);


        mIvHome.setOnClickListener(this);
        mIvNode.setOnClickListener(this);
        mIvMy.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        int id = v.getId();
        if (id == R.id.home) {
            if (mTopicFragment == null) {
                mTopicFragment = new TopicFragment();
                ft.hide(mCurFragment);
                ft.add(R.id.fragment, mTopicFragment);
            } else if (mTopicFragment.isAdded() && mCurFragment != mTopicFragment) {
                ft.hide(mCurFragment);
                ft.show(mTopicFragment);
            }
            mCurFragment = mTopicFragment;
            getSupportActionBar().setTitle("首页");
        } else if (id == R.id.my) {
            if (mProfileFragment == null) {
                mProfileFragment = new ProfileFragment();
                ft.hide(mCurFragment);
                ft.add(R.id.fragment, mProfileFragment);
                userProfile = UserCache.getUser(this);
            } else if (mProfileFragment.isAdded() && mCurFragment != mProfileFragment) {
                ft.hide(mCurFragment);
                ft.show(mProfileFragment);
            }
            getSupportActionBar().setTitle(userProfile.getUsername());
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
            getSupportActionBar().setTitle("节点");
            mCurFragment = mAllNodeFragment;
        }
        showIcon(id);
        ft.commit();
    }

    private void showIcon(int id){
        mIvHome.setBackgroundResource(R.drawable.ic_home_grey);
        mIvNode.setBackgroundResource(R.drawable.ic_node_grey);
        mIvMy.setBackgroundResource(R.drawable.ic_user_grey);

        switch (id){
           case R.id.home:
               mIvHome.setBackgroundResource(R.drawable.ic_home_primary);
               break;
            case R.id.node:
                mIvNode.setBackgroundResource(R.drawable.ic_node_primary);
                break;
            case R.id.my:
                mIvMy.setBackgroundResource(R.drawable.ic_user_primary);
                break;
        }
    }
}