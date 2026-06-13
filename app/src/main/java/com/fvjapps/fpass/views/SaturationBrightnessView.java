package com.fvjapps.fpass.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class SaturationBrightnessView extends View {

    private Bitmap bitmap;
    private Paint bitmapPaint;
    private Paint crosshairPaint;
    private float hue = 0f;
    private float saturation = 1f;
    private float brightness = 1f;
    private OnColorChangeListener listener;

    public interface OnColorChangeListener {
        void onColorChanged(int color);
    }

    public SaturationBrightnessView(Context context) {
        super(context);
        init();
    }

    public SaturationBrightnessView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SaturationBrightnessView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        crosshairPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        crosshairPaint.setStyle(Paint.Style.STROKE);
        crosshairPaint.setStrokeWidth(2f);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    public void setOnColorChangeListener(OnColorChangeListener listener) {
        this.listener = listener;
    }

    public void setHue(float hue) {
        this.hue = hue;
        rebuildBitmap();
        invalidate();
    }

    public float getSaturation() { return saturation; }
    public float getBrightness() { return brightness; }

    public void setSaturationBrightness(float saturation, float brightness) {
        this.saturation = Math.max(0f, Math.min(1f, saturation));
        this.brightness = Math.max(0f, Math.min(1f, brightness));
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rebuildBitmap();
    }

    private void rebuildBitmap() {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;
        if (bitmap != null && !bitmap.isRecycled()) bitmap.recycle();
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                float sat = (float) x / w;
                float bright = 1f - (float) y / h;
                bitmap.setPixel(x, y, Color.HSVToColor(new float[]{hue, sat, bright}));
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap != null && !bitmap.isRecycled()) {
            canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);
        }
        float cx = saturation * getWidth();
        float cy = (1f - brightness) * getHeight();
        crosshairPaint.setColor(Color.WHITE);
        canvas.drawCircle(cx, cy, 10f, crosshairPaint);
        crosshairPaint.setColor(Color.argb(80, 0, 0, 0));
        canvas.drawCircle(cx, cy, 11f, crosshairPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (x < 0) x = 0;
        if (x > getWidth()) x = getWidth();
        if (y < 0) y = 0;
        if (y > getHeight()) y = getHeight();
        saturation = x / getWidth();
        brightness = 1f - y / getHeight();
        if (listener != null) {
            listener.onColorChanged(Color.HSVToColor(new float[]{hue, saturation, brightness}));
        }
        invalidate();
        return true;
    }

    public int getCurrentColor() {
        return Color.HSVToColor(new float[]{hue, saturation, brightness});
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }
}
