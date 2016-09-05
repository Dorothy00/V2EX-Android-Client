package com.dorothy.v2ex.fragment;


import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dorothy.v2ex.R;
import com.viewpagerindicator.TabPageIndicator;

public class TopicFragment extends Fragment {


    private ViewPager mViewpager;
    private TabPageIndicator mTabPageIndicator;

    public static TopicFragment newInstance() {
        TopicFragment fragment = new TopicFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_topic, container, false);
        mViewpager = (ViewPager) root.findViewById(R.id.view_pager);
        mTabPageIndicator = (TabPageIndicator) root.findViewById(R.id.page_indicator);
        TopicTabPagerAdapter adapter = new TopicTabPagerAdapter(getActivity().getFragmentManager());
        mViewpager.setAdapter(adapter);
        mTabPageIndicator.setViewPager(mViewpager);
        return root;
    }




        class TopicTabPagerAdapter extends FragmentPagerAdapter {
        private final String[] CONTENT = new String[]{"技术", "创意", "好玩", "Apple", "酷工作", "交易",
                "城市", "问与答", "最热", "全部", "R2", "关注"};

        public TopicTabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return TopicListFragment.newInstance(TopicListFragment.TOPIC_TECH);
                case 1:
                    return TopicListFragment.newInstance(TopicListFragment.TOPIC_CREATIVE);
                case 2:
                    return TopicListFragment.newInstance(TopicListFragment.TOPIC_PLAY);
                case 3:
                    return TopicListFragment.newInstance(TopicListFragment.TOPIC_APPLE);
                case 4:
                    return TopicListFragment.newInstance(TopicListFragment.TOPIC_JOB);
                case 5:
                    return TopicListFragment.newInstance(TopicListFragment.TOPIC_DEAL);
                case 6:
                    return TopicListFragment.newInstance(TopicListFragment.TOPIC_CITY);
                case 7:
                    return TopicListFragment.newInstance(TopicListFragment.TOPIC_QNA);
                case 8:
                    return TopicListFragment.newInstance(TopicListFragment.TOPIC_HOT);
                case 9:
                    return TopicListFragment.newInstance(TopicListFragment.TOPIC_ALL);
                case 10:
                    return TopicListFragment.newInstance(TopicListFragment.TOPIC_R2);
                case 11:
                    return TopicListFragment.newInstance(TopicListFragment.TOPIC_FOCUS);
            }
            return null;
        }

        @Override
        public int getCount() {
            return CONTENT.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length].toUpperCase();
        }
    }
}
