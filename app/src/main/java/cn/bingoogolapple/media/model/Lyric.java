package cn.bingoogolapple.media.model;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/23 下午11:01
 * 描述:
 */
public class Lyric {
    public String content;
    public long startPoint;

    public Lyric(String content, long startPoint) {
        this.content = content;
        this.startPoint = startPoint;
    }
}