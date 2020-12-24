package ir.am3n.needtool.platekeyboard.keyboard.layouts

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.button.MaterialButton
import ir.am3n.needtool.R
import ir.am3n.needtool.platekeyboard.keyboard.KeyboardListener
import ir.am3n.needtool.platekeyboard.keyboard.controllers.KeyboardController
import ir.am3n.needtool.platekeyboard.utilities.ComponentUtils


abstract class KeyboardLayout(
    context: Context,
    private val controller: KeyboardController?,
    var hasNextFocus: Boolean = false
) : LinearLayout(context) {

    private var screenWidth = 0.0f
    internal var textSize = 23.0f

    private val Int.toDp: Int get() = (this / Resources.getSystem().displayMetrics.density).toInt()

    init {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun createKeyboard(screenWidth: Float = this.screenWidth) {
        removeAllViews()
        this.screenWidth = screenWidth

        val keyboardWrapper = createWrapperLayout()
        for (row in createRows()) {
            keyboardWrapper.addView(row)
        }
        addView(keyboardWrapper)
    }

    private fun createWrapperLayout(): LinearLayout {
        val wrapper = LinearLayout(context)
        val lp = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        lp.topMargin = 15.toDp
        lp.bottomMargin = 15.toDp
        wrapper.layoutParams = lp
        wrapper.orientation = VERTICAL
        return wrapper
    }

    private fun createButton(text: String, widthAsPctOfScreen: Float): Button {
        val button = AppCompatButton(ContextThemeWrapper(context, R.style.button))
        button.layoutParams = LayoutParams(
            (screenWidth * widthAsPctOfScreen).toInt(),
            LayoutParams.WRAP_CONTENT
        )
        button.textSize = textSize
        button.text = text
        return button
    }

    internal fun createButton(text: String, widthAsPctOfScreen: Float, c: Char): Button {
        val button = createButton(text, widthAsPctOfScreen)
        button.setOnClickListener { controller?.onKeyStroke(c) }
        return button
    }

    internal fun createButton(text: String, widthAsPctOfScreen: Float, key: KeyboardController.SpecialKey): Button {
        val button = createButton(text, widthAsPctOfScreen)
        button.setOnClickListener { controller?.onKeyStroke(key) }
        return button
    }

    internal fun createRow(buttons: List<View>): LinearLayout {
        val row = LinearLayout(context)
        row.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        orientation = HORIZONTAL
        row.gravity = Gravity.CENTER
        for (button in buttons) {
            row.addView(button)
        }
        return row
    }

    internal fun registerListener(listener: KeyboardListener) {
        controller?.registerListener(listener)
    }

    internal abstract fun createRows(): List<LinearLayout>

}
