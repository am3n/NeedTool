package ir.am3n.needtool.platekeyboard.utilities

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.text.InputFilter
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.util.TypedValue
import android.view.WindowManager
import android.widget.EditText
import androidx.core.graphics.drawable.DrawableCompat

object ComponentUtils {

    const val DEFAULT_COMPONENT_HEIGHT_DP = 80

    fun Context.getScreenSize(): Point {
        val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        val display = wm?.defaultDisplay
        val size = Point()
        display?.getSize(size)
        return size
    }

    fun Context.getScreenWidth(): Int {
        return getScreenSize().x
    }

    fun Context.getScreenHeight(): Int {
        return getScreenSize().y
    }

    fun hideSystemKeyboard(context: Context, view: View) {
        view.windowToken?.let{
            val imm: InputMethodManager = context.getSystemService(
                Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun setBackgroundTint(view: View, color: Int) {
        val drawable: Drawable = DrawableCompat.wrap(view.background)
        DrawableCompat.setTint(drawable, color)
    }

    fun configureTextField(field: EditText, singleLine: Boolean, maxChars: Int) {
        if (singleLine) {
            field.maxLines = 1
            field.isSingleLine = true
        }
        field.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxChars))
    }

    fun dpToPx(context: Context, dp: Int): Int {
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics)

        val density = context.resources.displayMetrics.density
        return (px / density).toInt()
    }

    fun pxToDp(context: Context, px: Int): Int {
        val dp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_PX,
            px.toFloat(),
            context.resources.displayMetrics)

        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

}