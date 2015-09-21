package cn.bingoogolapple.media.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;
import java.util.ArrayList;

import cn.bingoogolapple.media.model.MediaFile;
import cn.bingoogolapple.media.util.Logger;
import cn.bingoogolapple.media.util.SPUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/20 下午4:48
 * 描述:
 */
public class AudioService extends Service {
    private static final String TAG = AudioService.class.getSimpleName();
    public static final String EXTRA_MEDIA_FILES = "EXTRA_MEDIA_FILES";
    public static final String EXTRA_CURRENT_MEDIA_FILE_POSITION = "EXTRA_CURRENT_MEDIA_FILE_POSITION";
    public static final String EXTRA_TOTAL_MEDIA_FILE = "EXTRA_TOTAL_MEDIA_FILE";
    public static final String EXTRA_MEDIA_FILE = "EXTRA_MEDIA_FILE";

    public static final String ACTION_AUDIO_PREPARED = "ACTION_AUDIO_PREPARED";
    public static final String ACTION_AUDIO_COMPLETION = "ACTION_AUDIO_COMPLETION";
    public static final String ACTION_AUDIO_FIRST_LAST = "ACTION_AUDIO_FIRST_LAST";

    private static final String SP_REPEAT_MODE = "SP_REPEAT_MODE";

    private AudioBinder mAudioBinder;
    private MediaPlayer mMediaPlayer;
    private ArrayList<MediaFile> mMediaFiles;
    private int mCurrentMediaFilePosition;
    private MediaFile mCurrentMediaFile;

    private RepeatMode mRepeatMode = RepeatMode.Order;

    private LocalBroadcastManager mLocalBroadcastManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.i(TAG, "onCreate");
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mAudioBinder = new AudioBinder();
        mRepeatMode = RepeatMode.values()[SPUtil.getInt(SP_REPEAT_MODE)];
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Logger.i(TAG, "onBind");
        return mAudioBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.i(TAG, "onStartCommand");
        if (intent != null && intent.getExtras() != null) {
            mCurrentMediaFilePosition = intent.getExtras().getInt(EXTRA_CURRENT_MEDIA_FILE_POSITION);
            mMediaFiles = intent.getExtras().getParcelableArrayList(EXTRA_MEDIA_FILES);
            mAudioBinder.playAudio(mCurrentMediaFilePosition);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Logger.i(TAG, "onDestroy");
        super.onDestroy();
    }

    private void notifyPrepared() {
        Intent intent = new Intent(ACTION_AUDIO_PREPARED);
        intent.putExtra(EXTRA_MEDIA_FILE, mCurrentMediaFile);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    private void notifyCompletion() {
        Intent intent = new Intent(ACTION_AUDIO_COMPLETION);
        intent.putExtra(EXTRA_MEDIA_FILE, mCurrentMediaFile);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    private void notifyFirstAndLast() {
        Intent intent = new Intent(ACTION_AUDIO_FIRST_LAST);
        intent.putExtra(EXTRA_CURRENT_MEDIA_FILE_POSITION, mCurrentMediaFilePosition);
        intent.putExtra(EXTRA_TOTAL_MEDIA_FILE, mMediaFiles.size());
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            notifyPrepared();
            mAudioBinder.start();
        }
    };

    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            notifyCompletion();
            switch (mRepeatMode) {
                case Order:
                    mAudioBinder.next();
                    break;
                case SingleRepeat:
                    mAudioBinder.playAudio(mCurrentMediaFilePosition);
                    break;
                case AllRepeat:
                    if (mCurrentMediaFilePosition == mMediaFiles.size() - 1) {
                        mAudioBinder.playAudio(0);
                    } else {
                        mAudioBinder.next();
                    }
                    break;
            }
        }
    };

    public final class AudioBinder extends Binder {

        public void playAudio(int position) {
            if (mMediaFiles != null && mMediaFiles.size() > 0 && position >= 0 && position <= mMediaFiles.size() - 1) {
                mCurrentMediaFilePosition = position;
                mCurrentMediaFile = mMediaFiles.get(mCurrentMediaFilePosition);

                if (mMediaPlayer != null) {
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
                mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
                try {
                    mMediaPlayer.setDataSource(mCurrentMediaFile.path);
                    mMediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                notifyFirstAndLast();
            }
        }

        public void start() {
            if (mMediaPlayer != null) {
                mMediaPlayer.start();
            }
        }

        public void pause() {
            if (mMediaPlayer != null) {
                mMediaPlayer.pause();
            }
        }

        public void pre() {
            playAudio(mCurrentMediaFilePosition - 1);
        }

        public void next() {
            playAudio(mCurrentMediaFilePosition + 1);
        }

        public void seekTo(int msec) {
            if (mMediaPlayer != null) {
                mMediaPlayer.seekTo(msec);
            }
        }

        public boolean isPlaying() {
            return mMediaPlayer != null ? mMediaPlayer.isPlaying() : false;
        }

        public int getCurrentPosition() {
            return mMediaPlayer != null ? mMediaPlayer.getCurrentPosition() : 0;
        }

        public int getDuration() {
            return mMediaPlayer != null ? mMediaPlayer.getDuration() : 0;
        }

        public void switchPlayMode() {
            switch (mRepeatMode) {
                case Order:
                    mRepeatMode = RepeatMode.SingleRepeat;
                    break;
                case SingleRepeat:
                    mRepeatMode = RepeatMode.AllRepeat;
                    break;
                case AllRepeat:
                    mRepeatMode = RepeatMode.Order;
                    break;
            }
            SPUtil.putInt(SP_REPEAT_MODE, mRepeatMode.ordinal());
        }

        public RepeatMode getRepeatMode() {
            return mRepeatMode;
        }
    }

    public enum RepeatMode {
        Order, SingleRepeat, AllRepeat
    }

}