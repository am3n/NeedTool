package ir.am3n.needtool

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View.LAYOUT_DIRECTION_RTL
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.text.layoutDirection

import java.lang.reflect.Field
import java.util.*

fun Context.drawable(@DrawableRes drawableResource: Int): Drawable? = ContextCompat.getDrawable(this, drawableResource)
fun Context.color(@ColorRes color: Int) = ContextCompat.getColor(this, color)
fun Context.string(@StringRes string: Int): String = getString(string)

fun Resources.string(@StringRes string: Int): String = getString(string)


val String.asColor: Int get() = try {
    Color.parseColor(this)
} catch (t: Throwable) {
    t.printStackTrace()
    Color.WHITE
}

val Int.asStateList: ColorStateList get() = ColorStateList.valueOf(this)

fun getResId(resName: String?, c: Class<*>): Int {
    return try {
        val idField: Field = c.getDeclaredField(resName!!)
        idField.getInt(idField)
    } catch (e: Exception) {
        -1
    }
}

fun Resources.currentLocale(): Locale {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        configuration.locales[0]
    } else {
        configuration.locale
    }
}

val Resources.isRtl: Boolean get() = currentLocale().layoutDirection == LAYOUT_DIRECTION_RTL


fun String.lc(context: Context): String =
    toLowerCase(context.resources?.currentLocale()?: Locale.US)

fun String.lc(resources: Resources): String =
    toLowerCase(resources.currentLocale())