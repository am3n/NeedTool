package ir.am3n.needtool.views

import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import ir.am3n.needtool.R
import ir.am3n.needtool.isRtl

class A3ConstraintLayout : ConstraintLayout {

    private var square: Int = -1
    private var lastSize = 0
    private var squareSize: Int = -1

    private var rtlized = false

    var direction: Int? = null
        set(value) {
            field = value
            requestLayout()
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        build(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        build(context, attrs, defStyleAttr)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (square > 0 && squareSize == -1) {
            val size = if (square == 1) measuredWidth else measuredHeight
            if (lastSize < size) {
                lastSize = size
                try {
                    updateLayoutParams<RelativeLayout.LayoutParams> { width = size; height = size }
                } catch (t: Throwable) {
                    try {
                        updateLayoutParams<LinearLayout.LayoutParams> { width = size; height = size }
                    } catch (t: Throwable) {
                        try {
                            updateLayoutParams<LayoutParams> { width = size; height = size }
                        } catch (t: Throwable) {
                        }
                    }
                }
            }
        }

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (squareSize > 0) {
            try {
                updateLayoutParams<RelativeLayout.LayoutParams> { width = squareSize; height = squareSize }
            } catch (t: Throwable) {
                try {
                    updateLayoutParams<LinearLayout.LayoutParams> { width = squareSize; height = squareSize }
                } catch (t: Throwable) {
                    try {
                        updateLayoutParams<LayoutParams> { width = squareSize; height = squareSize }
                    } catch (t: Throwable) {
                    }
                }
            }
        }

        val isRtl = when (direction) {
            0 -> false
            1 -> true
            2 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) parent.layoutDirection == 1 else false
            3 -> resources.isRtl
            else -> false
        }
        if (!rtlized && isRtl) {
            rtlized = true
            rtlize(isRtl)
        }

    }

    private fun build(context: Context, attrs: AttributeSet?, defStyleAttr: Int? = null, defStyleRes: Int? = null) {

        val ta: TypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.A3ConstraintLayout, defStyleAttr ?: 0, 0)

        if (ta.hasValue(R.styleable.A3ConstraintLayout_a3_square) && square == -1)
            square = ta.getInt(R.styleable.A3ConstraintLayout_a3_square, 0)
        if (square < 0) square = 0

        if (ta.hasValue(R.styleable.A3ConstraintLayout_a3_squareSize) && squareSize == -1)
            squareSize = ta.getDimensionPixelSize(R.styleable.A3ConstraintLayout_a3_squareSize, 0)

        if (ta.hasValue(R.styleable.A3ConstraintLayout_a3_direction))
            direction = ta.getInt(R.styleable.A3ConstraintLayout_a3_direction, 0)

    }

    @Synchronized
    private fun rtlize(isRtl: Boolean) {
        if (isRtl) {

            for (i in 0 until childCount) {
                getChildAt(i).updateLayoutParams<LayoutParams> {

                    val _startToStart = startToStart
                    val _endToEnd = endToEnd
                    val _leftToLeft = leftToLeft
                    val _rightToRight = rightToRight
                    startToStart = _endToEnd
                    endToEnd = _startToStart
                    rightToRight = _leftToLeft
                    leftToLeft = _rightToRight

                    val _startToEnd = startToEnd
                    val _endToStart = endToStart
                    val _leftToRight = leftToRight
                    val _rightToLeft = rightToLeft
                    endToStart = _startToEnd
                    startToEnd = _endToStart
                    rightToLeft = _leftToRight
                    leftToRight = _rightToLeft

                    if ((this.leftMargin > 0 || this.rightMargin > 0) && this.leftMargin != this.rightMargin) {
                        val leftMargin = this.leftMargin
                        val rightMargin = this.rightMargin
                        updateMargins(left = rightMargin, right = leftMargin)
                    }

                    if ((getChildAt(i).paddingLeft > 0 || getChildAt(i).paddingRight > 0) && getChildAt(i).paddingLeft != getChildAt(i).paddingRight) {
                        val leftPadding = getChildAt(i).paddingLeft
                        val rightPadding = getChildAt(i).paddingRight
                        getChildAt(i).updatePadding(left = rightPadding, right = leftPadding)
                    }

                }
            }

        }
    }


}