package cn.bingoogolapple.media.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;

import cn.bingoogolapple.media.model.MediaFile;
import cn.bingoogolapple.media.util.Logger;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/20 下午4:48
 * 描述:
 */
public class AudioService extends Service {
    private static final String TAG = AudioService.class.getSimpleName();
    public static final String EXTRA_MEDIA_FILE = "EXTRA_MEDIA_FILE";
    public static final String ACTION_MEDIA_PREPARED = "ACTION_MEDIA_PREPARED";

    private AudioBinder mAudioBinder;
    private MediaPlayer mMediaPlayer;
    private MediaFile mCurrentMediaFile;

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.i(TAG, "onCreate");
        mAudioBinder = new AudioBinder();
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
        mCurrentMediaFile = intent.getParcelableExtra(EXTRA_MEDIA_FILE);
        mAudioBinder.playAudio();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Logger.i(TAG, "onDestroy");
        super.onDestroy();
    }

    private void notifyPrepared() {
        Intent intent = new Intent(ACTION_MEDIA_PREPARED);
        intent.putExtra(EXTRA_MEDIA_FILE, mCurrentMediaFile);
        sendBroadcast(intent);
    }

    public final class AudioBinder extends Binder {

        public void pre() {

        }

        public void playAudio() {
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                    notifyPrepared();
                }
            });
            try {
                mMediaPlayer.setDataSource(mCurrentMediaFile.path);
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
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

        public void next() {

        }

        public boolean isPlaying() {
            return mMediaPlayer != null ? mMediaPlayer.isPlaying() : false;
        }
    }

}