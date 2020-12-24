package ir.am3n.needtool

import android.app.Activity
import android.os.Build
import androidx.core.app.ActivityCompat
import kotlin.system.exitProcess

fun Activity.stopCompletely() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        finishAndRemoveTask()
    } else {
        ActivityCompat.finishAffinity(this)
    }
    try { exitProcess(0) } catch (t: Throwable) {}
}