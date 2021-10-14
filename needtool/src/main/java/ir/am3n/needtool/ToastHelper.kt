package ir.am3n.needtool

import android.content.Context
import android.graphics.Typeface
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.FontRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import ir.am3n.needtool.ToastHelper.defaultToastTypeface

object ToastHelper {

    var defaultToastTypeface: Typeface? = null

}


private fun setFont(context: Context, toast: Toast, fontRes: Int?) {
    toast.apply {
        try {
            val view = view as LinearLayout
            val tv = view.getChildAt(0) as TextView
            val typeFace = if (fontRes != null) ResourcesCompat.getFont(context, fontRes) else defaultToastTypeface
            tv.typeface = typeFace
        } catch (t: Throwable) {}
    }
}


fun Context.toast(@StringRes resource: Int, duration: Int = Toast.LENGTH_SHORT, @FontRes fontRes: Int? = null) {
    Toast.makeText(this, resource, duration).apply {
        setFont(applicationContext, this, fontRes)
    }.show()
}
fun Context.toast(text: String?, duration: Int = Toast.LENGTH_SHORT, @FontRes fontRes: Int? = null) {
    Toast.makeText(this, text.orEmpty(), duration).apply {
        setFont(applicationContext, this, fontRes)
    }.show()
}

fun Fragment.toast(@StringRes resource: Int, duration: Int = Toast.LENGTH_SHORT, @FontRes fontRes: Int? = null) {
    context?.toast(resource, duration, fontRes)
}
fun Fragment.toast(text: String?, duration: Int = Toast.LENGTH_SHORT, @FontRes fontRes: Int? = null) {
    context?.toast(text, duration, fontRes)
}


fun Context.ltoast(@StringRes resource: Int, @FontRes fontRes: Int? = null) {
    toast(resource, Toast.LENGTH_LONG, fontRes)
}
fun Context.ltoast(text: String?, @FontRes fontRes: Int? = null) {
    toast(text, Toast.LENGTH_LONG, fontRes)
}
fun Fragment.ltoast(@StringRes resource: Int, @FontRes fontRes: Int? = null) {
    context?.toast(resource, Toast.LENGTH_LONG, fontRes)
}
fun Fragment.ltoast(text: String?, @FontRes fontRes: Int? = null) {
    context?.toast(text, Toast.LENGTH_LONG, fontRes)
}
