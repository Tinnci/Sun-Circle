package com.dudu.watchface.lunacircle;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.View.LayerType;
import com.dudu.watchface.WatchFace;

public class LunaCircleWatchfaceImpl extends WatchFace {
    LunaCircleRender render = new LunaCircleRender(getContext()){
        @Override
        public boolean isInAmbientMode() {
            return false;
        }
        @Override
        public void redraw() {
            invalidate();
        }
    };
    public LunaCircleWatchfaceImpl(Context context) {
        super(context);
        setLayerType(View.LAYER_TYPE_SOFTWARE,render.blurPaint);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        render.init(getMeasuredWidth(),getMeasuredHeight());
    }
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.onDraw(canvas);
        render.onDraw(canvas);
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        render.onDestroy();
    }
    @Override
    public void updateTime() {
        super.updateTime();
        render.onTimeTick();
    }
} 