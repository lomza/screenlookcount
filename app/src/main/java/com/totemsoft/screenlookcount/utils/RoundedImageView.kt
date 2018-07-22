package com.totemsoft.screenlookcount.utils

import android.content.Context
import android.graphics.*
import android.graphics.Bitmap.Config
import android.graphics.PorterDuff.Mode
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.widget.ImageView

/**
 * Tweaked circle-like ImageView.
 * The code was take from here - https://gist.github.com/melanke/7158342
 */
class RoundedImageView @JvmOverloads constructor
(ctx: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ImageView(ctx, attrs, defStyleAttr) {

    override fun onDraw(canvas: Canvas) {
        val drawable = drawable ?: return

        if (width == 0 || height == 0) {
            return
        }
        val bitmap = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)

        val roundBitmap = getCroppedBitmap(bitmap, width)
        canvas.drawBitmap(roundBitmap, 0f, 0f, null)
    }

    private fun getCroppedBitmap(bmp: Bitmap, radius: Int): Bitmap {
        val bitmapToDraw = if (bmp.width != radius || bmp.height != radius) {
            val smallest = Math.min(bmp.width, bmp.height).toFloat()
            val factor = smallest / radius
            Bitmap.createScaledBitmap(bmp, (bmp.width / factor).toInt(), (bmp.height / factor).toInt(), false)
        } else {
            bmp
        }

        val output = Bitmap.createBitmap(radius, radius,
                Config.ARGB_8888)
        val canvas = Canvas(output)

        val rect = Rect(0, 0, radius, radius)
        val paint = Paint()
        with(paint) {
            isAntiAlias = true
            isFilterBitmap = true
            isDither = true
            canvas.drawARGB(0, 0, 0, 0)
            canvas.drawCircle(radius / 2 + 0.7f,
                    radius / 2 + 0.7f, radius / 2 + 0.1f, this)
            xfermode = PorterDuffXfermode(Mode.SRC_IN)
            canvas.drawBitmap(bitmapToDraw, rect, rect, this)
        }

        return output
    }
}