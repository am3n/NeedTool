package ir.am3n.needtool

import android.content.Context
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.FontRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
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
fun Context.toast(text: String?, duration: Int = Toast.LENGTH_SHORT) {
    toast(text, Toast.LENGTH_SHORT, defaultToastTypefaceRes)
}

fun Context.toast(@StringRes resource: Int, @FontRes fontRes: Int? = null) {
    toast(resource, Toast.LENGTH_SHORT, fontRes)
}
fun Context.toast(text: String?, @FontRes fontRes: Int? = null) {
    toast(text, Toast.LENGTH_SHORT, fontRes)
}

fun Context.toast(@StringRes resource: Int) {
    toast(resource, Toast.LENGTH_SHORT, defaultToastTypefaceRes)
}
fun Context.toast(text: String?) {
    toast(text, Toast.LENGTH_SHORT, defaultToastTypefaceRes)
}



fun Context.ltoast(@StringRes resource: Int, @FontRes fontRes: Int? = null) {
    toast(resource, Toast.LENGTH_LONG, fontRes)
}
fun Context.ltoast(text: String?, @FontRes fontRes: Int? = null) {
    toast(text, Toast.LENGTH_LONG, fontRes)
}

fun Context.ltoast(@StringRes resource: Int) {
    toast(resource, Toast.LENGTH_LONG, defaultToastTypefaceRes)
}
fun Context.ltoast(text: String?) {
    toast(text, Toast.LENGTH_LONG, defaultToastTypefaceRes)
}
