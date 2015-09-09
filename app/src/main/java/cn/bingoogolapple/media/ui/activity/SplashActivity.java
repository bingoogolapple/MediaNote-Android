package cn.bingoogolapple.media.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;

import cn.bingoogolapple.media.R;
import cn.bingoogolapple.media.util.ThreadUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/2 下午4:14
 * 描述:
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        TextView versionTv = getViewById(R.id.tv_splash_version);
        versionTv.setText("V" + mApp.getCurrentVersionName());

        fireSDCard();

        ThreadUtil.runInUIThread(new Runnable() {
            @Override
            public void run() {
                forwardAndFinish(MainActivity.class);
            }
        }, 1500);
    }

    private void fireSDCard() {
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
    }
}