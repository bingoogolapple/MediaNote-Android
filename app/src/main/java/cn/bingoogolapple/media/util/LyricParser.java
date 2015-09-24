package cn.bingoogolapple.media.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import cn.bingoogolapple.media.model.Lyric;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/23 下午11:53
 * 描述:
 */
public class LyricParser {

    public static ArrayList<Lyric> parseLyricFromFile(File lyricFile) {
        if (lyricFile == null || !lyricFile.exists()){
            return null;
        }

        ArrayList<Lyric> list = new ArrayList<>();

        try {
            //1.获取每一行文本内容
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(lyricFile), "gbk"));
            String line;
            while ((line = reader.readLine()) != null) {
                //2.将每行歌词内容转为lyric对象
                //[00:07.33][00:02.00]听个工人说 -split("]")
                //[00:07.33  [00:02.00      听个工人说
                String[] arr = line.split("\\]");
                for (int i = 0; i < arr.length - 1; i++) {
                    Lyric lyric = new Lyric();
                    lyric.content = arr[arr.length - 1];
                    lyric.startPoint = formatLyricStartPoint(arr[i]);

                    list.add(lyric);
                }
            }
            //3.对歌词进行排序
            Collections.sort(list);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 将[00:07.33转为long
     *
     * @param startPoint
     * @return
     */
    private static long formatLyricStartPoint(String startPoint) {
        startPoint = startPoint.substring(1);
        //split(":") - > 00   07.33
        String[] arr = startPoint.split("\\:");
        String[] arr2 = arr[1].split("\\.");
        int minute = Integer.parseInt(arr[0]);
        int second = Integer.parseInt(arr2[0]);
        int mills = Integer.parseInt(arr2[1]);
        return minute * 60 * 1000 + second * 1000 + mills * 10;
    }
}