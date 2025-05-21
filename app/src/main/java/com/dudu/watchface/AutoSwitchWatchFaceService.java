package com.dudu.watchface;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import com.dudu.watchface.suncircle.SunCircleRender;
import com.dudu.watchface.lunacircle.LunaCircleRender;
import java.util.Calendar;

public class AutoSwitchWatchFaceService extends WallpaperService {
    @Override
    public Engine onCreateEngine() {
        return new AutoSwitchEngine();
    }

    private class AutoSwitchEngine extends Engine {
        private Object render;
        private int lastType = -1; // 0=sun, 1=luna
        private int width, height;
        private final Handler handler = new Handler(Looper.getMainLooper());
        private final Runnable drawRunnable = this::draw;

        private void updateRender() {
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            int type = (hour >= 6 && hour < 18) ? 0 : 1;
            if (render == null || type != lastType) {
                if (type == 0) {
                    render = new SunCircleRender(AutoSwitchWatchFaceService.this) {
                        @Override public boolean isInAmbientMode() { return false; }
                        @Override public void redraw() { handler.post(drawRunnable); }
                    };
                } else {
                    render = new LunaCircleRender(AutoSwitchWatchFaceService.this) {
                        @Override public boolean isInAmbientMode() { return false; }
                        @Override public void redraw() { handler.post(drawRunnable); }
                    };
                }
                lastType = type;
                if (width > 0 && height > 0) {
                    if (render instanceof SunCircleRender) {
                        ((SunCircleRender)render).init(width, height);
                    } else if (render instanceof LunaCircleRender) {
                        ((LunaCircleRender)render).init(width, height);
                    }
                }
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            super.onSurfaceChanged(holder, format, w, h);
            width = w;
            height = h;
            updateRender();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) handler.post(drawRunnable);
        }

        @Override
        public void onDestroy() {
            handler.removeCallbacks(drawRunnable);
            super.onDestroy();
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xStep, float yStep, int xPixels, int yPixels) {
            handler.post(drawRunnable);
        }

        @Override
        public void onTouchEvent(android.view.MotionEvent event) {
            handler.post(drawRunnable);
        }

        @Override
        public void onTimeTick() {
            handler.post(drawRunnable);
        }

        private void draw() {
            updateRender();
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null && render != null) {
                    if (render instanceof SunCircleRender) {
                        ((SunCircleRender)render).onDraw(canvas);
                    } else if (render instanceof LunaCircleRender) {
                        ((LunaCircleRender)render).onDraw(canvas);
                    }
                }
            } finally {
                if (canvas != null) holder.unlockCanvasAndPost(canvas);
            }
        }
    }
} 