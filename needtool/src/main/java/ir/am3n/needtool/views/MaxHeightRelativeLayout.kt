package ir.am3n.needtool.views

import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import ir.am3n.needtool.R
import ir.am3n.needtool.iDp2Px

class MaxHeightRelativeLayout : A3RelativeLayout {

    private var maxHeightPx = -1

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        build(context, attrs, 0)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        build(context, attrs, defStyleAttr)
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        build(context, attrs, defStyleAttr)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (maxHeightPx >= 0) {
            val maxHeightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeightPx, MeasureSpec.AT_MOST)
            super.onMeasure(widthMeasureSpec, maxHeightMeasureSpec)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    private fun build(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) {

        if (context == null) return

        val ta: TypedArray = context.theme.obtainStyledAttributes(
            attrs, R.styleable.MaxHeightRelativeLayout, defStyleAttr, 0)

        if (ta.hasValue(R.styleable.MaxHeightRelativeLayout_android_maxHeight))
            maxHeightPx = ta.getDimensionPixelSize(
                R.styleable.MaxHeightRelativeLayout_android_maxHeight, 0)

        if (ta.hasValue(R.styleable.MaxHeightRelativeLayout_a3_square) && square == -1)
            square = ta.getInt(R.styleable.MaxHeightRelativeLayout_a3_square, 0)
        if (square < 0) square = 0

        if (ta.hasValue(R.styleable.MaxHeightRelativeLayout_a3_squareSize) && squareSize == -1)
            squareSize = ta.getDimensionPixelSize(R.styleable.MaxHeightRelativeLayout_a3_squareSize, 0)

        if (ta.hasValue(R.styleable.MaxHeightRelativeLayout_a3_direction))
            direction = ta.getInt(R.styleable.MaxHeightRelativeLayout_a3_direction, 0)

    }

    fun setMaxHeightPx(maxHeightPx: Int) {
        this.maxHeightPx = maxHeightPx
        invalidate()
        requestLayout()
    }

    fun setMaxHeightDp(maxHeightDp: Int) {
        setMaxHeightPx(maxHeightDp.iDp2Px)
    }

}