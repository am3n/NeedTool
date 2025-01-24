package ir.am3n.needtool

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.FontRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar


/**
 * CALLI    not set any typeface
 * AUTO     read "assets/fonts/" dir for .ttf fonts. if exists set it to snackview
 * PATH     get typeface by provided @param fontPath
 */
enum class SnackFont {
    CALLI,
    AUTO,
    PATH
}


enum class Direction { LTR, RTL, INHERIT, LOCALE }

fun Context.snack(
    view: View?,
    direction: Direction = Direction.INHERIT,
    text: String?,
    duration: Int = Snackbar.LENGTH_LONG,
    gravity: Int = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM,
    font: SnackFont = SnackFont.CALLI,
    fontPath: String? = null,
    @FontRes fontRes: Int? = null,
    @ColorRes textColor: Int = 0,
    textSizeSp: Float = 0f,
    @ColorRes backgroundColor: Int = 0,
    @BaseTransientBottomBar.AnimationMode animation: Int? = null,
    actionText: String? = "",
    @ColorRes actionTextColor: Int = 0,
    action: () -> Unit = {}
): Snackbar? {

    if (view == null || text == null) return null

    val snackbar = Snackbar.make(view, text, duration)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        snackbar.view.translationZ = 100.fDp2Px
    }

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
    if (backgroundColor != 0) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            snackbarView.backgroundTintList = ColorStateList.valueOf(color(backgroundColor))
        }
    }

    snackbarView.findViewById<TextView?>(com.google.android.material.R.id.snackbar_text)?.apply {
        if (textColor != 0) {
            setTextColor(color(textColor))
        }
        if (textSizeSp != 0f) {
            textSize = textSizeSp
        }
    }

    (if (font == SnackFont.PATH && fontPath != null) fontPath
    else if (font == SnackFont.AUTO) fetchFontPath(view.context)
    else null)?.let {
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

    if (snackbar.view.layoutParams is CoordinatorLayout.LayoutParams) {
        snackbar.view.updateLayoutParams<CoordinatorLayout.LayoutParams> {
            this.gravity = gravity
        }
    } else if (snackbar.view.layoutParams is FrameLayout.LayoutParams) {
        snackbar.view.updateLayoutParams<FrameLayout.LayoutParams> {
            this.gravity = gravity
        }
    }

    ViewCompat.setLayoutDirection(snackbar.view, when (direction) {
        Direction.LTR -> ViewCompat.LAYOUT_DIRECTION_LTR
        Direction.RTL -> ViewCompat.LAYOUT_DIRECTION_RTL
        Direction.INHERIT -> ViewCompat.LAYOUT_DIRECTION_INHERIT
        else -> ViewCompat.LAYOUT_DIRECTION_LOCALE
    })

    if (animation != null) {
        snackbar.animationMode = animation
    }

    snackbar.show()

    return snackbar
}

fun snack(
    view: View?,
    direction: Direction = Direction.INHERIT,
    text: String?,
    duration: Int = Snackbar.LENGTH_LONG,
    gravity: Int = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM,
    font: SnackFont = SnackFont.CALLI,
    fontPath: String? = null,
    @FontRes fontRes: Int? = null,
    @ColorInt textColor: Int = 0,
    textSizeSp: Float = 0f,
    @ColorInt backgroundColor: Int = 0,
    @BaseTransientBottomBar.AnimationMode animation: Int? = null,
    actionText: String? = "",
    @ColorInt actionTextColor: Int = 0,
    action: () -> Unit = {}
): Snackbar? {

    if (view == null || text == null) return null

    val snackbar = Snackbar.make(view, text, duration)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        snackbar.view.translationZ = 100.fDp2Px
    }

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
    if (backgroundColor != 0) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            snackbarView.backgroundTintList = ColorStateList.valueOf(backgroundColor)
        }
    }

    snackbarView.findViewById<TextView?>(com.google.android.material.R.id.snackbar_text)?.apply {
        if (textColor != 0)
            setTextColor(textColor)
        if (textSizeSp != 0f)
            textSize = textSizeSp
    }

    (if (font == SnackFont.PATH && fontPath != null) fontPath
    else if (font == SnackFont.AUTO) fetchFontPath(view.context)
    else null)?.let {
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

    if (snackbar.view.layoutParams is CoordinatorLayout.LayoutParams) {
        snackbar.view.updateLayoutParams<CoordinatorLayout.LayoutParams> {
            this.gravity = gravity
        }
    } else if (snackbar.view.layoutParams is FrameLayout.LayoutParams) {
        snackbar.view.updateLayoutParams<FrameLayout.LayoutParams> {
            this.gravity = gravity
        }
    }

    ViewCompat.setLayoutDirection(snackbar.view, when (direction) {
        Direction.LTR -> ViewCompat.LAYOUT_DIRECTION_LTR
        Direction.RTL -> ViewCompat.LAYOUT_DIRECTION_RTL
        Direction.INHERIT -> ViewCompat.LAYOUT_DIRECTION_INHERIT
        else -> ViewCompat.LAYOUT_DIRECTION_LOCALE
    })

    if (animation != null) {
        snackbar.animationMode = animation
    }

    snackbar.show()

    return snackbar
}


private fun fetchFontPath(context: Context): String? {
    try {
        val list = context.assets.list("fonts/")
        if (list!!.isNotEmpty()) {
            for (file in list) {
                if (file.contains(".ttf")) {
                    return "fonts/$file"
                }
            }
        }
    } catch (_: Throwable) {
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
    } catch (_: Throwable) {
    }
    return null
}
