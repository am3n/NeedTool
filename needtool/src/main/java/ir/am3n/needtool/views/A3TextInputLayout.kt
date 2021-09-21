package ir.am3n.needtool.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Paint
import android.graphics.Typeface
import android.text.*
import android.text.style.MetricAffectingSpan
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.textfield.TextInputLayout
import ir.am3n.needtool.R
import ir.am3n.needtool.iDp2Px

class A3TextInputLayout @JvmOverloads constructor(
    private val mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextInputLayout(mContext, attrs, defStyleAttr) {

    private var hideErrorText = false
    private var errorTextTypeface: Typeface? = null

    init {

        val ta: TypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.A3TextInputLayout, defStyleAttr, 0)

        if (ta.hasValue(R.styleable.A3TextInputLayout_a3_errorTextTypeface))
            errorTextTypeface = ResourcesCompat.getFont(mContext, ta.getResourceId(R.styleable.A3TextInputLayout_a3_errorTextTypeface, 0))

    }

    override fun setError(error: CharSequence?) {

        hideErrorText = error?.length==0

        if (error!=null)
            isErrorEnabled = true

        var spannable: Spannable? = null
        if (!TextUtils.isEmpty(error))
            spannable = wrapInCustomFont(error?.toString() ?: "")
        super.setError(spannable)

        if (error==null)
            isErrorEnabled = false
    }

    override fun setErrorEnabled(enabled: Boolean) {
        super.setErrorEnabled(enabled)
        if (enabled && hideErrorText)
            try {
                getChildAt(1).visibility = View.GONE
            } catch (e: Exception) {
                e.printStackTrace()
            }
    }

    private fun wrapInCustomFont(text: String): Spannable {
        val spannable = SpannableString(text)
        if (errorTextTypeface != null)
            spannable.setSpan(TypefaceSpan(errorTextTypeface), 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannable
    }

    fun errorHeight(): Int {
        return if (getChildAt(1)?.measuredHeight ?: 16.iDp2Px > 0)
            getChildAt(1).measuredHeight
        else
            16.iDp2Px
    }

    class TypefaceSpan(private val mTypeface: Typeface?) : MetricAffectingSpan() {
        override fun updateMeasureState(p: TextPaint) {
            p.typeface = mTypeface
            p.flags = p.flags or Paint.SUBPIXEL_TEXT_FLAG
        }
        override fun updateDrawState(tp: TextPaint) {
            tp.typeface = mTypeface
            tp.flags = tp.flags or Paint.SUBPIXEL_TEXT_FLAG
        }
    }

}