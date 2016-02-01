package com.rstudio.view.waveview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;

/**
 * Created by Ryan on 1/31/16.
 */
public class WaveView extends View {

    private static String TAG = "WaveView";

    public static final Property<WaveView, Float> WAVE_VIEW_ANIMATE_PROPERTY
            = new Property<WaveView, Float>(Float.class, "WAVE_VIEW_ANIMATE_PROPERTY") {
        @Override
        public Float get(WaveView object) {
            return object.getProgress();
        }

        @Override
        public void set(WaveView spanGroup, Float value) {
            spanGroup.setProgress(value);
        }
    };

    private ObjectAnimator objectAnimator;

    private Paint mWavePaint;

    private int height, width;

    private int midPivotX, midPivotY;

    private float targetWaveHeight = 0;
    private float currentWaveHeight = 0;

    private float ratio;

    private float gap;

    private float waveGap;

    private float waveSlope;

    private float alphaWave1, alphaWave2, alphaWave3;

    private int color1, color2, color3;

    public WaveView(Context context) {
        super(context);
        init();
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        // this line is very important, it make the view smoothly
        // when you drag n drop, zoom in n out component inside it
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        mWavePaint = new Paint();
        mWavePaint.setStyle(Paint.Style.FILL);
        gap = 0.14f;
        alphaWave1 = 0f;
        alphaWave2 = 0f;
        alphaWave3 = 0f;
        setWaveColors(getContext().getResources().getColor(R.color.wave_color_1)
                , getContext().getResources().getColor(R.color.wave_color_2)
                , getContext().getResources().getColor(R.color.wave_color_3));
        waveSlope = 1.0f;
        waveGap = 1f;
    }

    public void start() {
        if (objectAnimator != null && objectAnimator.isRunning()) return;
        else if (objectAnimator == null) {
            objectAnimator = ObjectAnimator.ofFloat(this, WaveView.WAVE_VIEW_ANIMATE_PROPERTY, 1, 30);
            objectAnimator.setRepeatMode(ValueAnimator.RESTART);
            objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
            objectAnimator.start();
        }
        else {
            objectAnimator.start();
        }
    }

    public boolean isRunning() {
        return objectAnimator == null ? false : objectAnimator.isRunning();
    }

    /**
     * set gap between 3 wave, 0.5f for 1/2 default gap
     * @param gap
     */
    public void setWaveGap(float gap) {
        waveGap = gap;
    }

    public void stop() {
        if (objectAnimator == null) return;
        objectAnimator.cancel();
    }

    public void increaseWaveHeight(float among) {
        targetWaveHeight += among;
    }

    public void setWaveSlope(float slope) {
        waveSlope = slope;
    }

    public void setWaveColors(int colorWave1, int colorWave2, int colorWave3) {
        color1 = colorWave1;
        color2 = colorWave2;
        color3 = colorWave3;
    }

    public void setWaveColorPalette(WaveColorPalette c) {
        switch (c) {
            case BLUE:
                setWaveColors(getContext().getResources().getColor(R.color.wave_color_1)
                        , getContext().getResources().getColor(R.color.wave_color_2)
                        , getContext().getResources().getColor(R.color.wave_color_3));
                break;
            case GREEN:
                setWaveColors(getContext().getResources().getColor(R.color.wave_color_1_green)
                        , getContext().getResources().getColor(R.color.wave_color_2_green)
                        , getContext().getResources().getColor(R.color.wave_color_3_green));
                break;
            case PINK:
                setWaveColors(getContext().getResources().getColor(R.color.wave_color_1_pink)
                        , getContext().getResources().getColor(R.color.wave_color_2_pink)
                        , getContext().getResources().getColor(R.color.wave_color_3_pink));
                break;
        }
    }

    private void updateWave(float x) {
        alphaWave1 += 0.01f;
        if (alphaWave1 >= 2) alphaWave1 = 0;
        alphaWave2 += 0.02f;
        if (alphaWave2 >= 2) alphaWave2 = 0;
        alphaWave3 += 0.03f;
        if (alphaWave3 >= 2) alphaWave3 = 0;

        if (Utils.compare(currentWaveHeight, targetWaveHeight) == -1) currentWaveHeight += 0.04;
        else if (Utils.compare(currentWaveHeight, targetWaveHeight) == 1) currentWaveHeight -= 0.04;

        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        midPivotX = width / 2;
        midPivotY = height /2;
        ratio = (float)width/2;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawWave(canvas, color3, -0.25f*waveGap + currentWaveHeight, alphaWave1);
        drawWave(canvas, color2, -0.4f*waveGap + currentWaveHeight, alphaWave2);
        drawWave(canvas, color1, -0.5f*waveGap + currentWaveHeight, alphaWave3);
    }

    private void drawWave(Canvas canvas, int color, float height, float alphaX) {
        mWavePaint.setColor(color);
        for (float i = -3; i < 1; i += gap) {
            WavePoint[] points = new WavePoint[4];
            points[0] = new WavePoint(i + alphaX, 0 - 1f);
            points[1] = new WavePoint(i + gap + 0.01f + alphaX, 0 - 1f);
            points[2] = new WavePoint(i + gap + 0.01f + alphaX, getWaveY(i + gap) + height);
            points[3] = new WavePoint(i + alphaX, getWaveY(i) + height);
            WavePoint[] x = Utils.getRightAxis(midPivotX, midPivotY, ratio, points);
            Utils.drawPolygon(canvas, mWavePaint, x);
        }
    }

    private float getWaveY(float x) {
        float ret = 0.1f*waveSlope*(float)Math.sin(x*Math.PI);
        return ret;
    }

    float progress;

    public float getProgress() {
        return progress;
    }

    public void setProgress(float x) {
        updateWave(x);
        progress = x;
    }

    public enum WaveColorPalette {
        BLUE
        , PINK
        , GREEN
    }

}
