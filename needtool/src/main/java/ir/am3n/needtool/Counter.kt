package ir.am3n.needtool

class Counter(milliseconds: Int, interval: Int, var tickListener: TimerTickListener) : Thread() {

    private lateinit var timer: CountDownTimer
    internal var finished = true
    internal var finishedTime: Long = 0
    internal var milliseconds = 5000
    internal var interval = 1000

    val isRunning: Boolean get() = !isFinished

    val isFinished: Boolean get() = finished && System.currentTimeMillis() - finishedTime > 300

    interface TimerTickListener {
        fun onTick(l: Long)
        fun onFinish()
    }


    init {
        this.milliseconds = milliseconds
        this.interval = interval
        run()
    }

    override fun run() {
        super.run()

        timer = object : CountDownTimer(milliseconds.toLong(), interval.toLong()) {
            override fun onTick(l: Long) {
                tickListener.onTick(l)
            }
            override fun onFinish() {
                tickListener.onFinish()
                finished = true
                finishedTime = System.currentTimeMillis()
            }
        }

    }

    fun stopTimer() {
        if (!finished)
            timer.cancel()
        finished = true
    }

    fun startTimer() {
        if (finished) {
            finished = false
            timer.start()
        }
    }

}
