package ir.am3n.needtool

import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings


fun Context.wifiDisconnect(onNeedShowChangeWifiConnectivityPermission: Runnable? = null): Boolean? {
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
    onNeedShowChangeWifiConnectivityPermission: Runnable? = null
) {
    onIO {
        wifiDisconnect(onNeedShowChangeWifiConnectivityPermission).let {
            onResult?.invoke(it)
        }
    }
}


//--------------------------------------------------------------------------------------------------


fun Context.wifiDisableIf(
    onNeedShowChangeWifiConnectivityPermission: Runnable? = null
): Boolean? {
    try {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return if (wifiManager?.isWifiEnabled == true) {
                onUI(onNeedShowChangeWifiConnectivityPermission, 500)
                val result = wifiManager?.setWifiEnabled(false)
                offUI(onNeedShowChangeWifiConnectivityPermission)
                result
            } else
                true
        } else {
            return if (wifiManager?.isWifiEnabled == true) {
                onUI(onNeedShowChangeWifiConnectivityPermission, 500)
                startActivity(Intent(Settings.Panel.ACTION_WIFI))
                offUI(onNeedShowChangeWifiConnectivityPermission)
                true
            } else
                true
        }
    } catch (t: Throwable) {
        offUI(onNeedShowChangeWifiConnectivityPermission)
    }
    return null
}

fun Activity.wifiDisableIf(
    onNeedShowChangeWifiConnectivityPermission: Runnable? = null,
    activityResultRequestCodeOnForAndroidQ: Int
): Boolean? {
    try {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return if (wifiManager?.isWifiEnabled == true) {
                onUI(onNeedShowChangeWifiConnectivityPermission, 500)
                val result = wifiManager?.setWifiEnabled(false)
                offUI(onNeedShowChangeWifiConnectivityPermission)
                result
            } else
                true
        } else {
            return if (wifiManager?.isWifiEnabled == true) {
                onUI(onNeedShowChangeWifiConnectivityPermission, 500)
                startActivityForResult(Intent(Settings.Panel.ACTION_WIFI), activityResultRequestCodeOnForAndroidQ)
                offUI(onNeedShowChangeWifiConnectivityPermission)
                true
            } else
                true
        }
    } catch (t: Throwable) {
        offUI(onNeedShowChangeWifiConnectivityPermission)
    }
    return null
}

fun Context.wifiDisableIfAsync(
    onResult: ((Boolean?) -> Unit)? = null,
    onNeedShowChangeWifiConnectivityPermission: Runnable? = null
) {
    onIO {
        wifiDisableIf(onNeedShowChangeWifiConnectivityPermission).let {
            onResult?.invoke(it)
        }
    }
}

fun Activity.wifiDisableIfAsync(
    onResult: ((Boolean?) -> Unit)? = null,
    onNeedShowChangeWifiConnectivityPermission: Runnable? = null,
    activityResultRequestCodeOnForAndroidQ: Int
) {
    onIO {
        wifiDisableIf(onNeedShowChangeWifiConnectivityPermission, activityResultRequestCodeOnForAndroidQ).let {
            onResult?.invoke(it)
        }
    }
}


//--------------------------------------------------------------------------------------------------


fun Context.wifiEnable(
    onNeedShowChangeWifiConnectivityPermission: Runnable? = null
): Boolean? {
    try {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return if (wifiManager?.isWifiEnabled == false) {
                onUI(onNeedShowChangeWifiConnectivityPermission, 500)
                val result = wifiManager?.setWifiEnabled(true)
                offUI(onNeedShowChangeWifiConnectivityPermission)
                result
            } else
                true
        } else {
            return if (wifiManager?.isWifiEnabled == false) {
                onUI(onNeedShowChangeWifiConnectivityPermission, 500)
                startActivity(Intent(Settings.Panel.ACTION_WIFI))
                offUI(onNeedShowChangeWifiConnectivityPermission)
                true
            } else
                true
        }
    } catch (t: Throwable) {
        t.printStackTrace()
    }
    return null
}

fun Activity.wifiEnable(
    onNeedShowChangeWifiConnectivityPermission: Runnable? = null,
    activityResultRequestCodeOnForAndroidQ: Int
): Boolean? {
    try {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return if (wifiManager?.isWifiEnabled == false) {
                onUI(onNeedShowChangeWifiConnectivityPermission, 500)
                val result = wifiManager?.setWifiEnabled(true)
                offUI(onNeedShowChangeWifiConnectivityPermission)
                result
            } else
                true
        } else {
            return if (wifiManager?.isWifiEnabled == false) {
                onUI(onNeedShowChangeWifiConnectivityPermission, 500)
                startActivityForResult(Intent(Settings.Panel.ACTION_WIFI), activityResultRequestCodeOnForAndroidQ)
                offUI(onNeedShowChangeWifiConnectivityPermission)
                true
            } else
                true
        }
    } catch (t: Throwable) {
        t.printStackTrace()
    }
    return null
}

fun Context.wifiEnableAsync(
    onResult: ((Boolean?) -> Unit)? = null,
    onNeedShowChangeWifiConnectivityPermission: Runnable? = null
) {
    onIO {
        wifiEnable(onNeedShowChangeWifiConnectivityPermission).let {
            onResult?.invoke(it)
        }
    }
}

fun Activity.wifiEnableAsync(
    onResult: ((Boolean?) -> Unit)? = null,
    onNeedShowChangeWifiConnectivityPermission: Runnable? = null,
    activityResultRequestCodeOnForAndroidQ: Int
) {
    onIO {
        wifiEnable(onNeedShowChangeWifiConnectivityPermission, activityResultRequestCodeOnForAndroidQ).let {
            onResult?.invoke(it)
        }
    }
}


//--------------------------------------------------------------------------------------------------


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


private val lifecycleCallback = object : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }
    override fun onActivityStarted(activity: Activity) {

    }
    override fun onActivityResumed(activity: Activity) {
        activity.unregisterActivityLifecycleCallbacks(this)
        if (pending && activity.isWifiEnabled) {
            try {
                pending = false
                tryed = 0
                val intentFilter = IntentFilter()
                intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
                activity.registerReceiver(wifiScanReceiver, intentFilter)
                activity.wifiManager?.startScan()
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        } else {
            scanCallback?.invoke(null)
            pending = false
            tryed = 0
        }
    }
    override fun onActivityPaused(activity: Activity) {

    }
    override fun onActivityStopped(activity: Activity) {

    }
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }
    override fun onActivityDestroyed(activity: Activity) {

    }
}

@Synchronized
fun Activity.scanWifi(callback: ((List<ScanResult>?) -> Unit)? = {}, onNeedShowChangeWifiConnectivityPermission: Runnable? = null) {
    scanCallback = callback
    if (pending) return
    pending = true
    try { unregisterReceiver(wifiScanReceiver) } catch (t: Throwable) {}
    onIO {
        try {
            if (!isWifiEnabled) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    try { unregisterActivityLifecycleCallbacks(lifecycleCallback) } catch (t: Throwable) {}
                    registerActivityLifecycleCallbacks(lifecycleCallback)
                    startActivity(Intent(Settings.Panel.ACTION_WIFI))

                } else {
                    wifiEnable(onNeedShowChangeWifiConnectivityPermission)
                    if (tryed > 6) {
                        pending = false
                        tryed = 0
                        scanCallback?.invoke(null)
                    } else {
                        onIO({
                            tryed++
                            pending = false
                            scanWifi(callback, onNeedShowChangeWifiConnectivityPermission)
                        }, 500)
                    }
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
            scanCallback?.invoke(null)
        }
    }
}