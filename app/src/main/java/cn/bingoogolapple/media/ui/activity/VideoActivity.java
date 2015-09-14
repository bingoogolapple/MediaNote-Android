package cn.bingoogolapple.media.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.List;

import cn.bingoogolapple.media.R;
import cn.bingoogolapple.media.model.MediaFile;
import cn.bingoogolapple.media.util.Logger;
import cn.bingoogolapple.media.util.StringUtil;
import cn.bingoogolapple.media.util.UIUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/10 下午2:52
 * 描述:
 */
public class VideoActivity extends BaseActivity {
    public static final String EXTRA_MEDIA_FILES = "EXTRA_MEDIA_FILES";
    public static final String EXTRA_CURRENT_MEDIA_FILE_POSITION = "EXTRA_CURRENT_MEDIA_FILE_POSITION";
    private static final int WHAT_UPDATE_SYSTIME = 0;
    private static final int WHAT_UPDATE_PROGRESS = 1;
    private VideoView mVideoView;
    private List<MediaFile> mMediaFiles;
    private MediaFile mCurrentMediaFile;
    private int mCurrentMediaFilePosition;
    private TextView mNameTv;
    private ImageView mBatteryIv;
    private TextView mSystimeTv;
    private SeekBar mVolumnSb;
    private ImageView mVolumnIv;
    private int mCurrentVolume;
    private int mMaxVolumn;
    private boolean mIsMute;

    private ImageView mExitIv;
    private ImageView mPreIv;
    private ImageView mPlayIv;
    private ImageView mNextIv;
    private ImageView mScreenIv;
    private SeekBar mProgressSb;
    private TextView mCurrentTimeTv;
    private TextView mTotalTimeTv;

    private AudioManager mAudioManager;

    private BatteryBroadcastReceiver mBatteryBroadcastReceiver;

    private float mDownY;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_UPDATE_PROGRESS:
                    updateProgress();
                    break;
                case WHAT_UPDATE_SYSTIME:
                    updateSystime();
                    break;
            }
        }
    };

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_video);
        mVideoView = getViewById(R.id.videoView);
        mNameTv = getViewById(R.id.tv_video_name);
        mBatteryIv = getViewById(R.id.iv_video_battery);
        mSystimeTv = getViewById(R.id.tv_video_systime);
        mVolumnIv = getViewById(R.id.iv_video_voice);
        mVolumnSb = getViewById(R.id.sb_video_volumn);

        mExitIv = getViewById(R.id.iv_video_exit);
        mPreIv = getViewById(R.id.iv_video_pre);
        mPlayIv = getViewById(R.id.iv_video_play);
        mNextIv = getViewById(R.id.iv_video_next);
        mScreenIv = getViewById(R.id.iv_video_screen);
        mProgressSb = getViewById(R.id.sb_video_progress);
        mTotalTimeTv = getViewById(R.id.tv_video_totleTime);
        mCurrentTimeTv = getViewById(R.id.tv_video_currentTime);
    }

    @Override
    protected void setListener() {
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mVideoView.start();
                updatePlayIvImageResource();

                mProgressSb.setMax(mCurrentMediaFile.duration);
                updateProgress();
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayIv.setImageResource(R.drawable.selector_btn_play);

                mHandler.removeMessages(WHAT_UPDATE_PROGRESS);

                mProgressSb.setProgress(mVideoView.getDuration());
                mCurrentTimeTv.setText(StringUtil.formatTime(mVideoView.getDuration()));

                playVideo(mCurrentMediaFilePosition + 1);
            }
        });
        mVolumnIv.setOnClickListener(this);
        mExitIv.setOnClickListener(this);
        mPreIv.setOnClickListener(this);
        mPlayIv.setOnClickListener(this);
        mNextIv.setOnClickListener(this);
        mScreenIv.setOnClickListener(this);
        mVolumnSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mIsMute = false;
                    mCurrentVolume = progress;
                    updateVolumn();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mProgressSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mHandler.removeMessages(WHAT_UPDATE_PROGRESS);
                    mVideoView.seekTo(progress);
                    updateProgress();

                    if (!mVideoView.isPlaying()) {
                        mVideoView.start();
                        updatePlayIvImageResource();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        registerBatteryBroadcastReceiver();

        mMediaFiles = getIntent().getParcelableArrayListExtra(EXTRA_MEDIA_FILES);

        playVideo(getIntent().getIntExtra(EXTRA_CURRENT_MEDIA_FILE_POSITION, 0));

        updateSystime();

        initVolumn();
    }

    private void playVideo(int position) {
        if (mMediaFiles != null && mMediaFiles.size() > 0 && position >= 0 && position < mMediaFiles.size() - 1) {
            mCurrentMediaFilePosition = position;
            mCurrentMediaFile = mMediaFiles.get(mCurrentMediaFilePosition);

            mVideoView.setVideoURI(Uri.parse(mCurrentMediaFile.path));
            mNameTv.setText(mCurrentMediaFile.name);

            mTotalTimeTv.setText(StringUtil.formatTime(mCurrentMediaFile.duration));

            mPreIv.setEnabled(mCurrentMediaFilePosition != 0);
            mNextIv.setEnabled(mCurrentMediaFilePosition != mMediaFiles.size() - 1);
        }

    }

    private void initVolumn() {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolumn = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mVolumnSb.setMax(mMaxVolumn);
        mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        updateVolumn();
    }

    private void updateVolumn() {
        // flag为1时会显示音量变化的悬浮窗口，使用0就好
        if (mIsMute) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            mVolumnSb.setProgress(0);
        } else {
            mVolumnSb.setProgress(mCurrentVolume);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mCurrentVolume, 0);
        }
    }

    private void registerBatteryBroadcastReceiver() {
        mBatteryBroadcastReceiver = new BatteryBroadcastReceiver();
        registerReceiver(mBatteryBroadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_video_voice:
                mIsMute = !mIsMute;
                updateVolumn();
                break;
            case R.id.iv_video_exit:
                backward();
                break;
            case R.id.iv_video_pre:
                playVideo(mCurrentMediaFilePosition - 1);
                break;
            case R.id.iv_video_play:
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    mHandler.removeMessages(WHAT_UPDATE_PROGRESS);
                } else {
                    mVideoView.start();
                    updateProgress();
                }
                updatePlayIvImageResource();
                break;
            case R.id.iv_video_next:
                playVideo(mCurrentMediaFilePosition + 1);
                break;
            case R.id.iv_video_screen:

                break;
        }
    }

    @Override
    protected void onDestroy() {
        mHandler.removeMessages(WHAT_UPDATE_SYSTIME);
        mHandler.removeMessages(WHAT_UPDATE_PROGRESS);
        unregisterReceiver(mBatteryBroadcastReceiver);
        super.onDestroy();
    }

    private void updatePlayIvImageResource() {
        if (mVideoView.isPlaying()) {
            mPlayIv.setImageResource(R.drawable.selector_btn_pause);
        } else {
            mPlayIv.setImageResource(R.drawable.selector_btn_play);
        }
    }

    private void updateSystime() {
        mSystimeTv.setText(StringUtil.formatSystemTime());
        mHandler.sendEmptyMessageDelayed(WHAT_UPDATE_SYSTIME, 1000);
        Logger.i(TAG, "修改系统时间");
    }

    private void updateProgress() {
        mProgressSb.setProgress(mVideoView.getCurrentPosition());
        mHandler.sendEmptyMessageDelayed(WHAT_UPDATE_PROGRESS, 1000);
        mCurrentTimeTv.setText(StringUtil.formatTime(mVideoView.getCurrentPosition()));
        Logger.i(TAG, "修改进度");
    }

    private class BatteryBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // level:0-100
            int level = intent.getIntExtra("level", 0);
            Logger.i(TAG, "BatteryLevel = " + level);
            updateBatteryImage(level);
        }

    }

    /**
     * 根据电量level设置不同的图片
     *
     * @param level
     */
    private void updateBatteryImage(int level) {
        if (level <= 0) {
            mBatteryIv.setImageResource(R.mipmap.ic_battery_0);
        } else if (level > 0 && level <= 10) {
            mBatteryIv.setImageResource(R.mipmap.ic_battery_10);
        } else if (level > 10 && level < 20) {
            mBatteryIv.setImageResource(R.mipmap.ic_battery_20);
        } else if (level > 20 && level <= 40) {
            mBatteryIv.setImageResource(R.mipmap.ic_battery_40);
        } else if (level > 40 && level <= 60) {
            mBatteryIv.setImageResource(R.mipmap.ic_battery_60);
        } else if (level > 60 && level <= 80) {
            mBatteryIv.setImageResource(R.mipmap.ic_battery_80);
        } else {
            mBatteryIv.setImageResource(R.mipmap.ic_battery_100);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float currentY = event.getY();
                float moveDistance = currentY - mDownY;
                if (Math.abs(moveDistance) > UIUtil.dp2px(this, 5)) {
                    mIsMute = false;
                    if (moveDistance < 0) {
                        mCurrentVolume += 1;
                    } else if (moveDistance > 0) {
                        mCurrentVolume -= 1;
                    }
                    if (mCurrentVolume < 0) {
                        mCurrentVolume = 0;
                    } else if (mCurrentVolume > mMaxVolumn) {
                        mCurrentVolume = mMaxVolumn;
                    }
                    updateVolumn();
                }

                mDownY = currentY;
                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return super.onTouchEvent(event);
    }
}