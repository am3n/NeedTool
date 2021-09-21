package ir.am3n.needtool

import android.app.Activity
import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import android.os.Build
import androidx.annotation.RequiresApi
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


@RequiresApi(Build.VERSION_CODES.KITKAT)
fun Activity.clearData() {
    try {
        (getSystemService(ACTIVITY_SERVICE) as ActivityManager?)?.clearApplicationUserData()
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}