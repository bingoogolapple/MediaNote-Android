package cn.bingoogolapple.media.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

import cn.bingoogolapple.media.util.UIUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/21 下午10:40
 * 描述:
 */
public class LyricView extends TextView {
    private static final String TAG = LyricView.class.getSimpleName();
    private int mHighlightColor = Color.parseColor("#e8f2fe");
    private int mDefaultColor = Color.parseColor("#ffffff");
    private Paint mPaint;

    public LyricView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDefaultAttrs();
    }

    private void initDefaultAttrs() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(UIUtil.sp2px(getContext(), 15));
        mPaint.setColor(mHighlightColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        String lyric = "我是歌词";
        Rect lyricRect = new Rect();
        mPaint.getTextBounds(lyric, 0, lyric.length(), lyricRect);
        float x = getWidth() / 2 - lyricRect.width() / 2;
        float y = getHeight() / 2 + lyricRect.height() / 2;
        canvas.drawText(lyric, x, y, mPaint);
    }
}