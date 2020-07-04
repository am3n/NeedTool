package ir.am3n.needtool

import android.os.Handler
import android.os.Looper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


private val IO_EXECUTOR: ExecutorService? = Executors.newSingleThreadExecutor()
fun onIO(func: () -> Unit) {
    IO_EXECUTOR?.execute(func)
}
fun onIO(func: () -> Unit, delay: Long) {
    if (delay <= 0)
        IO_EXECUTOR?.execute(func)
    else
        onUI({ IO_EXECUTOR?.execute(func) }, delay)
}
fun onIO(func: Runnable) {
    IO_EXECUTOR?.execute(func)
}
fun onIO(func: Runnable, delay: Long) {
    if (delay <= 0)
        IO_EXECUTOR?.execute(func)
    else
        onUI(Runnable { IO_EXECUTOR?.execute(func) }, delay)
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
fun offUI(func: () -> Unit) {
    UI_EXECUTER.removeCallbacks(func)
}
fun offUI(func: Runnable?) {
    if (func!=null)
        UI_EXECUTER.removeCallbacks(func)
}

