package ir.am3n.needtool

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.view.WindowManager

val statusBarHeight: Int
    get() {
        var result = 0
        val resourceId = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = Resources.getSystem().getDimensionPixelSize(resourceId)
        }
        return result
    }


val Context.actionBarHeight: Int
    get() {
        val styledAttributes = this.theme?.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
        val result = styledAttributes?.getDimension(0, 0f)?.toInt()
        styledAttributes?.recycle()
        return result ?: 0
    }


val navigaionBarHeight: Int
    get() {
        var navigationBarHeight = 0
        val resourceId = Resources.getSystem().getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            navigationBarHeight = Resources.getSystem().getDimensionPixelSize(resourceId)
        }
        return navigationBarHeight
    }


fun Context.getScreenSize(): Point {
    val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
    val display = wm?.defaultDisplay
    val size = Point()
    display?.getSize(size)
    return size
}

fun Context.getScreenWidth(): Int {
    return getScreenSize().x
}

fun Context.getScreenHeight(): Int {
    return getScreenSize().y
}