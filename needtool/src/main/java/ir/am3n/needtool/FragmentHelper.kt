package ir.am3n.needtool

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

abstract class BackPressHandler : Fragment() {
    var created: Long = System.currentTimeMillis()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        created = System.currentTimeMillis()
        return null
    }
    abstract fun onBackPressed(): Boolean
    val fm: FragmentManager? get() = activity?.supportFragmentManager
}

fun FragmentActivity.consumeBackPress(): Boolean {
    var consumed = false
    supportFragmentManager.fragments
        .filterIsInstance<BackPressHandler>()
        .sortedByDescending { it.created }
        .forEach {
            if (!consumed) {
                it.onBackPressed().let { flag -> consumed = flag }
            }
        }
    return consumed
}



fun View.onBackPressed(backPressHandler: BackPressHandler?) {
    this.isFocusableInTouchMode = true
    this.requestFocus()
    this.setOnKeyListener { _, keyCode, event ->
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            backPressHandler?.onBackPressed()
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
