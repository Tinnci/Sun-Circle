package com.dudu.watchface.suncircle;
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

public class SunCircleWatchFaceService extends CanvasWatchFaceService {

    @Override
    public Engine onCreateEngine() {
        return new MyEngine();
    }

    private class MyEngine extends CanvasWatchFaceService.Engine {
        SunCircleRender render = new SunCircleRender(SunCircleWatchFaceService.this){
            @Override
            public boolean isInAmbientMode() {
                return MyEngine.this.isInAmbientMode();
            }
            @Override
            public void redraw() {
                invalidate();
            }
            
        };
        
        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            render.init(width,height);
        }
        @Override
        public void onVisibilityChanged(boolean visible) {
            render.onVisibilityChanged();
            /*if (visible) {
                circleY = screenHeight;
                invalidate();
                handler.postDelayed(this::startAnimation,1000);
            } else {
                // 停止动画以节省资源
                handler.removeCallbacksAndMessages(null);
                isAnimating = false;
            }*//*
            this.visible = visible;
            if (visible) {
                timeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            } else {
                timeHandler.removeMessages(MSG_UPDATE_TIME);
            }*/
        }/*
        @Override
        public void onSurfaceCreated(SurfaceHolder arg0) {
            super.onSurfaceCreated(arg0);
        }
        */
        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            render.onDraw(canvas);
        }
        @Override
        public void onAmbientModeChanged(boolean enabled) {
            render.onAmbientModeChanged(enabled);
        }
        
        @Override
        public void onDestroy() {
            render.onDestroy();
        }
        @Override
        public void onTimeTick() {
            render.onTimeTick();
        }
        
    }
}