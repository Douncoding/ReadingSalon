package com.douncoding.readingsalon;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    Toolbar mToolbar;
    TabLayout mTabLayout;
    ViewPager mViewPager;
    ImageView mConfigImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mTabLayout = (TabLayout)findViewById(R.id.main_tabs);
        mViewPager = (ViewPager)findViewById(R.id.main_container);
        mConfigImageView = (ImageView)findViewById(R.id.toolbar_menu);
        mConfigImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ConfigActivity.class);
                startActivity(intent);
            }
        });
        setupTabLayout();
    }

    private void setupTabLayout() {
        SectionsPagerAdapter mSectionsPagerAdapter =
                new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
            }
        });

        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                tab.setIcon(getTabLayoutIcon(i));
            }
        }

        mViewPager.setCurrentItem(0);
    }

    private Drawable getTabLayoutIcon(int position) {
        switch(position) {
            case 0:
                return this.getResources().getDrawable(R.drawable.tablayout_today);
            case 1:
                return this.getResources().getDrawable(R.drawable.tablayout_book);
            case 2:
                return this.getResources().getDrawable(R.drawable.tablayout_info);
            case 3:
                return this.getResources().getDrawable(R.drawable.tablayout_collect);
        }
        return null;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return TodayFragment.newInstance();
                case 1:
                    return BookFragment.newInstance();
                case 2:
                    return NoticeFragment.newInstance();
                case 3:
                    return FavoritesFragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "오늘글귀";
                case 1:
                    return "추천도서";
                case 2:
                    return "공지사항";
                case 3:
                    return "모아보기";
            }
            return null;
        }
    }
}
