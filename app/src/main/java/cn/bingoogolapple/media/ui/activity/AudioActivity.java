package cn.bingoogolapple.media.ui.activity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import cn.bingoogolapple.media.R;
import cn.bingoogolapple.media.model.MediaFile;
import cn.bingoogolapple.media.util.ToastUtil;
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
        mMediaFiles = getIntent().getParcelableArrayListExtra(EXTRA_MEDIA_FILES);
        mCurrentMediaFilePosition = getIntent().getIntExtra(EXTRA_CURRENT_MEDIA_FILE_POSITION, 0);
        mCurrentMediaFile = mMediaFiles.get(mCurrentMediaFilePosition);

        mTitlebar.setTitleText(mCurrentMediaFile.name);
        mNameTv.setText(mCurrentMediaFile.artist);

        mAnim = (AnimationDrawable) mAnimIv.getDrawable();
        mAnim.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_audio_mode:
                ToastUtil.show("播放模式");
                break;
            case R.id.iv_audio_pre:
                ToastUtil.show("上一首");
                break;
            case R.id.iv_audio_play:
                ToastUtil.show("播放");
                break;
            case R.id.iv_audio_next:
                ToastUtil.show("下一首");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        backward();
    }
}