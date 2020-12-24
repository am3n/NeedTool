package ir.am3n.needtool

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.animation.*
import android.widget.ProgressBar
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible

fun ProgressBar.tintByColor(@ColorInt color: Int) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        val wrapDrawable: Drawable = DrawableCompat.wrap(indeterminateDrawable)
        DrawableCompat.setTint(wrapDrawable, color)
        indeterminateDrawable = DrawableCompat.unwrap(wrapDrawable)
    } else {
        indeterminateDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
    }
}

fun ProgressBar.tintByRes(@ColorRes colorRes: Int) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        val wrapDrawable: Drawable = DrawableCompat.wrap(indeterminateDrawable)
        DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(context, colorRes))
        indeterminateDrawable = DrawableCompat.unwrap(wrapDrawable)
    } else {
        indeterminateDrawable.setColorFilter(ContextCompat.getColor(context, colorRes), PorterDuff.Mode.SRC_IN)
    }
}

