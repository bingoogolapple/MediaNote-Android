package cn.bingoogolapple.media.ui.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import cn.bingoogolapple.media.R;
import cn.bingoogolapple.media.model.MediaFile;
import cn.bingoogolapple.media.service.AudioService;
import cn.bingoogolapple.media.util.Logger;
import cn.bingoogolapple.titlebar.BGATitlebar;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/18 下午11:46
 * 描述:
 */
public class AudioActivity extends BaseActivity {
    public static final String EXTRA_MEDIA_FILES = "EXTRA_MEDIA_FILES";
    public static final String EXTRA_CURRENT_MEDIA_FILE_POSITION = "EXTRA_CURRENT_MEDIA_FILE_POSITION";
    private ArrayList<MediaFile> mMediaFiles;
    private MediaFile mCurrentMediaFile;
    private int mCurrentMediaFilePosition;
    private ImageView mAnimIv;
    private AnimationDrawable mAnim;
    private TextView mNameTv;

    private TextView mTimeTv;
    private SeekBar mProgressSb;
    private ImageView mModeIv;
    private ImageView mPreIv;
    private ImageView mPlayIv;
    private ImageView mNextIv;

    private AudioServiceReceiver mAudioServiceReceiver;

    private AudioServiceConnection mAudioServiceConnection;
    private AudioService.AudioBinder mAudioBinder;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_audio);
        mTitlebar = getViewById(R.id.titlebar);
        mAnimIv = getViewById(R.id.iv_audio_anim);
        mNameTv = getViewById(R.id.tv_audio_name);

        mTimeTv = getViewById(R.id.tv_audio_time);
        mProgressSb = getViewById(R.id.sb_audio_progress);
        mModeIv = getViewById(R.id.iv_audio_mode);
        mPreIv = getViewById(R.id.iv_audio_pre);
        mPlayIv = getViewById(R.id.iv_audio_play);
        mNextIv = getViewById(R.id.iv_audio_next);

    }

    @Override
    protected void setListener() {
        mTitlebar.setDelegate(new BGATitlebar.BGATitlebarDelegate() {
            @Override
            public void onClickLeftCtv() {
                onBackPressed();
            }
        });
        mModeIv.setOnClickListener(this);
        mPreIv.setOnClickListener(this);
        mPlayIv.setOnClickListener(this);
        mNextIv.setOnClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        registerAudioServiceReceiver();
        bindAudioService();

        mAnim = (AnimationDrawable) mAnimIv.getDrawable();
    }

    private void registerAudioServiceReceiver() {
        mAudioServiceReceiver = new AudioServiceReceiver();
        registerReceiver(mAudioServiceReceiver, new IntentFilter(AudioService.ACTION_MEDIA_PREPARED));
    }

    private void bindAudioService() {
        mAudioServiceConnection = new AudioServiceConnection();
        Intent intent = new Intent(this, AudioService.class);
        mMediaFiles = getIntent().getParcelableArrayListExtra(EXTRA_MEDIA_FILES);
        mCurrentMediaFilePosition = getIntent().getIntExtra(EXTRA_CURRENT_MEDIA_FILE_POSITION, 0);
        intent.putExtra(AudioService.EXTRA_MEDIA_FILE, mMediaFiles.get(mCurrentMediaFilePosition));
        startService(intent);
        bindService(intent, mAudioServiceConnection, BIND_AUTO_CREATE);
    }

    private void unbindAudioService() {
        if (mAudioServiceConnection != null) {
            unbindService(mAudioServiceConnection);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mAudioServiceReceiver);
        unbindAudioService();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_audio_mode:
                break;
            case R.id.iv_audio_pre:
                mAudioBinder.pre();
                break;
            case R.id.iv_audio_play:
                if (mAudioBinder.isPlaying()) {
                    mAudioBinder.pause();
                } else {
                    mAudioBinder.start();
                }
                updatePlayIvImageResource();
                break;
            case R.id.iv_audio_next:
                mAudioBinder.next();
                break;
        }
    }

    private void updatePlayIvImageResource() {
        if (mAudioBinder.isPlaying()) {
            mPlayIv.setImageResource(R.drawable.selector_btn_audio_pause);
            mAnim.start();
        } else {
            mPlayIv.setImageResource(R.drawable.selector_btn_audio_play);
            mAnim.stop();
        }
    }

    @Override
    public void onBackPressed() {
        backward();
    }

    private final class AudioServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logger.i(TAG, "onServiceConnected");
            mAudioBinder = (AudioService.AudioBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logger.i(TAG, "onServiceDisconnected");
        }
    }

    private final class AudioServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case AudioService.ACTION_MEDIA_PREPARED:
                    updatePlayIvImageResource();
                    mCurrentMediaFile = intent.getParcelableExtra(AudioService.EXTRA_MEDIA_FILE);
                    mTitlebar.setTitleText(mCurrentMediaFile.name);
                    mNameTv.setText(mCurrentMediaFile.artist);
                    break;
            }

        }
    }
}