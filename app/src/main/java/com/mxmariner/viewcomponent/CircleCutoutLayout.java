package com.mxmariner.viewcomponent;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.mxmariner.tides.R;

public class CircleCutoutLayout extends FrameLayout {

    private final Path path = new Path();
    private final Paint borderPaint = new Paint();
    private int borderPixels;

    public CircleCutoutLayout(Context context) {
        super(context);
        init();
    }

    public CircleCutoutLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleCutoutLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        borderPixels = getResources().getDimensionPixelSize(R.dimen.border_width);
        borderPaint.setStrokeWidth(borderPixels * 2);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(getResources().getColor(R.color.secondary_text));
        borderPaint.setAntiAlias(true);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int x = getWidth() / 2;
        int y = getHeight() / 2;
        int r = Math.min(x, y);
        path.addCircle(x, y, r, Path.Direction.CW);
        canvas.clipPath(path);
        super.dispatchDraw(canvas);
        canvas.drawCircle(x, y, r, borderPaint);
    }
}
