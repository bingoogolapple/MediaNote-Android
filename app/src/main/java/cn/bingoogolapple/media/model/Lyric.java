package cn.bingoogolapple.media.model;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/23 下午11:01
 * 描述:
 */
public class Lyric implements Comparable<Lyric> {
    public String content;
    public long startPoint;

    public Lyric() {
    }

    public Lyric(String content, long startPoint) {
        this.content = content;
        this.startPoint = startPoint;
    }

    @Override
    public int compareTo(Lyric another) {
        return (int) (this.startPoint - another.startPoint);
    }
}