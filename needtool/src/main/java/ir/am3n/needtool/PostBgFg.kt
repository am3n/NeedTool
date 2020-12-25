package ir.am3n.needtool

import ir.am3n.needtool.AfterForegrounded.isInBackground
import java.lang.Thread.sleep


enum class When {
    BACKGROUNDED,
    FOREGROUNDED
}

data class AFRunnable(
    val runnable: Runnable,
    val runWhen: When,
    val onIO: Boolean
)

private val afRunnables = ArrayList<AFRunnable>()

fun runIfOrPost(runWhen: When = When.FOREGROUNDED, onIO: Boolean = false, runnable: Runnable) {
    if (isInBackground)
        afRunnables.add(AFRunnable(runnable, runWhen, onIO))
    else
        runnable.run()
}

fun postTo(runWhen: When = When.FOREGROUNDED, onIO: Boolean = false, runnable: Runnable) {
    afRunnables.add(AFRunnable(runnable, runWhen, onIO))
}

object AfterForegrounded {

    var isDestroyed = false

    var isInBackground = false
        set(value) {
            field = value
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