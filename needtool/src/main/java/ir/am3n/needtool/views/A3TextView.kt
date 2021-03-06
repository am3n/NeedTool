package ir.am3n.needtool.views

import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.*
import ir.am3n.needtool.R
import ir.am3n.needtool.isRtl

class A3TextView : AppCompatTextView {

    private var direction: Int? = null
    private var rtlized = false

    private var needRefresh = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        build(context, attrs, 0)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        build(context, attrs, 0)
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
            rtlize()
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun build(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {

        val ta: TypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.A3TextView, defStyleAttr, 0)

        if (ta.hasValue(R.styleable.A3TextView_a3_direction))
            direction = ta.getInt(R.styleable.A3TextView_a3_direction, 0)

        needRefresh = true
    }

    private fun rtlize() {

        if (context == null || resources == null) return
        rtlized = true
        needRefresh = false

        if (resources.isRtl) {

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

        }

    }

}