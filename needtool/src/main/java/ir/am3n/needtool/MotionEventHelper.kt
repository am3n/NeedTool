package ir.am3n.needtool

import android.view.MotionEvent

val MotionEvent.isReleased: Boolean get() =
    action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL