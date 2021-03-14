package ir.am3n.needtool

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton

import androidx.appcompat.widget.AppCompatImageView

class SquareImageButton : AppCompatImageButton {

    private var square: Int = -1

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        build(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        build(context, attrs, defStyleAttr)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val size = if (square == 1) widthSize else if (square == 2) heightSize else square
        if (size > 0) {
            val finalMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)
            super.onMeasure(finalMeasureSpec, finalMeasureSpec)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    private fun build(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {

        val ta: TypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.SquareImageButton, defStyleAttr, 0)
        if (ta.hasValue(R.styleable.SquareImageButton_square) && square == -1)
            square = ta.getInt(R.styleable.SquareImageButton_square, 0)

    }

}