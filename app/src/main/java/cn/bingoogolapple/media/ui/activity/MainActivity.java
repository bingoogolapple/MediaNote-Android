package cn.bingoogolapple.media.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.List;

import cn.bingoogolapple.bgaindicator.BGAFixedIndicator;
import cn.bingoogolapple.media.R;
import cn.bingoogolapple.media.ui.fragment.AudioFragment;
import cn.bingoogolapple.media.ui.fragment.VideoFragment;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    private BGAFixedIndicator mIndicator;
    private ViewPager mContentVp;
    private AudioFragment mAudioFragment;
    private VideoFragment mVideoFragment;

    // 处理 Android 6.0 的权限获取
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }

    @AfterPermissionGranted(1)
    private void conversationWrapper() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
        } else {
            EasyPermissions.requestPermissions(this, "", 1, perms);
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        mIndicator = getViewById(R.id.indicator);
        mContentVp = getViewById(R.id.vp_main_content);
    }

    @Override
    protected void setListener() {
        getViewById(R.id.btn_main_testNetVideo).setOnClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mContentVp.setAdapter(new ContentAdapter(getSupportFragmentManager()));
        mIndicator.initData(0, mContentVp);

        conversationWrapper();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_main_testNetVideo) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(Uri.parse("http://7xk9dj.com1.z0.glb.clouddn.com/medianote/oppo.mp4"), "video/*");
            intent.setDataAndType(Uri.parse("http://7xk9dj.com1.z0.glb.clouddn.com/medianote/rmvb.rmvb"), "video/*");
//            intent.setDataAndType(Uri.parse("http://7xk9dj.com1.z0.glb.clouddn.com/medianote/网络视频.mp4"), "video/*");
//            intent.setDataAndType(Uri.parse("http://7xk9dj.com1.z0.glb.clouddn.com/medianote/测试3gp格式.3gp"), "video/*");
//            intent.setDataAndType(Uri.parse("http://7xk9dj.com1.z0.glb.clouddn.com/medianote/测试非法视频.avi"), "video/*");
            startActivity(intent);
        }
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