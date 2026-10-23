package com.vietnam.retrolauncher;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class StampCardView extends FrameLayout {

    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint focusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();
    private boolean isStampMode = true;
    private int cardColor = Color.parseColor("#C62828");
    private int focusColor = Color.parseColor("#FFD54F");

    public StampCardView(Context context) {
        super(context);
        init();
    }

    public StampCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StampCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        setFocusable(true);
        setFocusableInTouchMode(true);

        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.WHITE);
        borderPaint.setStrokeWidth(5f);
        // Dashed / dotted effect for postage stamp edge
        borderPaint.setPathEffect(new DashPathEffect(new float[]{10f, 10f}, 0));

        focusPaint.setStyle(Paint.Style.STROKE);
        focusPaint.setColor(focusColor);
        focusPaint.setStrokeWidth(8f);

        setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                animate().scaleX(1.06f).scaleY(1.06f).translationZ(16f).setDuration(180).start();
            } else {
                animate().scaleX(1.0f).scaleY(1.0f).translationZ(0f).setDuration(180).start();
            }
            invalidate();
        });
    }

    public void setCardColor(int color) {
        this.cardColor = color;
        invalidate();
    }

    public void setStampMode(boolean stamp) {
        this.isStampMode = stamp;
        invalidate();
    }

    public void setFocusBorderColor(int color) {
        this.focusColor = color;
        focusPaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float density = getResources().getDisplayMetrics().density;
        rect.set(0, 0, getWidth(), getHeight());

        // Draw card background
        Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(cardColor);
        bgPaint.setStyle(Paint.Style.FILL);
        float radius = isStampMode ? 8 * density : 14 * density;
        canvas.drawRoundRect(rect, radius, radius, bgPaint);

        // Draw postage stamp perforated inner line
        if (isStampMode) {
            float inset = 7 * density;
            RectF inner = new RectF(inset, inset, getWidth() - inset, getHeight() - inset);
            canvas.drawRoundRect(inner, 4 * density, 4 * density, borderPaint);
        }

        // Draw TV Remote focus indicator
        if (isFocused()) {
            float focusInset = 2 * density;
            RectF focusRect = new RectF(focusInset, focusInset, getWidth() - focusInset, getHeight() - focusInset);
            canvas.drawRoundRect(focusRect, radius, radius, focusPaint);
        }

        super.onDraw(canvas);
    }
}
