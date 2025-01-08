package ir.am3n.needtool

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import androidx.core.content.ContextCompat.getSystemService


@SuppressLint("WrongConstant")
fun Context.collpasePanel() {
    try {
        val sbservice: Any = getSystemService("statusbar")
        val statusbarManager: Class<*> = Class.forName("android.app.StatusBarManager")
        val showsb: Method = statusbarManager.getMethod("collapsePanels")
        showsb.invoke(sbservice)
    } catch (t: Throwable) {}
}


@SuppressLint("WrongConstant")
fun Context.expandPanel() {
    try {
        val sbservice: Any = getSystemService("statusbar")
        val statusbarManager = Class.forName("android.app.StatusBarManager")
        val showsb: Method = statusbarManager.getMethod("expandPanels")
        showsb.invoke(sbservice)
    } catch (t: Throwable) {}
}