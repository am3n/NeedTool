package ir.am3n.needtool.views

import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity.*
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.*
import ir.am3n.needtool.R
import ir.am3n.needtool.isRtl
import kotlin.math.roundToInt

open class A3LinearLayout : LinearLayoutCompat {

    protected var square: Int = -1
    private var lastSize = 0
    protected var squareSize: Int = -1

    protected var paddingMiddle: Float? = null

    protected var needRefresh = false

    var direction: Int? = null
        set(value) {
            field = value
            requestLayout()
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        build(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        build(context, attrs, defStyleAttr)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        refresh()

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
                        } catch (t: Throwable) {
                        }
                    }
                }
            }
        }

    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (paddingMiddle != null) {
            val count = childCount - 1
            for (i in 0 until count) {
                View(context).let {
                    it.tag = "divider"
                    addView(it, i * 2 + 1)
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
                    } catch (t: Throwable) {
                    }
                }
            }
        }

    }

    private fun build(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {

        val ta: TypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.A3LinearLayout, defStyleAttr, 0)

        if (ta.hasValue(R.styleable.A3LinearLayout_a3_square) && square == -1)
            square = ta.getInt(R.styleable.A3LinearLayout_a3_square, 0)
        if (square < 0) square = 0

        if (ta.hasValue(R.styleable.A3LinearLayout_a3_squareSize) && squareSize == -1)
            squareSize = ta.getDimensionPixelSize(R.styleable.A3LinearLayout_a3_squareSize, 0)

        if (ta.hasValue(R.styleable.A3LinearLayout_a3_direction))
            direction = ta.getInt(R.styleable.A3LinearLayout_a3_direction, 0)

        if (ta.hasValue(R.styleable.A3LinearLayout_a3_paddingMiddle))
            paddingMiddle = ta.getDimensionPixelSize(R.styleable.A3LinearLayout_a3_paddingMiddle, 0).toFloat()

        needRefresh = true

    }

    @Synchronized
    private fun refresh() {

        if (context == null || childCount == 0) return

        if (!needRefresh) return
        needRefresh = false

        val childs = mutableListOf<View>()
        val count = childCount
        for (i in 0 until count) {
            childs.add(getChildAt(i))
        }

        if (paddingMiddle != null) {
            childs.filter { it.tag == "divider" }.forEach { child ->
                child.updateLayoutParams<LayoutParams> {
                    width = paddingMiddle!!.roundToInt()
                    height = 0
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

        if (isRtl) {

            childs.filter { it.tag != "divider" }.forEach { child ->

                val marginLeft = child.marginLeft
                val marginRight = child.marginRight

                child.updateLayoutParams<LayoutParams> {
                    if (marginLeft != 0 || marginRight != 0) {
                        updateMargins(left = marginRight, right = marginLeft)
                    }
                }

                val paddingLeft = child.paddingLeft
                val paddingRight = child.paddingRight
                if (paddingLeft != 0 || paddingRight != 0) {
                    child.updatePadding(left = paddingRight, right = paddingLeft)
                }

                if (this@A3LinearLayout.orientation == VERTICAL) {
                    if (child is TextView) {
                        when (child.gravity) {
                            START -> child.gravity = RIGHT
                            START or TOP -> child.gravity = RIGHT or TOP
                            START or BOTTOM -> child.gravity = RIGHT or BOTTOM
                            START or CENTER_VERTICAL -> child.gravity = RIGHT or CENTER_VERTICAL
                            LEFT -> child.gravity = RIGHT
                            LEFT or TOP -> child.gravity = RIGHT or TOP
                            LEFT or BOTTOM -> child.gravity = RIGHT or BOTTOM
                            LEFT or CENTER_VERTICAL -> child.gravity = RIGHT or CENTER_VERTICAL
                            END -> child.gravity = LEFT
                            END or TOP -> child.gravity = LEFT or TOP
                            END or BOTTOM -> child.gravity = LEFT or BOTTOM
                            END or CENTER_VERTICAL -> child.gravity = LEFT or CENTER_VERTICAL
                            RIGHT -> child.gravity = LEFT
                            RIGHT or TOP -> child.gravity = LEFT or TOP
                            RIGHT or BOTTOM -> child.gravity = LEFT or BOTTOM
                            RIGHT or CENTER_VERTICAL -> child.gravity = LEFT or CENTER_VERTICAL
                        }
                    }
                }

            }

            removeAllViews()

            if (orientation == HORIZONTAL) {

                childs.reversed().forEach { view ->
                    addView(view)
                }

                if (gravity != CENTER && gravity != CENTER_HORIZONTAL) {
                    when (gravity) {
                        START -> gravity = END
                        START or TOP -> gravity = END or TOP
                        START or BOTTOM -> gravity = END or BOTTOM
                        START or CENTER_VERTICAL -> gravity = END or CENTER_VERTICAL
                        LEFT -> gravity = RIGHT
                        LEFT or TOP -> gravity = RIGHT or TOP
                        LEFT or BOTTOM -> gravity = RIGHT or BOTTOM
                        LEFT or CENTER_VERTICAL -> gravity = RIGHT or CENTER_VERTICAL
                        END -> gravity = START
                        END or TOP -> gravity = START or TOP
                        END or BOTTOM -> gravity = START or BOTTOM
                        END or CENTER_VERTICAL -> gravity = START or CENTER_VERTICAL
                        RIGHT -> gravity = LEFT
                        RIGHT or TOP -> gravity = LEFT or TOP
                        RIGHT or BOTTOM -> gravity = LEFT or BOTTOM
                        RIGHT or CENTER_VERTICAL -> gravity = LEFT or CENTER_VERTICAL
                    }
                }

            } else {
                childs.forEach { view ->
                    addView(view)
                }
            }

        }

    }

}