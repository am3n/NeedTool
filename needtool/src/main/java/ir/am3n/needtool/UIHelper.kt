package ir.am3n.needtool

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager

fun Window.hideNavAndStus() {
    val flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            //View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            //View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_IMMERSIVE or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    this.decorView.systemUiVisibility = flags
    this.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
        if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
            this.decorView.systemUiVisibility = flags
        }
    }
}


fun Context.showKeyboard() = try {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
} catch (e: Exception) {
    e.printStackTrace()
}

fun Context.hideKeyboard(view: View?) = try {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.hideSoftInputFromWindow(view?.windowToken, 0)
} catch (e: Exception) {
    e.printStackTrace()
}



fun Context.minimizeApp() {
    val startMain = Intent(Intent.ACTION_MAIN)
    startMain.addCategory(Intent.CATEGORY_HOME)
    startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    startActivity(startMain)
}

