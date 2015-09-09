package cn.bingoogolapple.media.engine;

import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.media.App;
import cn.bingoogolapple.media.model.MediaFile;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/7 下午11:00
 * 描述:
 */
public class MediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {
    private static String AUDIO_PATH = MediaStore.Audio.AudioColumns.DATA;
    private static String AUDIO_TITLE = MediaStore.Audio.AudioColumns.TITLE;
    private static String AUDIO_SIZE = MediaStore.Audio.AudioColumns.SIZE;
    private static String AUDIO_DURATION = MediaStore.Audio.AudioColumns.DURATION;
    private static String AUDIO_PROJECTION[] = {AUDIO_PATH, AUDIO_TITLE, AUDIO_SIZE, AUDIO_DURATION};

    private static String VIDEO_PATH = MediaStore.Video.VideoColumns.DATA;
    private static String VIDEO_TITLE = MediaStore.Video.VideoColumns.TITLE;
    private static String VIDEO_SIZE = MediaStore.Video.VideoColumns.SIZE;
    private static String VIDEO_DURATION = MediaStore.Video.VideoColumns.DURATION;
    private static String VIDEO_PROJECTION[] = {VIDEO_PATH, VIDEO_TITLE, VIDEO_SIZE, VIDEO_DURATION};

    private MediaScannerConnection mMs;
    private File mFile;

    public void startScan(File file) {
        if (mMs != null) {
            mMs.disconnect();
        }
        mFile = file;
        mMs = new MediaScannerConnection(App.getInstance(), this);
        mMs.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        mMs.scanFile(mFile.getAbsolutePath(), null);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        mMs.disconnect();
    }

    public static List<MediaFile> scanAudio() {
        return scanMedia(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, AUDIO_PROJECTION, AUDIO_TITLE, AUDIO_TITLE, AUDIO_PATH, AUDIO_SIZE, AUDIO_DURATION);
    }

    public static List<MediaFile> scanVideo() {
        return scanMedia(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, VIDEO_PROJECTION, VIDEO_TITLE, VIDEO_TITLE, VIDEO_PATH, VIDEO_SIZE, VIDEO_DURATION);
    }

    public static List<MediaFile> scanMedia(Uri uri, String[] projection, String sortOrder, String nameColumn, String pathColumn, String sizeColumn, String durationColumn) {
        List<MediaFile> results = new ArrayList<>();
        Cursor cursor = App.getInstance().getContentResolver().query(uri, projection, null, null, sortOrder);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                MediaFile mediaFile;
                while (cursor.moveToNext()) {
                    mediaFile = new MediaFile();
                    mediaFile.name = cursor.getString(cursor.getColumnIndex(nameColumn));
                    mediaFile.path = cursor.getString(cursor.getColumnIndex(pathColumn));
                    mediaFile.duration = cursor.getInt(cursor.getColumnIndex(durationColumn));
                    mediaFile.size = cursor.getInt(cursor.getColumnIndex(sizeColumn));
                    results.add(mediaFile);
                }
            }
            cursor.close();
        }
        return results;
    }

}