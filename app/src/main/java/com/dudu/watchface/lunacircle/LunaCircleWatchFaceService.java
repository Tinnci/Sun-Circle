package com.dudu.watchface.lunacircle;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import androidx.wear.watchface.CanvasWatchFaceService;
import androidx.wear.watchface.WatchFaceStyle;

public class LunaCircleWatchFaceService extends CanvasWatchFaceService {
    @Override
    public Engine onCreateEngine() {
        return new MyEngine();
    }

    private class MyEngine extends CanvasWatchFaceService.Engine {
        LunaCircleRender render = new LunaCircleRender(LunaCircleWatchFaceService.this){
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
        }
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