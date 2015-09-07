package cn.bingoogolapple.media.util;

import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.widget.Toast;

import cn.bingoogolapple.media.App;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/2 下午5:17
 * 描述:
 */
public class ToastUtil {

    private ToastUtil() {
    }

    public static void show(CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            if (text.length() < 10) {
                Toast.makeText(App.getInstance(), text, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(App.getInstance(), text, Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void show(@StringRes int resId) {
        show(App.getInstance().getResources().getString(resId));
    }

    public static void showSafe(final CharSequence text) {
        ThreadUtil.runInUIThread(new Runnable() {
            @Override
            public void run() {
                show(text);
            }
        });
    }

    public static void showSafe(@StringRes int resId) {
        showSafe(App.getInstance().getResources().getString(resId));
    }
}