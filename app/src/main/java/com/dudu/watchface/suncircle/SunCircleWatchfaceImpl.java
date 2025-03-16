package com.dudu.watchface.suncircle;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import com.dudu.wearlauncher.model.WatchFace;
import com.dudu.wearlauncher.utils.ILog;
import java.util.Date;

public class SunCircleWatchfaceImpl extends WatchFace {
    SunCircleRender render = new SunCircleRender(getContext()){
        @Override
        public boolean isInAmbientMode() {
            return false;
        }
        @Override
        public void redraw() {
            invalidate();
        }
        
    };
    
    public SunCircleWatchfaceImpl(Context context) {
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
