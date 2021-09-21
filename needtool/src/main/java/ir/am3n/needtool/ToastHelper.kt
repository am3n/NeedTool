package ir.am3n.needtool

import android.content.Context
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.FontRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import ir.am3n.needtool.ToastHelper.defaultToastTypefaceRes

object ToastHelper {

    @FontRes
    var defaultToastTypefaceRes: Int? = null

}


private fun setFont(context: Context, toast: Toast, fontRes: Int?) {
    if (fontRes != null) {
        toast.apply {
            try {
                val view = view as LinearLayout
                val tv = view.getChildAt(0) as TextView
                val typeFace = ResourcesCompat.getFont(context, fontRes)
                tv.typeface = typeFace
            } catch (t: Throwable) {}
        }
    }
}


fun Context.toast(@StringRes resource: Int, duration: Int = Toast.LENGTH_SHORT, @FontRes fontRes: Int? = null) {
    Toast.makeText(this, resource, duration).apply {
        setFont(applicationContext, this, fontRes ?: defaultToastTypefaceRes)
    }.show()
}
fun Context.toast(text: String?, duration: Int = Toast.LENGTH_SHORT, @FontRes fontRes: Int? = null) {
    Toast.makeText(this, text.orEmpty(), duration).apply {
        setFont(applicationContext, this, fontRes ?: defaultToastTypefaceRes)
    }.show()
}

fun Context.toast(@StringRes resource: Int, duration: Int = Toast.LENGTH_SHORT) {
    toast(resource, Toast.LENGTH_SHORT, defaultToastTypefaceRes)
}
fun Fragment.toast(@StringRes resource: Int, duration: Int = Toast.LENGTH_SHORT) {
    context?.toast(resource, Toast.LENGTH_SHORT, defaultToastTypefaceRes)
}
fun Context.toast(text: String?, duration: Int = Toast.LENGTH_SHORT) {
    toast(text, Toast.LENGTH_SHORT, defaultToastTypefaceRes)
}
fun Fragment.toast(text: String?, duration: Int = Toast.LENGTH_SHORT) {
    context?.toast(text, Toast.LENGTH_SHORT, defaultToastTypefaceRes)
}

fun Context.toast(@StringRes resource: Int, @FontRes fontRes: Int? = null) {
    toast(resource, Toast.LENGTH_SHORT, fontRes)
}
fun Fragment.toast(@StringRes resource: Int, @FontRes fontRes: Int? = null) {
    context?.toast(resource, Toast.LENGTH_SHORT, fontRes)
}
fun Context.toast(text: String?, @FontRes fontRes: Int? = null) {
    toast(text, Toast.LENGTH_SHORT, fontRes)
}
fun Fragment.toast(text: String?, @FontRes fontRes: Int? = null) {
    context?.toast(text, Toast.LENGTH_SHORT, fontRes)
}

fun Context.toast(@StringRes resource: Int) {
    toast(resource, Toast.LENGTH_SHORT, defaultToastTypefaceRes)
}
fun Fragment.toast(@StringRes resource: Int) {
    context?.toast(resource, Toast.LENGTH_SHORT, defaultToastTypefaceRes)
}
fun Context.toast(text: String?) {
    toast(text, Toast.LENGTH_SHORT, defaultToastTypefaceRes)
}
fun Fragment.toast(text: String?) {
    context?.toast(text, Toast.LENGTH_SHORT, defaultToastTypefaceRes)
}



fun Context.ltoast(@StringRes resource: Int, @FontRes fontRes: Int? = null) {
    toast(resource, Toast.LENGTH_LONG, fontRes ?: defaultToastTypefaceRes)
}
fun Fragment.ltoast(@StringRes resource: Int, @FontRes fontRes: Int? = null) {
    context?.toast(resource, Toast.LENGTH_LONG, fontRes ?: defaultToastTypefaceRes)
}
fun Context.ltoast(text: String?, @FontRes fontRes: Int? = null) {
    toast(text, Toast.LENGTH_LONG, fontRes ?: defaultToastTypefaceRes)
}
fun Fragment.ltoast(text: String?, @FontRes fontRes: Int? = null) {
    context?.toast(text, Toast.LENGTH_LONG, fontRes ?: defaultToastTypefaceRes)
}
