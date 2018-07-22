package com.totemsoft.screenlookcount.utils

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.widget.Button

/**
 * Custom circle button which adjusts to screen's width, if its size is bigger than this screen's width.
 *
 * @author Antonina
 */

class CircleButton : Button {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val radius = this.measuredWidth
        var finalSize = radius
        val screenSize = context.getScreenSize()
        val optimalSize = Math.min(screenSize[0], screenSize[1]) * 2 / 3

        if (radius > optimalSize) {
            finalSize = optimalSize
            Log.d(C.TAG, "Circle button size downgraded to:$optimalSize")
        }

        setMeasuredDimension(finalSize, finalSize)
        // after measurements are set, center text
        gravity = Gravity.CENTER
    }
}