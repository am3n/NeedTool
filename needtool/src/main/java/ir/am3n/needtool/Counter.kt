package ir.am3n.needtool

class Counter(
    var milliseconds: Long,
    var interval: Long,
    var onTick: (time: Long, finished: Boolean) -> Unit
) : Thread() {

    private var timer: CountDownTimer? = null
    private var finished = true
    private var finishedTime: Long = 0

    val isRunning: Boolean get() = !isFinished

    val isFinished: Boolean get() = finished && System.currentTimeMillis() - finishedTime > 300

    init {
        run()
    }

    override fun run() {
        super.run()

        timer = object : CountDownTimer(milliseconds, interval) {
            override fun onTick(l: Long) {
                try {
                    onTick(l, finished)
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
            }
            override fun onFinish() {
                try {
                    finished = true
                    finishedTime = System.currentTimeMillis()
                    onTick(milliseconds, finished)
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
            }
        }

    }

    fun stopTimer() {
        if (!finished)
            timer?.cancel()
        finished = true
    }

    fun startTimer() {
        if (finished) {
            finished = false
            timer?.start()
        }
    }

}
