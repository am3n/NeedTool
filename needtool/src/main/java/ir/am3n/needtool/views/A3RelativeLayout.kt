package ir.am3n.needtool.views

import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
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

                    if (rules[ALIGN_PARENT_RIGHT] != 0) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            removeRule(ALIGN_PARENT_RIGHT)
                        } else {
                            addRule(ALIGN_PARENT_RIGHT, 0)
                        }
                        addRule(ALIGN_PARENT_LEFT, 1)
                    } else if (rules[ALIGN_PARENT_LEFT] != 0) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            removeRule(ALIGN_PARENT_LEFT)
                        } else {
                            addRule(ALIGN_PARENT_LEFT, 0)
                        }
                        addRule(ALIGN_PARENT_RIGHT, 1)
                    }

                    if (rules[LEFT_OF] != 0) {
                        val leftOf = rules[LEFT_OF]
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            removeRule(LEFT_OF)
                        } else {
                            addRule(LEFT_OF, 0)
                        }
                        addRule(RIGHT_OF, leftOf)
                    } else if (rules[RIGHT_OF] != 0) {
                        val rightOf = rules[RIGHT_OF]
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            removeRule(RIGHT_OF)
                        } else {
                            addRule(RIGHT_OF, 0)
                        }
                        addRule(LEFT_OF, rightOf)
                    }

                    if (rules[ALIGN_RIGHT] != 0 && rules[ALIGN_LEFT] != 0) {
                        val alignRight = rules[ALIGN_RIGHT]
                        val alignLeft = rules[ALIGN_LEFT]
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            removeRule(ALIGN_RIGHT)
                        } else {
                            addRule(ALIGN_RIGHT, 0)
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            removeRule(ALIGN_LEFT)
                        } else {
                            addRule(ALIGN_LEFT, 0)
                        }
                        addRule(ALIGN_RIGHT, alignLeft)
                        addRule(ALIGN_LEFT, alignRight)
                    } else if (rules[ALIGN_RIGHT] != 0) {
                        val alignRight = rules[ALIGN_RIGHT]
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            removeRule(ALIGN_RIGHT)
                        } else {
                            addRule(ALIGN_RIGHT, 0)
                        }
                        addRule(ALIGN_LEFT, alignRight)
                    } else if (rules[ALIGN_LEFT] != 0) {
                        val alignLeft = rules[ALIGN_LEFT]
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            removeRule(ALIGN_LEFT)
                        } else {
                            addRule(ALIGN_LEFT, 0)
                        }
                        addRule(ALIGN_RIGHT, alignLeft)
                    }

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