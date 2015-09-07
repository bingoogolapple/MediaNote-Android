package cn.bingoogolapple.media.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import cn.bingoogolapple.bgaindicator.BGAFixedIndicator;
import cn.bingoogolapple.media.R;
import cn.bingoogolapple.media.ui.fragment.VideoFragment;
import cn.bingoogolapple.media.ui.fragment.AudioFragment;

public class MainActivity extends BaseActivity {
    private BGAFixedIndicator mIndicator;
    private ViewPager mContentVp;
    private AudioFragment mAudioFragment;
    private VideoFragment mVideoFragment;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        mIndicator = getViewById(R.id.indicator);
        mContentVp = getViewById(R.id.vp_main_content);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mContentVp.setAdapter(new ContentAdapter(getSupportFragmentManager()));
        mIndicator.initData(0, mContentVp);
    }

    private class ContentAdapter extends FragmentPagerAdapter {

        public ContentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (mAudioFragment == null) {
                        mAudioFragment = new AudioFragment();
                    }
                    return mAudioFragment;
                case 1:
                    if (mVideoFragment == null) {
                        mVideoFragment = new VideoFragment();
                    }
                    return mVideoFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "音乐";
                case 1:
                    return "视频";
                default:
                    return null;
            }
        }
    }
}