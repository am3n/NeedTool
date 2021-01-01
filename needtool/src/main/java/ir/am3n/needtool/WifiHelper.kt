package ir.am3n.needtool

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build


fun Context.wifiDisconnect(onNeedShowChangeWifiConnectivityPermission: (() -> Unit)? = null): Boolean? {
    try {
        onUI(onNeedShowChangeWifiConnectivityPermission, 500)
        val result = wifiManager?.disconnect()
        offUI(onNeedShowChangeWifiConnectivityPermission)
        return result
    } catch (t: Throwable) {
        offUI(onNeedShowChangeWifiConnectivityPermission)
    }
    return null
}

fun Context.wifiDisconnectAsync(
    onResult: ((Boolean?) -> Unit)? = null,
    onNeedShowChangeWifiConnectivityPermission: (() -> Unit)? = null
) {
    onIO {
        wifiDisconnect(onNeedShowChangeWifiConnectivityPermission).let {
            onResult?.invoke(it)
        }
    }
}


fun Context.wifiDisableIf(onNeedShowChangeWifiConnectivityPermission: (() -> Unit)? = null): Boolean? {
    try {
        return if (wifiManager?.isWifiEnabled == true) {
            onUI(onNeedShowChangeWifiConnectivityPermission, 500)
            val result = wifiManager?.setWifiEnabled(false)
            offUI(onNeedShowChangeWifiConnectivityPermission)
            result
        } else
            true
    } catch (t: Throwable) {
        offUI(onNeedShowChangeWifiConnectivityPermission)
    }
    return null
}

fun Context.wifiDisableIfAsync(
    onResult: ((Boolean?) -> Unit)? = null,
    onNeedShowChangeWifiConnectivityPermission: (() -> Unit)? = null
) {
    onIO {
        wifiDisableIf(onNeedShowChangeWifiConnectivityPermission).let {
            onResult?.invoke(it)
        }
    }
}


private val wifiScanReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val success = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
        } else {
            intent.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION
        }
        onIO {
            scanCallback?.invoke(if (success) context.wifiManager?.scanResults else null)
        }
        try { context.unregisterReceiver(this) } catch (t: Throwable) {}
    }
}

private var scanCallback: ((List<ScanResult>?) -> Unit)? = null
private var pending = false
private var tryed = 0

@Synchronized
fun Context.scan(callback: (List<ScanResult>?) -> Unit = {}) {
    scanCallback = callback
    if (pending)
        return
    pending = true
    try {
        unregisterReceiver(wifiScanReceiver)
    } catch (t: Throwable) {
    }
    onIO {
        try {
            if (!isWifiEnabled) {
                wifiManager?.isWifiEnabled = true
                if (tryed > 6) {
                    pending = false
                    tryed = 0
                } else {
                    onIO({ tryed++; pending = false; scan(callback) }, 500)
                }
            } else {
                pending = false
                tryed = 0
                val intentFilter = IntentFilter()
                intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
                registerReceiver(wifiScanReceiver, intentFilter)
                wifiManager?.startScan()
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            pending = false
            tryed = 0
        }
    }
}