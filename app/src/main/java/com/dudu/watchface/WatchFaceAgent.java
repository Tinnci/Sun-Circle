package com.dudu.watchface;

import android.content.Context;
import com.dudu.watchface.suncircle.SunCircleRender;
import com.dudu.watchface.lunacircle.LunaCircleRender;
import java.util.Calendar;
import com.dudu.watchface.suncircle.R;

public class WatchFaceAgent {
    public static Object getCurrentRender(Context context) {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 6 && hour < 18) {
            return new SunCircleRender(context) {
                @Override
                public boolean isInAmbientMode() { return false; }
                @Override
                public void redraw() {}
            };
        } else {
            return new LunaCircleRender(context) {
                @Override
                public boolean isInAmbientMode() { return false; }
                @Override
                public void redraw() {}
            };
        }
    }
} 