package cn.bingoogolapple.media;

import android.test.AndroidTestCase;

import java.util.List;

import cn.bingoogolapple.media.engine.MediaScanner;
import cn.bingoogolapple.media.model.MediaFile;
import cn.bingoogolapple.media.util.Logger;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/7 下午11:59
 * 描述:
 */
public class TestMediaScanner extends AndroidTestCase {
    private static final String TAG = TestMediaScanner.class.getSimpleName();

    public void testScanAudio() {
        List<MediaFile> mediaFiles = MediaScanner.scanAudio();
        for (MediaFile mediaFile : mediaFiles) {
            Logger.i(TAG, mediaFile.path);
        }
    }

    public void testVideoAudio() {
        List<MediaFile> mediaFiles = MediaScanner.scanVideo();
        for (MediaFile mediaFile : mediaFiles) {
            Logger.i(TAG, mediaFile.path);
        }
    }
}