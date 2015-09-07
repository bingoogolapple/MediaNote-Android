package cn.bingoogolapple.media;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;

import cn.bingoogolapple.media.util.ToastUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/2 下午4:13
 * 描述:
 */
public class App extends Application {
    private static final String TAG = App.class.getSimpleName();
    private static App sInstance;
    private long mLastPressBackKeyTime;
    private LinkedList<Activity> mActivities = new LinkedList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

    }

    public static App getInstance() {
        return sInstance;
    }

    /**
     * 获取当前版本名称
     *
     * @return
     */
    public String getCurrentVersionName() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) {
            // 利用系统api getPackageName()得到的包名，这个异常根本不可能发生
            return null;
        }
    }


    public void addActivity(Activity activity) {
        mActivities.add(activity);
    }

    public void removeActivity(Activity activity) {
        mActivities.remove(activity);
    }

    /**
     * 双击后完全退出应用程序
     */
    public void exitWithDoubleClick() {
        if (System.currentTimeMillis() - mLastPressBackKeyTime <= 1500) {
            exit();
        } else {
            mLastPressBackKeyTime = System.currentTimeMillis();
            ToastUtil.show(R.string.toast_exit_tip);
        }
    }

    /**
     * 退出应用程序
     */
    public void exit() {
        Activity activity;
        while (mActivities.size() != 0) {
            activity = mActivities.poll();
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
//        android.os.Process.killProcess(android.os.Process.myPid());
//        System.gc();
//        System.exit(0);
    }
}