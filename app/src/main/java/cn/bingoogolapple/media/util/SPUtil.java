package cn.bingoogolapple.media.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import cn.bingoogolapple.media.App;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/20 下午11:30
 * 描述:
 */
public class SPUtil {
    private static SharedPreferences mSharedPreferences;

    private SPUtil() {
    }

    private static synchronized SharedPreferences getPreferneces() {
        if (mSharedPreferences == null) {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
        }
        return mSharedPreferences;
    }

    public static void clear() {
        getPreferneces().edit().clear().commit();
    }

    public static void putString(String key, String value) {
        getPreferneces().edit().putString(key, value).commit();
    }

    public static String getString(String key) {
        return getPreferneces().getString(key, null);
    }

    public static void putInt(String key, int value) {
        getPreferneces().edit().putInt(key, value).commit();
    }

    public static int getInt(String key) {
        return getPreferneces().getInt(key, 0);
    }

    public static void putBoolean(String key, Boolean value) {
        getPreferneces().edit().putBoolean(key, value).commit();
    }

    public static void putLong(String key, long value) {
        getPreferneces().edit().putLong(key, value).commit();
    }

    public static long getLong(String key) {
        return getPreferneces().getLong(key, 0);
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return getPreferneces().getBoolean(key, defValue);
    }

    public static void remove(String key) {
        getPreferneces().edit().remove(key).commit();
    }

    public static boolean hasKey(String key) {
        return getPreferneces().contains(key);
    }
}