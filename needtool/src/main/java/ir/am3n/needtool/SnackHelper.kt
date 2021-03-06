package ir.am3n.needtool

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import com.google.android.material.snackbar.Snackbar


/**
 * CALLI    not set any typeface
 * AUTO     read "assets/fonts/" dir for .ttf fonts. if exists set it to textviews
 * PATH     get typeface by provided @param fontPath
 */
enum class SnackFont {
    CALLI,
    AUTO,
    PATH
}


fun Context.snack(
    view: View?, text: String?, duration: Int = Snackbar.LENGTH_LONG,
    font: SnackFont = SnackFont.CALLI, fontPath: String? = null, @FontRes fontRes: Int? = null,
    @ColorRes textColor: Int = 0, textSizeSp: Float = 0f,
    @ColorRes backgroundColor: Int = 0,
    actionText: String? = "", @ColorRes actionTextColor: Int = 0, action: () -> Unit = {}
): Snackbar? {
    if (view == null || text == null) return null

    val snackbar = Snackbar.make(view, text, duration)

    if (actionText?.isNotEmpty() == true) {
        snackbar.setAction(actionText) { action.invoke() }
        snackbar.setActionTextColor(
            when {
                actionTextColor != 0 -> color(actionTextColor)
                else -> fetchAccentColor(this) ?: Color.CYAN
            }
        )
    }

    val snackbarView = snackbar.view
    if (backgroundColor != 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            snackbarView.backgroundTintList = ColorStateList.valueOf(color(backgroundColor))
        else
            snackbarView.setBackgroundColor(color(backgroundColor))

    snackbarView.findViewById<TextView?>(com.google.android.material.R.id.snackbar_text)?.apply {
        if (textColor != 0)
            setTextColor(color(textColor))
        if (textSizeSp != 0f)
            textSize = textSizeSp
    }

    (if (font==SnackFont.PATH && fontPath!=null) fontPath else if (font==SnackFont.AUTO) fetchFontPath(view.context) else null)?.let {
        Typeface.createFromAsset(assets, it)?.let { typeface ->
            snackbarView.findViewById<TextView?>(com.google.android.material.R.id.snackbar_text)?.typeface = typeface
            snackbarView.findViewById<TextView?>(com.google.android.material.R.id.snackbar_action)?.typeface = typeface
        }
    }

    if (fontRes != null) {
        ResourcesCompat.getFont(view.context, fontRes)?.let { typeface ->
            snackbarView.findViewById<TextView?>(com.google.android.material.R.id.snackbar_text)?.typeface = typeface
            snackbarView.findViewById<TextView?>(com.google.android.material.R.id.snackbar_action)?.typeface = typeface
        }
    }

    ViewCompat.setLayoutDirection(snackbar.view, ViewCompat.LAYOUT_DIRECTION_RTL)

    snackbar.show()

    return snackbar
}

fun snack(
    view: View?, text: String?, duration: Int = Snackbar.LENGTH_LONG,
    font: SnackFont = SnackFont.CALLI, fontPath: String? = null, @FontRes fontRes: Int? = null,
    @ColorInt textColor: Int = 0, textSizeSp: Float = 0f,
    @ColorInt backgroundColor: Int = 0,
    actionText: String? = "", @ColorInt actionTextColor: Int = 0, action: () -> Unit = {}
): Snackbar? {
    if (view == null || text == null) return null

    val snackbar = Snackbar.make(view, text, duration)

    if (actionText?.isNotEmpty() == true) {
        snackbar.setAction(actionText) { action.invoke() }
        snackbar.setActionTextColor(
            when {
                actionTextColor != 0 -> actionTextColor
                view.context != null -> fetchAccentColor(view.context) ?: Color.CYAN
                else -> Color.CYAN
            }
        )
    }

    val snackbarView = snackbar.view
    if (backgroundColor != 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            snackbarView.backgroundTintList = ColorStateList.valueOf(backgroundColor)
        else
            snackbarView.setBackgroundColor(backgroundColor)

    snackbarView.findViewById<TextView?>(com.google.android.material.R.id.snackbar_text)?.apply {
        if (textColor != 0)
            setTextColor(textColor)
        if (textSizeSp != 0f)
            textSize = textSizeSp
    }

    (if (font==SnackFont.PATH && fontPath!=null) fontPath else if (font==SnackFont.AUTO) fetchFontPath(view.context) else null)?.let {
        Typeface.createFromAsset(view.context.assets, it)?.let { typeface ->
            snackbarView.findViewById<TextView?>(com.google.android.material.R.id.snackbar_action)?.typeface = typeface
            snackbarView.findViewById<TextView?>(com.google.android.material.R.id.snackbar_text)?.typeface = typeface
        }
    }

    if (fontRes != null) {
        ResourcesCompat.getFont(view.context, fontRes)?.let { typeface ->
            snackbarView.findViewById<TextView?>(com.google.android.material.R.id.snackbar_text)?.typeface = typeface
            snackbarView.findViewById<TextView?>(com.google.android.material.R.id.snackbar_action)?.typeface = typeface
        }
    }

    ViewCompat.setLayoutDirection(snackbar.view, ViewCompat.LAYOUT_DIRECTION_RTL)

    snackbar.show()

    return snackbar
}



private fun fetchFontPath(context: Context): String? {
    try {
        val list = context.assets.list("fonts/")
        if (list!!.isNotEmpty()) {
            for (file in list) {
                if (file.contains(".ttf"))
                    return "fonts/$file"
            }
        }
    } catch (t: Throwable) {
        return null
    }
    return null
}

private fun fetchAccentColor(context: Context): Int? {
    try {
        val typedValue = TypedValue()
        val a: TypedArray = context.obtainStyledAttributes(typedValue.data, intArrayOf(android.R.attr.colorAccent))
        val color = a.getColor(0, 0)
        a.recycle()
        return color
    } catch (t: Throwable) {}
    return null
}
