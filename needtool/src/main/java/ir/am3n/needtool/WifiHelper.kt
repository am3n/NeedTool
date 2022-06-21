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
import android.util.Log


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
        Log.d("NeedTool-WifiHelper", "WifiScanReceiver onReceive()")
        try {
            context.unregisterReceiver(this)
        } catch (t: Throwable) {
        }
        val success = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
        } else {
            intent.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION
        }
        Log.d("NeedTool-WifiHelper", "WifiScanReceiver onReceive() success=$success")
        onIO {
            try {
                scanCallback!!.invoke(success, if (success) context.wifiManager!!.scanResults else null)
                Log.d("NeedTool-WifiHelper", "WifiScanReceiver onReceive() success=$success > invoke callback")
            } catch (t: Throwable) {
                Log.e("NeedTool-WifiHelper", "WifiScanReceiver onReceive()", t)
            }
        }
    }
}

private var scanCallback: ((success: Boolean?, result: List<ScanResult>?) -> Unit)? = null
private var pending = false
private var tryed = 0


private val lifecycleCallback = object : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Log.d("NeedTool-WifiHelper", "ActivityLifecycle onActivityCreated()")
    }

    override fun onActivityStarted(activity: Activity) {
        Log.d("NeedTool-WifiHelper", "ActivityLifecycle onActivityStarted()")
    }

    override fun onActivityResumed(activity: Activity) {
        Log.d("NeedTool-WifiHelper", "ActivityLifecycle onActivityResumed()")
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                activity.unregisterActivityLifecycleCallbacks(this)
            }
        } catch (t: Throwable) {
        }
        Log.d(
            "NeedTool-WifiHelper",
            "ActivityLifecycle onActivityResumed() > pending=$pending   isWifiEnabled=${activity.isWifiEnabled}"
        )
        if (pending && activity.isWifiEnabled) {
            try {
                tryed = 0
                pending = false
                val intentFilter = IntentFilter()
                intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
                activity.registerReceiver(wifiScanReceiver, intentFilter)
                activity.wifiManager!!.startScan()
                Log.d(
                    "NeedTool-WifiHelper",
                    "ActivityLifecycle onActivityResumed()  pending=$pending  isWifiEnabled=${activity.isWifiEnabled} > wifiManager.startScan()"
                )
            } catch (t: Throwable) {
                try {
                    Log.e(
                        "NeedTool-WifiHelper",
                        "ActivityLifecycle onActivityResumed()  pending=$pending  isWifiEnabled=${activity.isWifiEnabled}",
                        t
                    )
                    scanCallback!!.invoke(null, null)
                } catch (t: Throwable) {
                    Log.e(
                        "NeedTool-WifiHelper",
                        "ActivityLifecycle onActivityResumed()  pending=$pending   isWifiEnabled=${activity.isWifiEnabled}",
                        t
                    )
                }
            }
        } else {
            try {
                tryed = 0
                pending = false
                scanCallback!!.invoke(null, null)
                Log.d("NeedTool-WifiHelper", "ActivityLifecycle onActivityResumed()  callback with null")
            } catch (t: Throwable) {
                Log.e("NeedTool-WifiHelper", "ActivityLifecycle onActivityResumed()", t)
            }
        }
    }

    override fun onActivityPaused(activity: Activity) {
        Log.d("NeedTool-WifiHelper", "ActivityLifecycle onActivityPaused()")
    }

    override fun onActivityStopped(activity: Activity) {
        Log.d("NeedTool-WifiHelper", "ActivityLifecycle onActivityStoped()")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        Log.d("NeedTool-WifiHelper", "ActivityLifecycle onActivitySaveInstanceState()")
    }

    override fun onActivityDestroyed(activity: Activity) {
        try {
            tryed = 0
            pending = false
            scanCallback!!.invoke(null, null)
            Log.d("NeedTool-WifiHelper", "ActivityLifecycle onActivityDestroyed()")
        } catch (t: Throwable) {
            Log.e("NeedTool-WifiHelper", "ActivityLifecycle onActivityDestroyed()", t)
        }
    }
}

@Synchronized
fun Activity.scanWifi(
    callback: ((success: Boolean?, result: List<ScanResult>?) -> Unit)? = { _, _ -> },
    onNeedShowChangeWifiConnectivityPermission: Runnable? = null
) {
    Log.d("NeedTool-WifiHelper", "scanWifi()  pending=$pending")
    /*if (callback != null) {
        return callback(emptyList())
    }*/
    scanCallback = callback
    if (pending) return
    pending = true
    try {
        unregisterReceiver(wifiScanReceiver)
    } catch (t: Throwable) {
    }
    onIO {
        try {
            Log.d("NeedTool-WifiHelper", "scanWifi()  pending=$pending  isWifiEnabled=$isWifiEnabled")
            if (!isWifiEnabled) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    try {
                        unregisterActivityLifecycleCallbacks(lifecycleCallback)
                    } catch (t: Throwable) {
                    }
                    registerActivityLifecycleCallbacks(lifecycleCallback)
                    startActivity(Intent(Settings.Panel.ACTION_WIFI))
                    Log.d("NeedTool-WifiHelper", "scanWifi()  pending=$pending  > call startActivity(..)")

                } else {
                    Log.d("NeedTool-WifiHelper", "scanWifi()  pending=$pending  > call wifiEnable()  tryed=$tryed")
                    wifiEnable(onNeedShowChangeWifiConnectivityPermission)
                    if (tryed > 6) {
                        pending = false
                        tryed = 0
                        scanCallback!!.invoke(null, null)
                        Log.d("NeedTool-WifiHelper", "scanWifi()  pending=$pending  tryed=$tryed  invoke scanCallback")
                    } else {
                        onIO({
                            tryed++
                            pending = false
                            scanWifi(callback, onNeedShowChangeWifiConnectivityPermission)
                            Log.d("NeedTool-WifiHelper", "scanWifi()  pending=$pending  tryed=$tryed  call scanWifi()")
                        }, 500)
                    }
                }

            } else {
                pending = false
                tryed = 0
                val intentFilter = IntentFilter()
                intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
                registerReceiver(wifiScanReceiver, intentFilter)
                wifiManager!!.startScan()
                Log.d("NeedTool-WifiHelper", "scanWifi()  pending=$pending  >  call wifiManager.startScan()")
            }
        } catch (t: Throwable) {
            Log.e("NeedTool-WifiHelper", "scanWifi()", t)
            try {
                pending = false
                tryed = 0
                scanCallback!!.invoke(null, null)
            } catch (t: Throwable) {
                Log.e("NeedTool-WifiHelper", "scanWifi() > catch(t)", t)
            }
        }
    }
}