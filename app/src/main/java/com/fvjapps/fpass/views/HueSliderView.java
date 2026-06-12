package com.fvjapps.fpass.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class HueSliderView extends View {

    private Paint gradientPaint;
    private Paint thumbPaint;
    private RectF barRect;
    private float hue = 0f;
    private float thumbRadius = 12f;
    private OnHueChangeListener listener;

    public interface OnHueChangeListener {
        void onHueChanged(float hue);
    }

    public HueSliderView(Context context) {
        super(context);
        init();
    }

    public HueSliderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HueSliderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        gradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        thumbPaint.setColor(Color.WHITE);
        thumbPaint.setShadowLayer(4f, 0f, 2f, Color.argb(80, 0, 0, 0));
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    public void setOnHueChangeListener(OnHueChangeListener listener) {
        this.listener = listener;
    }

    public float getHue() {
        return hue;
    }

    public void setHue(float hue) {
        this.hue = Math.max(0f, Math.min(360f, hue));
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float padding = thumbRadius + 4f;
        barRect = new RectF(padding, h / 2f - 8f, w - padding, h / 2f + 8f);
        buildGradient();
    }

    private void buildGradient() {
        if (barRect == null) return;
        int[] colors = new int[]{
                Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN,
                Color.BLUE, Color.MAGENTA, Color.RED
        };
        float[] positions = new float[]{0f, 0.17f, 0.33f, 0.5f, 0.67f, 0.83f, 1f};
        gradientPaint.setShader(new LinearGradient(
                barRect.left, 0, barRect.right, 0,
                colors, positions, Shader.TileMode.CLAMP
        ));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (barRect == null) return;
        canvas.drawRoundRect(barRect, 8f, 8f, gradientPaint);
        float thumbX = barRect.left + (hue / 360f) * barRect.width();
        canvas.drawCircle(thumbX, getHeight() / 2f, thumbRadius, thumbPaint);
        thumbPaint.setStyle(Paint.Style.STROKE);
        thumbPaint.setStrokeWidth(2f);
        thumbPaint.setColor(Color.parseColor("#BDBDBD"));
        canvas.drawCircle(thumbX, getHeight() / 2f, thumbRadius, thumbPaint);
        thumbPaint.setStyle(Paint.Style.FILL);
        thumbPaint.setColor(Color.WHITE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (barRect == null) return true;
        float x = event.getX();
        if (x < barRect.left) x = barRect.left;
        if (x > barRect.right) x = barRect.right;
        hue = ((x - barRect.left) / barRect.width()) * 360f;
        if (listener != null) listener.onHueChanged(hue);
        invalidate();
        return true;
    }
}
