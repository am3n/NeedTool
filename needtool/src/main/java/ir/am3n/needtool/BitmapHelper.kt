package ir.am3n.needtool

import android.graphics.*


val Bitmap.isDark: Boolean get() {
    val thresholdRate = .45f
    var darkThreshold = width * height * thresholdRate
    var darkPixels = 0
    val pixels = IntArray(width * height)
    getPixels(pixels, 0, width, 0, 0, width, height)
    for (pixel in pixels) {
        if (pixel.isDarkNotTrans)
            darkPixels++
        if (pixel.isTrans)
            darkThreshold -= thresholdRate
    }
    return darkPixels >= darkThreshold
}

fun Bitmap.tint(color: Int) {
    val paint = Paint()
    paint.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
    val canvas = Canvas(this)
    canvas.drawBitmap(this, 0f, 0f, paint)
}