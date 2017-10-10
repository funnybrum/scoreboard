package com.brum.scoreboard.common;

import android.content.Context;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.TextView;

public class Utils {
    public static int getScreenWidth(WindowManager windowManager) {
        return windowManager.getDefaultDisplay().getWidth();
    }

    public static int getScreenHeight(WindowManager windowManager) {
        return windowManager.getDefaultDisplay().getHeight();
    }
    
    public static int measureTextHeight(String text,
                                        int fieldWidth,
                                        int fontSize,
                                        Context context) {
        TextView tv = new TextView(context);
        tv.setWidth(fieldWidth);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        tv.setText(text);
        tv.measure(0, 0);
        return tv.getMeasuredHeight();
    }
    
    public static int measureTextWidth(String text,
                                       int fontSize,
                                       Context context) {
        TextView tv = new TextView(context);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        tv.setText(text);
        tv.measure(0, 0);
        return tv.getMeasuredWidth();
    }
}
