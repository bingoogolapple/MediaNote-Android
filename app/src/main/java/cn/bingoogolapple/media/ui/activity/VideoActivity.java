package cn.bingoogolapple.media.ui.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import cn.bingoogolapple.media.R;
import cn.bingoogolapple.media.model.MediaFile;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/10 下午2:52
 * 描述:
 */
public class VideoActivity extends BaseActivity {
    public static final String EXTRA_MEDIA_FILE = "EXTRA_MEDIA_FILE";
    private VideoView mVideoView;
    private MediaFile mMediaFile;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_video);
        mVideoView = getViewById(R.id.videoView);
    }

    @Override
    protected void setListener() {
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mVideoView.start();
            }
        });
//        mVideoView.setMediaController(new MediaController(this));
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mMediaFile = getIntent().getParcelableExtra(EXTRA_MEDIA_FILE);
        mVideoView.setVideoURI(Uri.parse(mMediaFile.path));

    }
}