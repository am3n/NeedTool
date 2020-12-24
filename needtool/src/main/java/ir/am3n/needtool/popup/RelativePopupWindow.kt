package ir.am3n.needtool.popup

import android.animation.Animator
import android.annotation.TargetApi
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.widget.PopupWindow
import androidx.annotation.IntDef
import androidx.core.widget.PopupWindowCompat
import kotlin.math.hypot
import kotlin.math.max

open class RelativePopupWindow : PopupWindow() {

    @IntDef(
        VerticalPosition.CENTER,
        VerticalPosition.ABOVE,
        VerticalPosition.BELOW,
        VerticalPosition.ALIGN_TOP,
        VerticalPosition.ALIGN_BOTTOM
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class VerticalPosition {
        companion object {
            const val CENTER = 0
            const val ABOVE = 1
            const val BELOW = 2
            const val ALIGN_TOP = 3
            const val ALIGN_BOTTOM = 4
        }
    }

    @IntDef(
        HorizontalPosition.CENTER,
        HorizontalPosition.LEFT,
        HorizontalPosition.RIGHT,
        HorizontalPosition.ALIGN_LEFT,
        HorizontalPosition.ALIGN_RIGHT
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class HorizontalPosition {
        companion object {
            const val CENTER = 0
            const val LEFT = 1
            const val RIGHT = 2
            const val ALIGN_LEFT = 3
            const val ALIGN_RIGHT = 4
        }
    }

    private var anchor: View? = null

    /**
     * Show at relative position to anchor View.
     *
     * @param anchor      Anchor View
     * @param vertPos     Vertical Position Flag
     * @param horizPos    Horizontal Position Flag
     * @param fitInScreen Automatically fit in screen or not
     */
    fun showOnAnchor(anchor: View, @VerticalPosition vertPos: Int, @HorizontalPosition horizPos: Int, fitInScreen: Boolean) {
        showOnAnchor(anchor, vertPos, horizPos, 0, 0, fitInScreen)
    }


    /**
     * Show at relative position to anchor View.
     *
     * @param anchor   Anchor View
     * @param vertPos  Vertical Position Flag
     * @param horizPos Horizontal Position Flag
     */
    fun showOnAnchor(anchor: View, @VerticalPosition vertPos: Int, @HorizontalPosition horizPos: Int, fitInScreen: Boolean, horizontalMargin: Int, verticalMargin: Int) {
        showOnAnchor(anchor, vertPos, horizPos, 0, 0, fitInScreen, horizontalMargin, verticalMargin)
    }

    /**
     * Show at relative position to anchor View with translation.
     *
     * @param anchor      Anchor View
     * @param vertPos     Vertical Position Flag
     * @param horizPos    Horizontal Position Flag
     * @param x           Translation X
     * @param y           Translation Y
     * @param fitInScreen Automatically fit in screen or not
     */
    @JvmOverloads
    fun showOnAnchor(anchor: View, @VerticalPosition vertPos: Int, @HorizontalPosition horizPos: Int, x: Int = 0, y: Int = 0, fitInScreen: Boolean = true) {
        showOnAnchor(anchor, vertPos, horizPos, x, y, fitInScreen, 0, 0)
    }

    /**
     * Show at relative position to anchor View with translation.
     *
     * @param anchor      Anchor View
     * @param vertPos     Vertical Position Flag
     * @param horizPos    Horizontal Position Flag
     * @param x           Translation X
     * @param y           Translation Y
     * @param fitInScreen Automatically fit in screen or not
     */
    open fun showOnAnchor(anchor: View, @VerticalPosition vertPos: Int, @HorizontalPosition horizPos: Int, x: Int, y: Int, fitInScreen: Boolean, horizontalMargin: Int, verticalMargin: Int) {
        this.anchor = anchor
        var x = x
        var y = y
        isClippingEnabled = fitInScreen
        val contentView = contentView
        contentView.measure(makeDropDownMeasureSpec(width), makeDropDownMeasureSpec(height))
        val measuredW = contentView.measuredWidth
        val measuredH = contentView.measuredHeight
        if (!fitInScreen) {
            val anchorLocation = IntArray(2)
            anchor.getLocationInWindow(anchorLocation)
            x += anchorLocation[0]
            y += anchorLocation[1] + anchor.height
        }
        when (vertPos) {
            VerticalPosition.ABOVE -> {
                y -= measuredH + anchor.height
                y += verticalMargin
            }
            VerticalPosition.ALIGN_BOTTOM -> {
                y -= measuredH
                y += verticalMargin
            }
            VerticalPosition.CENTER ->
                y -= anchor.height / 2 + measuredH / 2
            VerticalPosition.ALIGN_TOP -> {
                y -= anchor.height
                y -= verticalMargin
            }
            VerticalPosition.BELOW ->
                // Default position.
                y -= verticalMargin
        }
        when (horizPos) {
            HorizontalPosition.LEFT -> {
                x -= measuredW
                x += horizontalMargin
            }
            HorizontalPosition.ALIGN_RIGHT -> {
                x -= measuredW - anchor.width
                x += horizontalMargin
            }
            HorizontalPosition.CENTER ->
                x += anchor.width / 2 - measuredW / 2
            HorizontalPosition.ALIGN_LEFT ->
                // Default position.
                x -= horizontalMargin
            HorizontalPosition.RIGHT -> {
                x += anchor.width
                x -= horizontalMargin
            }
        }
        if (fitInScreen) {
            PopupWindowCompat.showAsDropDown(this, anchor, x, y, Gravity.NO_GRAVITY)
        } else {
            showAtLocation(anchor, Gravity.NO_GRAVITY, x, y)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            contentView.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                    contentView.removeOnLayoutChangeListener(this)
                    circularReveal(anchor, true)
                }
            })
        }

    }

    private fun makeDropDownMeasureSpec(measureSpec: Int): Int {
        return View.MeasureSpec.makeMeasureSpec(
            View.MeasureSpec.getSize(measureSpec),
            getDropDownMeasureSpecMode(measureSpec)
        )
    }

    private fun getDropDownMeasureSpecMode(measureSpec: Int): Int {
        return when (measureSpec) {
            ViewGroup.LayoutParams.WRAP_CONTENT -> View.MeasureSpec.UNSPECIFIED
            else -> View.MeasureSpec.EXACTLY
        }
    }



    override fun dismiss() {
        if (anchor != null) {
            circularReveal(anchor!!, false) {
                super.dismiss()
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun circularReveal(anchor: View, show: Boolean, dismiss: (() -> Unit)? = null) {
        val contentView = contentView

        val myLocation = IntArray(2)
        val anchorLocation = IntArray(2)
        contentView.getLocationOnScreen(myLocation)
        anchor.getLocationOnScreen(anchorLocation)
        val cx = anchorLocation[0] - myLocation[0] + anchor.width / 2
        val cy = anchorLocation[1] - myLocation[1] + anchor.height / 2

        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val dx = max(cx, contentView.measuredWidth - cx)
        val dy = max(cy, contentView.measuredHeight - cy)
        val finalRadius = hypot(dx.toDouble(), dy.toDouble()).toFloat()

        val animator = ViewAnimationUtils.createCircularReveal(contentView, cx, cy,
            if (show) 0f else finalRadius, if (show) finalRadius else 0f)
        animator.interpolator = if (show) AccelerateDecelerateInterpolator() else AccelerateInterpolator()
        animator.duration = 200
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                dismiss?.invoke()
            }
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationRepeat(animation: Animator?) {}
        })
        animator.start()

    }

    interface Listener {
        fun onShow()
        fun onDismiss()
    }

}