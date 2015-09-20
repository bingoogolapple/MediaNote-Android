package cn.bingoogolapple.media.ui.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import cn.bingoogolapple.media.R;
import cn.bingoogolapple.media.model.MediaFile;
import cn.bingoogolapple.media.service.AudioService;
import cn.bingoogolapple.media.util.Logger;
import cn.bingoogolapple.media.util.StringUtil;
import cn.bingoogolapple.titlebar.BGATitlebar;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/18 下午11:46
 * 描述:
 */
public class AudioActivity extends BaseActivity {
    private static final int WHAT_UPDATE_PROGRESS = 0;

    private ImageView mAnimIv;
    private AnimationDrawable mAnim;
    private TextView mArtistTv;

    private TextView mTimeTv;
    private SeekBar mProgressSb;
    private ImageView mModeIv;
    private ImageView mPreIv;
    private ImageView mPlayIv;
    private ImageView mNextIv;

    private AudioServiceReceiver mAudioServiceReceiver;

    private AudioServiceConnection mAudioServiceConnection;
    private AudioService.AudioBinder mAudioBinder;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_UPDATE_PROGRESS:
                    updateProgress();
                    break;
            }
        }
    };

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_audio);
        mTitlebar = getViewById(R.id.titlebar);
        mAnimIv = getViewById(R.id.iv_audio_anim);
        mArtistTv = getViewById(R.id.tv_audio_artist);

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
        mProgressSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mHandler.removeMessages(WHAT_UPDATE_PROGRESS);
                    mAudioBinder.seekTo(progress);
                    updateProgress();

                    if (!mAudioBinder.isPlaying()) {
                        mAudioBinder.start();
                        updatePlayIvImageResource();
                    }
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void updateProgress() {
        int progress = mAudioBinder.getCurrentPosition();
        mTimeTv.setText(StringUtil.formatTime(progress) + "/" + StringUtil.formatTime(mAudioBinder.getDuration()));
        mProgressSb.setProgress(progress);
        mHandler.sendEmptyMessageDelayed(WHAT_UPDATE_PROGRESS, 1000);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        registerAudioServiceReceiver();
        bindAudioService();

        mAnim = (AnimationDrawable) mAnimIv.getDrawable();
    }

    private void registerAudioServiceReceiver() {
        mAudioServiceReceiver = new AudioServiceReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AudioService.ACTION_AUDIO_PREPARED);
        intentFilter.addAction(AudioService.ACTION_AUDIO_COMPLETION);
        intentFilter.addAction(AudioService.ACTION_AUDIO_FIRST_LAST);
        registerReceiver(mAudioServiceReceiver, intentFilter);
    }

    private void bindAudioService() {
        mAudioServiceConnection = new AudioServiceConnection();
        Intent intent = new Intent(this, AudioService.class);
        intent.putExtras(getIntent().getExtras());
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
        mHandler.removeCallbacksAndMessages(null);
        unregisterReceiver(mAudioServiceReceiver);
        unbindAudioService();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_audio_mode:
                mAudioBinder.switchPlayMode();
                updateRepeatModeIvImageResource();
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

    private void updateRepeatModeIvImageResource() {
        switch (mAudioBinder.getRepeatMode()) {
            case Order:
                mModeIv.setImageResource(R.drawable.selector_audio_mode_normal);
                break;
            case SingleRepeat:
                mModeIv.setImageResource(R.drawable.selector_audio_mode_single_repeat);
                break;
            case AllRepeat:
                mModeIv.setImageResource(R.drawable.selector_audio_mode_all_repeat);
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
            MediaFile mediaFile = intent.getParcelableExtra(AudioService.EXTRA_MEDIA_FILE);
            switch (intent.getAction()) {
                case AudioService.ACTION_AUDIO_PREPARED:
                    updatePlayIvImageResource();

                    mTitlebar.setTitleText(mediaFile.name);
                    mArtistTv.setText(mediaFile.artist);

                    mTimeTv.setText("00:00/" + StringUtil.formatTime(mediaFile.duration));
                    mProgressSb.setMax(mediaFile.duration);

                    updateProgress();
                    break;
                case AudioService.ACTION_AUDIO_COMPLETION:
                    updatePlayIvImageResource();

                    mHandler.removeMessages(WHAT_UPDATE_PROGRESS);

                    mTimeTv.setText(StringUtil.formatTime(mediaFile.duration) + "/" + StringUtil.formatTime(mediaFile.duration));
                    mProgressSb.setProgress(mediaFile.duration);

                    break;
                case AudioService.ACTION_AUDIO_FIRST_LAST:
                    int currentPosition = intent.getIntExtra(AudioService.EXTRA_CURRENT_MEDIA_FILE_POSITION, 0);
                    int total = intent.getIntExtra(AudioService.EXTRA_TOTAL_MEDIA_FILE, 0);
                    mPreIv.setEnabled(currentPosition != 0);
                    mNextIv.setEnabled(currentPosition != total - 1);
                    break;
            }

        }
    }
}