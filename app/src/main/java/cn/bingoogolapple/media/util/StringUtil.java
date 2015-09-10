package cn.bingoogolapple.media.util;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/10 上午10:22
 * 描述:
 */
public class StringUtil {

    private StringUtil() {
    }

    public static String formatTime(long remainTime) {
        int SECOND = 1000;
        int MINUTE = SECOND * 60;
        int HOUR = MINUTE * 60 * 60;

        int hour = (int) (remainTime / HOUR);
        remainTime = remainTime % HOUR;

        int minute = (int) (remainTime / MINUTE);
        remainTime = remainTime % MINUTE;

        int second = (int) (remainTime / SECOND);

        if (hour == 0) {
            return String.format("%02d:%02d", minute, second);
        } else {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        }
    }
}