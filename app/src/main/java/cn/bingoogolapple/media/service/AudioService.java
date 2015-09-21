package cn.bingoogolapple.media.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.RemoteViews;

import java.io.IOException;
import java.util.ArrayList;

import cn.bingoogolapple.media.App;
import cn.bingoogolapple.media.R;
import cn.bingoogolapple.media.model.MediaFile;
import cn.bingoogolapple.media.ui.activity.AudioActivity;
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
    public static final String ACTION_AUDIO_UPDATE_ICON = "ACTION_AUDIO_UPDATE_ICON";

    public static final String EXTRA_NOTIFICATION_TYPE = "EXTRA_NOTIFICATION_TYPE";
    public static final String EXTRA_IS_FROM_NOTIFICATION = "EXTRA_IS_FROM_NOTIFICATION";
    public static final int REQUESTCODE_PRE = 0;
    public static final int REQUESTCODE_PLAY = 1;
    public static final int REQUESTCODE_NEXT = 2;
    public static final int REQUESTCODE_CONTAINER = 3;

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
            if (intent.getExtras().getBoolean(EXTRA_IS_FROM_NOTIFICATION)) {
                switch (intent.getExtras().getInt(AudioService.EXTRA_NOTIFICATION_TYPE)) {
                    case AudioService.REQUESTCODE_PRE:
                        mAudioBinder.pre();
                        break;
                    case AudioService.REQUESTCODE_PLAY:
                        if (mAudioBinder.isPlaying()) {
                            mAudioBinder.pause();
                        } else {
                            mAudioBinder.start();
                        }
                        break;
                    case AudioService.REQUESTCODE_NEXT:
                        mAudioBinder.next();
                        break;
                    case AudioService.REQUESTCODE_CONTAINER:
                        notifyPrepared();
                        break;
                }
            } else {
                mCurrentMediaFilePosition = intent.getExtras().getInt(EXTRA_CURRENT_MEDIA_FILE_POSITION);
                mMediaFiles = intent.getExtras().getParcelableArrayList(EXTRA_MEDIA_FILES);
                mAudioBinder.playAudio(mCurrentMediaFilePosition);
            }
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

    private void notifyUpdateIcon() {
        Intent intent = new Intent(ACTION_AUDIO_UPDATE_ICON);
        intent.putExtra(EXTRA_CURRENT_MEDIA_FILE_POSITION, mCurrentMediaFilePosition);
        intent.putExtra(EXTRA_TOTAL_MEDIA_FILE, mMediaFiles.size());
        mLocalBroadcastManager.sendBroadcast(intent);

        sendNotification();
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

    private void sendNotification() {
        Intent preIntent = new Intent(this, AudioService.class);
        Bundle preBundle = new Bundle();
        preBundle.putBoolean(EXTRA_IS_FROM_NOTIFICATION, true);
        preBundle.putInt(AudioService.EXTRA_NOTIFICATION_TYPE, AudioService.REQUESTCODE_PRE);
        preIntent.putExtras(preBundle);
        // 如果有多个PendingIntent,必须把requestCode设置成不同
        PendingIntent prePendingIntent = PendingIntent.getService(this, AudioService.REQUESTCODE_PRE, preIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playIntent = new Intent(this, AudioService.class);
        Bundle playBundle = new Bundle();
        playBundle.putBoolean(EXTRA_IS_FROM_NOTIFICATION, true);
        playBundle.putInt(AudioService.EXTRA_NOTIFICATION_TYPE, AudioService.REQUESTCODE_PLAY);
        playIntent.putExtras(playBundle);
        // 也可以通过getService方法获取PendingIntent，在onStartCommand方法中处理
        PendingIntent playPendingIntent = PendingIntent.getService(this, AudioService.REQUESTCODE_PLAY, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(this, AudioService.class);
        Bundle nextBundle = new Bundle();
        nextBundle.putBoolean(EXTRA_IS_FROM_NOTIFICATION, true);
        nextBundle.putInt(AudioService.EXTRA_NOTIFICATION_TYPE, AudioService.REQUESTCODE_NEXT);
        nextIntent.putExtras(nextBundle);
        PendingIntent nextPendingIntent = PendingIntent.getService(this, AudioService.REQUESTCODE_NEXT, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent containerIntent = new Intent(this, AudioActivity.class);
        Bundle containerBundle = new Bundle();
        containerBundle.putBoolean(EXTRA_IS_FROM_NOTIFICATION, true);
        containerBundle.putInt(AudioService.EXTRA_NOTIFICATION_TYPE, AudioService.REQUESTCODE_CONTAINER);
        containerIntent.putExtras(containerBundle);
        PendingIntent containerPendingIntent = PendingIntent.getActivity(this, AudioService.REQUESTCODE_CONTAINER, containerIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews remoteView = new RemoteViews(getPackageName(), R.layout.notification_audio);
        remoteView.setOnClickPendingIntent(R.id.iv_audio_pre, prePendingIntent);
        remoteView.setOnClickPendingIntent(R.id.iv_audio_play, playPendingIntent);
        remoteView.setOnClickPendingIntent(R.id.iv_audio_next, nextPendingIntent);
        remoteView.setOnClickPendingIntent(R.id.ll_audio_container, containerPendingIntent);
        remoteView.setTextViewText(R.id.tv_audio_name, mCurrentMediaFile.name);
        remoteView.setTextViewText(R.id.tv_audio_artist, mCurrentMediaFile.artist);

        remoteView.setImageViewResource(R.id.iv_audio_play, mAudioBinder.isPlaying() ? R.drawable.selector_btn_audio_pause : R.drawable.selector_btn_audio_play);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setOngoing(true);
        // 必须制定图标，否则不会显示该条信息
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setTicker(mCurrentMediaFile.name);
        builder.setWhen(System.currentTimeMillis());
        builder.setContent(remoteView);

//        App.getInstance().removeNotification(1000);
        App.getInstance().addNotification(1000, builder.build());
//        startForeground(1000, builder.build());
//        stopForeground(true);
    }

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
            }
        }

        public void start() {
            if (mMediaPlayer != null) {
                mMediaPlayer.start();
            }
            notifyUpdateIcon();
        }

        public void pause() {
            if (mMediaPlayer != null) {
                mMediaPlayer.pause();
            }
            notifyUpdateIcon();
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