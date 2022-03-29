package ir.am3n.needtool.views

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import ir.am3n.needtool.R
import ir.am3n.needtool.iDp2Px

class MaxHeightLinearLayout : A3LinearLayout {

    private var maxHeightPx = -1

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        build(context, attrs, 0)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
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
            attrs, R.styleable.MaxHeightLinearLayout, defStyleAttr, 0)

        if (ta.hasValue(R.styleable.MaxHeightLinearLayout_android_maxHeight))
            maxHeightPx = ta.getDimensionPixelSize(
                R.styleable.MaxHeightLinearLayout_android_maxHeight, 0)

        if (ta.hasValue(R.styleable.MaxHeightLinearLayout_a3_square) && square == -1)
            square = ta.getInt(R.styleable.MaxHeightLinearLayout_a3_square, 0)
        if (square < 0) square = 0

        if (ta.hasValue(R.styleable.MaxHeightLinearLayout_a3_squareSize) && squareSize == -1)
            squareSize = ta.getDimensionPixelSize(R.styleable.MaxHeightLinearLayout_a3_squareSize, 0)

        if (ta.hasValue(R.styleable.MaxHeightLinearLayout_a3_direction))
            direction = ta.getInt(R.styleable.MaxHeightLinearLayout_a3_direction, 0)

        if (ta.hasValue(R.styleable.MaxHeightLinearLayout_a3_paddingMiddle))
            paddingMiddle = ta.getDimensionPixelSize(R.styleable.MaxHeightLinearLayout_a3_paddingMiddle, 0).toFloat()

        needRefresh = true

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