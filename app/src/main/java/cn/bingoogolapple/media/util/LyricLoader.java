package cn.bingoogolapple.media.util;

import android.os.Environment;

import java.io.File;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/24 上午12:06
 * 描述:
 */
public class LyricLoader {

    public static File loadLyricFile(String audioName) {
        String lyricDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Download";
        File file = new File(lyricDir, StringUtil.formatAudioName(audioName) + ".txt");
        if (!file.exists()) {
            file = new File(lyricDir, StringUtil.formatAudioName(audioName) + ".lrc");
        }
        return file;
    }

}