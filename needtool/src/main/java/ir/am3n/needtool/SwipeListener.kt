package ir.am3n.needtool

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

@SuppressLint("ClickableViewAccessibility")
class SwipeListener(
    ctx: Context?,
    private val onSwipe: (Direction) -> Unit
) : View.OnTouchListener {

    companion object {
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }

    enum class Direction { LEFT, TOP, RIGHT, BOTTOM }

    private val gestureDetector: GestureDetector

    init {
        gestureDetector = GestureDetector(ctx, GestureListener())
    }


    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return false
        }
        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            var result = false
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > Companion.SWIPE_THRESHOLD && abs(velocityX) > Companion.SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipe(Direction.RIGHT)
                        } else {
                            onSwipe(Direction.LEFT)
                        }
                        result = true
                    }
                } else if (abs(diffY) > Companion.SWIPE_THRESHOLD && abs(velocityY) > Companion.SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipe(Direction.BOTTOM)
                    } else {
                        onSwipe(Direction.TOP)
                    }
                    result = true
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }
            return result
        }
    }

}


fun View.setSwipeListener(onSwipe: (SwipeListener.Direction) -> Unit) {
    val swipeTouchListener = SwipeListener(context) {
        onSwipe(it)
    }
    setOnTouchListener(swipeTouchListener)
}

