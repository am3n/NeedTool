package ir.am3n.needtool.views

import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.*
import ir.am3n.needtool.R
import ir.am3n.needtool.isRtl
import ir.am3n.needtool.persianLetter

class A3TextView : AppCompatTextView {

    private var direction: Int? = null
    private var rtlized = false

    private var needRefresh = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        build(context, attrs, 0)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        build(context, attrs, defStyleAttr)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val isRtl = when (direction) {
            0 -> false
            1 -> true
            2 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) parent.layoutDirection==1 else false
            3 -> resources.isRtl
            else -> false
        }
        if (!rtlized && isRtl && needRefresh) {
            rtlize(isRtl)
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun build(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {

        val ta: TypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.A3TextView, defStyleAttr, 0)

        if (ta.hasValue(R.styleable.A3TextView_a3_direction))
            direction = ta.getInt(R.styleable.A3TextView_a3_direction, 0)

        needRefresh = true
    }

    private fun rtlize(isRtl: Boolean) {

        if (context == null) return
        rtlized = true
        needRefresh = false

        if (isRtl) {

            val leftMargin = marginLeft
            val rightMargin = marginRight
            try {
                updateLayoutParams<RelativeLayout.LayoutParams> { updateMargins(left = rightMargin, right = leftMargin) }
            } catch (t: Throwable) {
                try {
                    updateLayoutParams<LinearLayout.LayoutParams> { updateMargins(left = rightMargin, right = leftMargin) }
                } catch (t: Throwable) {
                    try {
                        updateLayoutParams<ConstraintLayout.LayoutParams> { updateMargins(left = rightMargin, right = leftMargin) }
                    } catch (t: Throwable) {}
                }
            }

            val leftPadding = paddingLeft
            val rightPadding = paddingRight
            updatePadding(left = rightPadding, right = leftPadding)

            if (gravity != Gravity.CENTER && gravity != Gravity.CENTER_HORIZONTAL) {
                when (gravity) {
                    Gravity.START -> gravity = Gravity.END
                    Gravity.START or Gravity.TOP -> gravity = Gravity.END or Gravity.TOP
                    Gravity.START or Gravity.BOTTOM -> gravity = Gravity.END or Gravity.BOTTOM
                    Gravity.START or Gravity.CENTER_VERTICAL -> gravity = Gravity.END or Gravity.CENTER_VERTICAL
                    Gravity.LEFT -> gravity = Gravity.RIGHT
                    Gravity.LEFT or Gravity.TOP -> gravity = Gravity.RIGHT or Gravity.TOP
                    Gravity.LEFT or Gravity.BOTTOM -> gravity = Gravity.RIGHT or Gravity.BOTTOM
                    Gravity.LEFT or Gravity.CENTER_VERTICAL -> gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
                    Gravity.END -> gravity = Gravity.START
                    Gravity.END or Gravity.TOP -> gravity = Gravity.START or Gravity.TOP
                    Gravity.END or Gravity.BOTTOM -> gravity = Gravity.START or Gravity.BOTTOM
                    Gravity.END or Gravity.CENTER_VERTICAL -> gravity = Gravity.START or Gravity.CENTER_VERTICAL
                    Gravity.RIGHT -> gravity = Gravity.LEFT
                    Gravity.RIGHT or Gravity.TOP -> gravity = Gravity.LEFT or Gravity.TOP
                    Gravity.RIGHT or Gravity.BOTTOM -> gravity = Gravity.LEFT or Gravity.BOTTOM
                    Gravity.RIGHT or Gravity.CENTER_VERTICAL -> gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
                }
            }

        }

    }

}