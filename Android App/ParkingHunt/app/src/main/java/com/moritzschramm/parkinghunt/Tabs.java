package com.moritzschramm.parkinghunt;

import android.app.Activity;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Moritz on 12.08.2016.
 */
public class Tabs implements NavbarClickHandler.ClickInterface {

    public static final int TAB_ONE = 0;
    public static final int TAB_TWO = 1;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PageListener pageListener;

    private LastSearchesFragment lastSearchesFragment;
    private OwnSpotsListFragment ownSpotsListFragment;

    public interface PageListener {
        void onChange(int position);
    }

    public Tabs(Activity activity, Toolbar toolbar, TabLayout tabLayout, ViewPager viewPager) {

        pageListener = (PageListener) activity;

        this.toolbar = toolbar;
        this.tabLayout = tabLayout;
        this.viewPager = viewPager;

        lastSearchesFragment = new LastSearchesFragment();
        ownSpotsListFragment = new OwnSpotsListFragment();
    }
    public void init(FragmentManager fragmentManager) {

        setupViewPager(viewPager, fragmentManager);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {

                pageListener.onChange(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }
    public int getCurrentTab() {

        return viewPager.getCurrentItem();
    }

    public void updateOwnSpots() {
        ownSpotsListFragment.notifyDataSetChanged();
    }

    private void setupViewPager(ViewPager viewPager, FragmentManager fragmentManager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(fragmentManager);
        adapter.addFragment(lastSearchesFragment, "SUCHEN");
        adapter.addFragment(ownSpotsListFragment, "EIGENE");
        viewPager.setAdapter(adapter);
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
