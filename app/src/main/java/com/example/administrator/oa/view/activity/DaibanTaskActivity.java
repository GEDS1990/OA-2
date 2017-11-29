package com.example.administrator.oa.view.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.oa.R;
import com.example.administrator.oa.view.fragment.daibantasfragments.CaoGaoXiangFragment;
import com.example.administrator.oa.view.fragment.daibantasfragments.daiBanFragment;
import com.example.administrator.oa.view.fragment.daibantasfragments.yiBanFragment;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/6/27.
 */

public class DaibanTaskActivity extends HeadBaseActivity implements OnTabSelectListener {

    @BindView(R.id.tablayout)
    SlidingTabLayout mTablayout;
    @BindView(R.id.vp_2)
    ViewPager mVp2;

    private String[] mTitles = {"待办", "草稿箱"};

    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();

    @Override
    protected int getChildLayoutRes() {
        return R.layout.activity_daibantask;
    }

    @Override
    protected void initView(RelativeLayout headView, RelativeLayout backBtn, RelativeLayout headerCenter,
                            RelativeLayout headerRight, View childView, LinearLayout statubar) {
        ((TextView) headerCenter.getChildAt(0)).setText("待办任务");
        initThisView();
    }

    private void initThisView() {

        mFragments.add(daiBanFragment.getInstance("Switch ViewPager " + "代办"));
        mFragments.add(CaoGaoXiangFragment.getInstance("Switch ViewPager " + "草稿箱"));

        mTablayout.setViewPager(mVp2, mTitles, this, mFragments);
        mVp2.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mTablayout.setViewPager(mVp2);
        mVp2.setCurrentItem(0);
    }

    @Override
    public void onTabSelect(int position) {

    }

    @Override
    public void onTabReselect(int position) {

    }


    class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }


}
