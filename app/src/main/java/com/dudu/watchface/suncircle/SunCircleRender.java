package com.dudu.watchface.suncircle;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.view.SurfaceHolder;
import java.util.Date;

public abstract class SunCircleRender {
    private Context context;
    public SunCircleRender(Context context){
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
    /*private final Handler timeHandler =
            new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == MSG_UPDATE_TIME) {
                        redraw();
                        sendEmptyMessageDelayed(MSG_UPDATE_TIME, timeRefreshCycle);
                    }
                }
            };*/
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
        // handler.postDelayed(this::startAnimation,1000);
        Typeface timeFont = context.getResources().getFont(R.font.cmf_by_nothing_time_font_watch);
        timePaint.setTypeface(timeFont);
        timePaint.setColor(Color.WHITE);
        timePaint.setTextSize(screenWidth*0.2f);

        Typeface dateFont = context.getResources().getFont(R.font.inter);
        datePaint.setTypeface(dateFont);
        datePaint.setColor(Color.WHITE);
        datePaint.setTextSize(screenWidth/15f);
        //timeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
        if (!isInAmbientMode()) handler.postDelayed(this::startAnimation, 500);
    }

    

    public void onDraw(Canvas canvas) {
        // 清空背景
        canvas.drawColor(Color.BLACK);

        // 绘制模糊圆
        timePaint.setAntiAlias(!isInAmbientMode());
        datePaint.setAntiAlias(!isInAmbientMode());
        blurPaint.setAntiAlias(!isInAmbientMode());
        if (!isInAmbientMode()) {
            blurPaint.setColor(Color.parseColor("#FB471F"));
            canvas.drawCircle(screenWidth / 2 - screenWidth*2/45, circleY + screenWidth/5, screenWidth/2, blurPaint);
        }
        // handler.postDelayed(this::startAnimation,1000);

        float fontMaxWidth = timePaint.measureText("0");

        Paint.FontMetrics fontMetrics = timePaint.getFontMetrics();
        Date date = new Date();
        float textHeight = fontMetrics.bottom - fontMetrics.top;
        float timeX = screenWidth * 0.22f;
        float timeY = screenHeight * 0.36f;

        canvas.drawText(String.valueOf(formatTime(date.getHours()).charAt(0)),timeX+(fontMaxWidth-timePaint.measureText(String.valueOf(formatTime(date.getHours()).charAt(0))))/2,timeY,timePaint);
        canvas.drawText(String.valueOf(formatTime(date.getHours()).charAt(1)),timeX+fontMaxWidth+(fontMaxWidth-timePaint.measureText(String.valueOf(formatTime(date.getHours()).charAt(1))))/2,timeY,timePaint);
        canvas.drawText(String.valueOf(formatTime(date.getMinutes()).charAt(0)),timeX+(fontMaxWidth-timePaint.measureText(String.valueOf(formatTime(date.getMinutes()).charAt(0))))/2,timeY+textHeight*1.15f,timePaint);
        canvas.drawText(String.valueOf(formatTime(date.getMinutes()).charAt(1)),timeX+fontMaxWidth+(fontMaxWidth-timePaint.measureText(String.valueOf(formatTime(date.getMinutes()).charAt(1))))/2,timeY+textHeight*1.15f,timePaint);

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
            //handler.postDelayed(this::updateAnimation, (long)(screenHeight/50)); // 控制帧率
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
        if (enabled) {/*
            timeRefreshCycle = 60000;
            handler.removeCallbacksAndMessages(null);
            redraw();*/
        } else {
            //timeRefreshCycle = 1000;
            circleY = screenHeight;
            redraw();
            handler.postDelayed(this::startAnimation, 500);
        }
    }

    public void onDestroy() {
        //handler.removeCallbacksAndMessages(null); // 停止所有动画
        //timeHandler.removeCallbacksAndMessages(null);
    }
    public void onVisibilityChanged() {
    	redraw();
    }
}
