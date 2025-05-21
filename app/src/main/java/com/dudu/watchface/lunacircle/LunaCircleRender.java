package com.dudu.watchface.lunacircle;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.view.SurfaceHolder;
import java.util.Date;
import com.dudu.watchface.suncircle.R;

public abstract class LunaCircleRender {
    private Context context;
    public LunaCircleRender(Context context){
        this.context = context;
    }
    private static final int MSG_UPDATE_TIME = 0;
    private int timeRefreshCycle = 1000;
    private boolean visible;
    private boolean showCircle = true;
    private final Paint paint = new Paint();
    public final Paint blurPaint = new Paint();
    private final Paint timePaint = new Paint();
    private final Paint datePaint = new Paint();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private float circleY; // 圆的位置
    private float circleEndY; //圆最终位置
    private float screenHeight; // 屏幕高度
    private float screenWidth; // 屏幕宽度
    private boolean isAnimating = false; // 动画状态标记
    public abstract boolean isInAmbientMode();
    public abstract void redraw();
    public void init(int width, int height) {
        screenWidth = width;
        screenHeight = height;
        circleY = screenHeight;
        updateCircleY();
        blurPaint.setMaskFilter(new BlurMaskFilter(screenWidth*5/90f, BlurMaskFilter.Blur.NORMAL));
        Typeface timeFont = context.getResources().getFont(R.font.cmf_by_nothing_time_font_watch);
        timePaint.setTypeface(timeFont);
        timePaint.setColor(Color.WHITE);
        timePaint.setTextSize(screenWidth*0.2f);
        Typeface dateFont = context.getResources().getFont(R.font.inter);
        datePaint.setTypeface(dateFont);
        datePaint.setColor(Color.LTGRAY);
        datePaint.setTextSize(screenWidth/15f);
        if (!isInAmbientMode()) handler.postDelayed(this::startAnimation, 500);
    }
    public void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        timePaint.setAntiAlias(!isInAmbientMode());
        datePaint.setAntiAlias(!isInAmbientMode());
        blurPaint.setAntiAlias(!isInAmbientMode());
        Date date = new Date();
        int second = date.getSeconds();
        float radius = screenWidth * 0.4f;
        float centerX = screenWidth / 2;
        float centerY = circleY;
        Paint haloPaint = new Paint();
        haloPaint.setAntiAlias(true);
        haloPaint.setColor(Color.WHITE);
        int haloAlpha = 60 + (int)(Math.abs(Math.sin(second/60.0 * Math.PI))*120);
        haloPaint.setAlpha(haloAlpha);
        canvas.drawCircle(centerX, centerY, radius * 1.25f, haloPaint);
        RadialGradient gradient = new RadialGradient(
            centerX, centerY, radius,
            new int[]{Color.WHITE, Color.LTGRAY, Color.DKGRAY},
            new float[]{0.0f, 0.7f, 1.0f},
            Shader.TileMode.CLAMP
        );
        paint.setShader(gradient);
        canvas.drawCircle(centerX, centerY, radius, paint);
        paint.setShader(null);
        float fontMaxWidth = timePaint.measureText("0");
        Paint.FontMetrics fontMetrics = timePaint.getFontMetrics();
        float textHeight = fontMetrics.bottom - fontMetrics.top;
        float timeX = screenWidth * 0.22f;
        float timeY = screenHeight * 0.36f;
        String hourStr = formatTime(date.getHours());
        String minStr = formatTime(date.getMinutes());
        canvas.drawText(String.valueOf(hourStr.charAt(0)),timeX+(fontMaxWidth-timePaint.measureText(String.valueOf(hourStr.charAt(0))))/2,timeY,timePaint);
        canvas.drawText(String.valueOf(hourStr.charAt(1)),timeX+fontMaxWidth+(fontMaxWidth-timePaint.measureText(String.valueOf(hourStr.charAt(1))))/2,timeY,timePaint);
        canvas.drawText(String.valueOf(minStr.charAt(0)),timeX+(fontMaxWidth-timePaint.measureText(String.valueOf(minStr.charAt(0))))/2,timeY+textHeight*1.15f,timePaint);
        canvas.drawText(String.valueOf(minStr.charAt(1)),timeX+fontMaxWidth+(fontMaxWidth-timePaint.measureText(String.valueOf(minStr.charAt(1))))/2,timeY+textHeight*1.15f,timePaint);
        fontMetrics = datePaint.getFontMetrics();
        textHeight = fontMetrics.bottom - fontMetrics.top;
        timeX = screenWidth * 0.68f;
        timeY = screenHeight * 0.72f;
        canvas.drawText(getDay(date.getDay()), timeX, timeY, datePaint);
        canvas.drawText(formatTime(date.getDate()), timeX, timeY + textHeight * 0.85f, datePaint);
    }
    private String formatTime(int num) {
        if (num < 10) {
            return "0" + num;
        }
        return String.valueOf(num);
    }
    private String getDay(int day) {
        switch (day) {
            case 0:
                return "SUN";
            case 1:
                return "MON";
            case 2:
                return "TUE";
            case 3:
                return "WED";
            case 4:
                return "THU";
            case 5:
                return "FRI";
            case 6:
                return "SAT";
        }
        return "NULL";
    }
    private void startAnimation() {
        if (isAnimating) return; // 防止重复启动动画
        isAnimating = true;
        circleY = screenHeight; // 重置圆的位置
        updateAnimation();
    }
    private void updateAnimation() {
        if (!isBetween(circleY,circleEndY-30,circleEndY+30)) {
            circleY += screenWidth/(circleY>circleEndY?-30:30); // 每次上移 5 像素
            redraw(); // 触发重绘
        }
        handler.postDelayed(this::updateAnimation, (long)(screenHeight/50)); // 控制帧率
    }
    public boolean isBetween(float num, float min, float max) {
        return num >= min && num <= max;
    }
    public void onTimeTick() {
        updateCircleY();
        redraw();
    }
    private void updateCircleY() {
        Date date = new Date();
        float hour = date.getHours()+date.getMinutes()/60f;
        float hour12 = hour>12?24-hour:hour;
        circleEndY = screenHeight - screenHeight*hour12/12f;
    }
    public void onAmbientModeChanged(boolean enabled) {
        if (enabled) {
        } else {
            circleY = screenHeight;
            redraw();
            handler.postDelayed(this::startAnimation, 500);
        }
    }
    public void onDestroy() {
    }
    public void onVisibilityChanged() {
        redraw();
    }
} 