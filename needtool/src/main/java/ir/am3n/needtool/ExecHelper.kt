package ir.am3n.needtool

import android.os.Handler
import android.os.Looper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.thread


fun onIO(func: () -> Unit) {
    thread(block = func)
}
fun onIO(func: () -> Unit, delay: Long) {
    if (delay <= 0)
        onIO(func)
    else
        onUI({ onIO(func) }, delay)
}
fun onIO(func: () -> Unit, delay: Int) {
    onIO(func, delay.toLong())
}
fun onIO(func: Runnable) {
    thread { func.run() }
}
fun onIO(func: Runnable, delay: Long) {
    if (delay <= 0)
        onIO(func)
    else
        onUI(Runnable { onIO(func) }, delay)
}
fun onIO(func: Runnable, delay: Int) {
    onIO(func, delay.toLong())
}


private val UI_EXECUTER = Handler(Looper.getMainLooper())
fun onUI(func: () -> Unit) {
    UI_EXECUTER.post(func)
}
fun onUI(func: () -> Unit, delay: Long) {
    if (delay <= 0)
        func.invoke()
    else
        UI_EXECUTER.postDelayed(func, delay)
}
fun onUI(func: () -> Unit, delay: Int) {
    onUI(func, delay.toLong())
}
fun onUI(func: Runnable?) {
    if (func!=null)
        UI_EXECUTER.post(func)
}
fun onUI(func: Runnable?, delay: Long) {
    if (delay <= 0)
        func?.run()
    else if (func != null)
        UI_EXECUTER.postDelayed(func, delay)
}
fun onUI(func: Runnable?, delay: Int) {
    onUI(func, delay.toLong())
}

fun offUI(func: Runnable?) {
    if (func!=null)
        UI_EXECUTER.removeCallbacks(func)
}

