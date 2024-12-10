package ir.am3n.needtool

import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.animation.*
import android.widget.ProgressBar
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.findViewTreeLifecycleOwner
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import okhttp3.Dispatcher

fun ProgressBar.tintByColor(@ColorInt color: Int) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        val wrapDrawable: Drawable = DrawableCompat.wrap(indeterminateDrawable)
        DrawableCompat.setTint(wrapDrawable, color)
        indeterminateDrawable = DrawableCompat.unwrap(wrapDrawable)
    } else {
        indeterminateDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
    }
}

fun ProgressBar.tintByRes(@ColorRes colorRes: Int) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        val wrapDrawable: Drawable = DrawableCompat.wrap(indeterminateDrawable)
        DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(context, colorRes))
        indeterminateDrawable = DrawableCompat.unwrap(wrapDrawable)
    } else {
        indeterminateDrawable.setColorFilter(ContextCompat.getColor(context, colorRes), PorterDuff.Mode.SRC_IN)
    }
}


fun View.delayOnLifecycle(
    duration: Long,
    dispatcher: CoroutineDispatcher = Main,
    block: () -> Unit
): Job? = findViewTreeLifecycleOwner()?.let { lifecycleOwner ->
    lifecycleOwner.lifecycle.coroutineScope.launch(dispatcher) {
        withContext(Dispatchers.IO) { delay(duration) }
        block()
    }
}

fun View.delayOnLifecycleSuspended(
    duration: Long,
    dispatcher: CoroutineDispatcher = Main,
    block: suspend () -> Unit
): Job? = findViewTreeLifecycleOwner()?.let { lifecycleOwner ->
    lifecycleOwner.lifecycle.coroutineScope.launch(dispatcher) {
        withContext(Dispatchers.IO) { delay(duration) }
        block()
    }
}

/** Defines bounds of displayed view and check is it contains [Point]
 * @param event Touch motion event
 * @return `true` if view bounds contains point, `false` - otherwise
 */
fun View.isPointInsideViewBounds(event: MotionEvent): Boolean {
    val touchPoint = Point(Math.round(event.rawX), Math.round(event.rawY))
    return this.isPointInsideViewBounds(touchPoint)
}

/** Defines bounds of displayed view and check is it contains [Point]
 * @param point Point to check inside bounds *
 * @return `true` if view bounds contains point, `false` - otherwise
 */
fun View.isPointInsideViewBounds(point: Point): Boolean =
    Rect().run {
        getDrawingRect(this)
        IntArray(2).also { locationOnScreen ->
            getLocationOnScreen(locationOnScreen)
            offset(locationOnScreen[0], locationOnScreen[1])
        }
        contains(point.x, point.y)
    }