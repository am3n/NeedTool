package ir.am3n.needtool.views

import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import ir.am3n.needtool.R
import ir.am3n.needtool.isRtl

class A3RelativeLayout : RelativeLayout {

    private var square: Int = -1
    private var lastSize = 0
    private var squareSize: Int = -1

    private var direction: Int? = null
    private var rtlized = false

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        build(context, attrs, 0)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        build(context, attrs, 0)
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        build(context, attrs, 0)
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
                            updateLayoutParams<ConstraintLayout.LayoutParams> { width = size; height = size }
                        } catch (t: Throwable) {}
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
                        updateLayoutParams<ConstraintLayout.LayoutParams> { width = squareSize; height = squareSize }
                    } catch (t: Throwable) {}
                }
            }
        }

        val isRtl = when (direction) {
            0 -> false
            1 -> true
            2 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) parent.layoutDirection==1 else false
            3 -> resources.isRtl
            else -> false
        }
        if (!rtlized && isRtl) {
            rtlized = true
            rtlize()
        }

    }

    private fun build(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) {

        if (context == null) return

        val ta: TypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.A3RelativeLayout, defStyleAttr, 0)

        if (ta.hasValue(R.styleable.A3RelativeLayout_a3_square) && square == -1)
            square = ta.getInt(R.styleable.A3RelativeLayout_a3_square, 0)
        if (square < 0) square = 0

        if (ta.hasValue(R.styleable.A3RelativeLayout_a3_squareSize) && squareSize == -1)
            squareSize = ta.getDimensionPixelSize(R.styleable.A3RelativeLayout_a3_squareSize, 0)

        if (ta.hasValue(R.styleable.A3RelativeLayout_a3_direction))
            direction = ta.getInt(R.styleable.A3RelativeLayout_a3_direction, 0)

    }

    private fun rtlize() {
        if (resources.isRtl) {

            for (i in 0 until childCount) {
                getChildAt(i).updateLayoutParams<LayoutParams> {

                    val alignParentRight = rules[ALIGN_PARENT_RIGHT]
                    val alignParentLeft = rules[ALIGN_PARENT_LEFT]
                    addRule(ALIGN_PARENT_LEFT, alignParentRight)
                    addRule(ALIGN_PARENT_RIGHT, alignParentLeft)

                    val leftOf = rules[LEFT_OF]
                    val rightOf = rules[RIGHT_OF]
                    addRule(RIGHT_OF, leftOf)
                    addRule(LEFT_OF, rightOf)

                    val alignRight = rules[ALIGN_RIGHT]
                    val alignLeft = rules[ALIGN_LEFT]
                    addRule(ALIGN_RIGHT, alignLeft)
                    addRule(ALIGN_LEFT, alignRight)

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

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
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

        }
    }

}