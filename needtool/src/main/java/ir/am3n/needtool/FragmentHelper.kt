package ir.am3n.needtool

import android.view.KeyEvent
import android.view.View


interface CustomFragment {
    fun onBackPressed()
}

fun View.onBackPressed(customFragment: CustomFragment?) {
    this.isFocusableInTouchMode = true
    this.requestFocus()
    this.setOnKeyListener { _, keyCode, event ->
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            customFragment?.onBackPressed()
            return@setOnKeyListener true
        }
        return@setOnKeyListener false
    }
}

fun View.onBackPressed(callback: () -> Unit) {
    this.isFocusableInTouchMode = true
    this.requestFocus()
    this.setOnKeyListener { _, keyCode, event ->
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            callback()
            return@setOnKeyListener true
        }
        return@setOnKeyListener false
    }
}





