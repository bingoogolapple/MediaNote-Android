package cn.bingoogolapple.media.ui.activity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

import cn.bingoogolapple.media.R;
import cn.bingoogolapple.media.model.MediaFile;
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
    private TextView mNameTv;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_audio);
        mTitlebar = getViewById(R.id.titlebar);
        mNameTv = getViewById(R.id.tv_audio_name);

    }

    @Override
    protected void setListener() {
        mTitlebar.setDelegate(new BGATitlebar.BGATitlebarDelegate() {
            @Override
            public void onClickLeftCtv() {
                onBackPressed();
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mMediaFiles = getIntent().getParcelableArrayListExtra(EXTRA_MEDIA_FILES);
        mCurrentMediaFilePosition = getIntent().getIntExtra(EXTRA_CURRENT_MEDIA_FILE_POSITION, 0);
        mCurrentMediaFile = mMediaFiles.get(mCurrentMediaFilePosition);
        mNameTv.setText(mCurrentMediaFile.name);

    }

    @Override
    public void onBackPressed() {
        backward();
    }
}