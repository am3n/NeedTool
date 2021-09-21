package ir.am3n.needtool

import java.lang.Thread.sleep
import java.net.DatagramPacket
import java.net.DatagramSocket

class DatagramServer(
    private val port: Int = 1000,
    private val maxLen: Int = 256,
    private val receiveTimeout: Long = 1500,
    private val intervalTimeout: Long = 500,
    private val log: Boolean = false,
    private val onPacket: (DatagramPacket) -> Unit = {}
) {

    private var running = false
    private var socket: DatagramSocket? = null
    private var thread: Thread? = null
    private val runnable: Runnable by lazy {
        Runnable {
            try {
                val lmessage = ByteArray(maxLen)
                val packet = DatagramPacket(lmessage, lmessage.size)
                socket = DatagramSocket(port)
                socket?.broadcast = true
                socket?.soTimeout = receiveTimeout.toInt()
                try {
                    while (running) {
                        try {
                            sleep(intervalTimeout)
                            socket?.receive(packet)
                            if (running && packet.length > 0)
                                onUI {
                                    onPacket(
                                        DatagramPacket(
                                            packet.data,
                                            packet.offset,
                                            packet.length,
                                            packet.address,
                                            packet.port
                                        )
                                    )
                                }
                            else if (!running)
                                break
                        } catch (t: Throwable) {
                            if (log) t.printStackTrace()
                        }
                    }
                } catch (t: Throwable) {
                    if (log) t.printStackTrace()
                }
                try {
                    socket?.disconnect()
                } catch (t: Throwable) {
                    if (log) t.printStackTrace()
                }
                try {
                    socket?.close()
                } catch (t: Throwable) {
                    if (log) t.printStackTrace()
                }
            } catch (t: Throwable) {
                if (log) t.printStackTrace()
            }
        }
    }

    init {
        resume()
    }

    fun resume() {
        try {
            if (running) return
            thread = Thread(runnable)
            thread!!.start()
            running = true
        } catch (t: Throwable) {
            if (log) t.printStackTrace()
        }
    }

    fun pause() {
        try {
            running = false
            socket?.close()
            socket = null
        } catch (t: Throwable) {
            if (log) t.printStackTrace()
        }
    }

}