package ir.am3n.needtool

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

import java.lang.reflect.Field

fun Context.drawable(@DrawableRes drawableResource: Int): Drawable? = ContextCompat.getDrawable(this, drawableResource)
fun Context.color(@ColorRes color: Int) = ContextCompat.getColor(this, color)
fun Context.string(@StringRes string: Int): String = getString(string)

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