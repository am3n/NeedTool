package ir.am3n.needtool

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.coroutineScope
import ir.am3n.needtool.AfterForegrounded.isInBackground
import kotlinx.coroutines.launch
import java.lang.Thread.sleep


enum class When {
    BACKGROUNDED,
    FOREGROUNDED
}

data class AFRunnable(
    val runnable: Runnable,
    val owner: LifecycleOwner?,
    val runWhen: When,
    val onIO: Boolean
)

private val afRunnables = ArrayList<AFRunnable>()

fun runIfOrPost(runWhen: When = When.FOREGROUNDED, onIO: Boolean = false, runnable: Runnable) {
    if (isInBackground)
        afRunnables.add(AFRunnable(runnable, null, runWhen, onIO))
    else
        runnable.run()
}

fun runIfOrPost(owner: LifecycleOwner, runWhen: When = When.FOREGROUNDED, onIO: Boolean = false, runnable: Runnable) {
    if (isInBackground)
        afRunnables.add(AFRunnable(runnable, owner, runWhen, onIO))
    else
        runnable.run()
}

fun postTo(runWhen: When = When.FOREGROUNDED, onIO: Boolean = false, runnable: Runnable) {
    afRunnables.add(AFRunnable(runnable, null, runWhen, onIO))
}

object AfterForegrounded {

    var isDestroyed = false

    var isInBackground = false
        set(value) {
            field = value
            if (!value)
                isDestroyed = false
            onIO {
                try {
                    sleep(500)

                    for (i in 0 until afRunnables.size) {
                        try {
                            if (afRunnables[i].runWhen == When.FOREGROUNDED && !value) {
                                try {
                                    afRunnables[i].let {
                                        if (it.onIO)
                                            onIO(it.runnable)
                                        else if (it.owner?.lifecycle?.currentState != Lifecycle.State.DESTROYED)
                                            onUI(it.runnable)
                                    }
                                } catch (t: Throwable) {
                                    t.printStackTrace()
                                }
                                try {
                                    afRunnables.removeAt(i)
                                } catch (t: Throwable) {
                                    t.printStackTrace()
                                }

                            } else if (afRunnables[i].runWhen == When.BACKGROUNDED && value) {
                                try {
                                    afRunnables[i].let {
                                        if (it.onIO)
                                            onIO(it.runnable)
                                        else
                                            onUI(it.runnable)
                                    }
                                } catch (t: Throwable) {
                                    t.printStackTrace()
                                }
                                try {
                                    afRunnables.removeAt(i)
                                } catch (t: Throwable) {
                                    t.printStackTrace()
                                }
                            }
                        } catch (t: Throwable) {
                            t.printStackTrace()
                        }
                    }

                } catch (t: Throwable) {
                    t.printStackTrace()
                }
            }
        }

}