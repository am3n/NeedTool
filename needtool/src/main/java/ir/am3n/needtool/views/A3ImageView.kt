package ir.am3n.needtool.views

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import ir.am3n.needtool.R
import ir.am3n.needtool.isRtl

class A3ImageView : AppCompatImageView {

    private var square: Int = -1
    private var lastSize = 0
    private var squareSize: Int = -1

    var isDirectionable: Boolean = true
        set(value) {
            field = value
            scaleX = if (value) {
                if (resources.isRtl) -1f else 1f
            } else 1f
        }


    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        build(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        build(context, attrs, defStyleAttr)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        scaleX = if (isDirectionable) {
            if (resources.isRtl) -1f else 1f
        } else 1f

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

    }

    private fun build(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {

        val ta: TypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.A3ImageView, defStyleAttr, 0)

        if (ta.hasValue(R.styleable.A3ImageView_a3_square) && square == -1)
            square = ta.getInt(R.styleable.A3ImageView_a3_square, 0)
        if (square < 0) square = 0

        if (ta.hasValue(R.styleable.A3ImageView_a3_squareSize) && squareSize == -1)
            squareSize = ta.getDimensionPixelSize(R.styleable.A3ImageView_a3_squareSize, 0)

        if (ta.hasValue(R.styleable.A3ImageView_a3_directionable))
            isDirectionable = ta.getBoolean(R.styleable.A3ImageView_a3_directionable, false)

    }

}