package ir.am3n.needtool

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import java.lang.reflect.Method
import java.net.NetworkInterface
import java.util.*
import kotlin.math.pow


val Context.isNetworkConnected: Boolean @SuppressLint("MissingPermission") get() = connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting == true


val Context.isHotspotOn: Boolean get() {
    try {
        val method: Method? = this.wifiManager?.javaClass?.getDeclaredMethod("isWifiApEnabled")
        method?.isAccessible = true
        return (method?.invoke(this.wifiManager) as Boolean?) ?:false
    } catch (t: Throwable) {}
    return false
}

fun Context.isVpnActive(): Boolean {
    val networkList: MutableList<String> = ArrayList()
    try {
        for (networkInterface in Collections.list(NetworkInterface.getNetworkInterfaces())) {
            if (networkInterface.isUp) networkList.add(networkInterface.name)
        }
    } catch (e: Exception) {}
    return networkList.contains("tun0") || networkList.contains("ppp0") ||
            (connectivityManager?.getNetworkInfo(ConnectivityManager.TYPE_VPN)?.isConnectedOrConnecting ?: false)
}

fun pingGoogle(callback: (Boolean) -> Unit) {
    ping(callback, "google.com", 0)
}

fun pingGoogle(callback: (Boolean) -> Unit, delay: Long = 0, timeout: Long = 1000) {
    ping(callback, "google.com", delay, timeout)
}

fun ping(callback: (Boolean) -> Unit, address: String, delay: Long = 0, timeout: Long = 1000) {
    var myCallback: ((Boolean) -> Unit)? = callback
    Thread {
        if (delay > 0)
            Thread.sleep(delay)
        var wait4p = true
        onUI({
            if (wait4p) {
                wait4p = false
                myCallback?.invoke(false)
                myCallback = null
            }
        }, timeout)
        try {
            val command = "ping -c 1 $address"
            val ret = Runtime.getRuntime().exec(command).waitFor() == 0
            wait4p = false
            onUI {
                myCallback?.invoke(ret)
                myCallback = null
            }
        } catch (t: Throwable) {
            onUI {
                myCallback?.invoke(false)
                myCallback = null
            }
            t.printStackTrace()
        }
    }.start()
}


fun ipToInt(addr: String): Int {
    val addrArray = addr.split("\\.")
    var num = 0
    for (i in addrArray.indices) {
        val power = 3-i
        num += (Integer.parseInt(addrArray[i]) %256 * 256.0.pow(power.toDouble()).toInt())
    }
    return num
}

