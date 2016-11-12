package com.totemsoft.screenlookcount.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

/**
 * Custom circle button which adjusts to screen's width, if its size is bigger than this screen's width.
 *
 * @author antonina.tkachuk
 */

public class CircleButton extends Button {
    private final Context context;

    public CircleButton(Context context) {
        super(context);
        this.context = context;
    }

    public CircleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public CircleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int radius = this.getMeasuredWidth();
        int finalSize = radius;
        int[] screenSize = Utils.getScreenSize(context);
        int optimalSize = Math.min(screenSize[0], screenSize[1]) * 2 / 3;

        if (radius > optimalSize) {
            finalSize = optimalSize;
            Log.d(C.TAG, "Circle button size downgraded to:" + optimalSize);
        }

        setMeasuredDimension(finalSize, finalSize);
    }
}