package ir.am3n.needtool

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.res.Configuration
import android.graphics.Point
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.telephony.TelephonyManager
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.jaredrummler.android.device.DeviceName
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.pow
import kotlin.math.sqrt


fun Context.sendLocalBroadcast(intent: Intent) {
    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
}

fun Context.sendLocalBroadcastSync(intent: Intent) {
    LocalBroadcastManager.getInstance(this).sendBroadcastSync(intent)
}


val Context.locationManager: LocationManager?
    get() =  getSystemService(Context.LOCATION_SERVICE) as LocationManager?

val Context.notificationManager: NotificationManager?
    get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

val Context.connectivityManager: ConnectivityManager?
    @SuppressLint("ServiceCast") get() = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

val Context.powerManager: PowerManager?
    get() = getSystemService(Context.POWER_SERVICE) as PowerManager?

val Context.activityManager: ActivityManager?
    get() = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?

val Context.wifiManager: WifiManager?
    get() = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?

val Context.teleManager: TelephonyManager?
    get() = applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?

val Context.ssid: String? get() {
    val info = wifiManager?.connectionInfo
    return info?.ssid
}



fun Context.deviceId(): String {
    return Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
}


val serialDevice: String
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { 
        try {
            Build.getSerial()
        } catch (t: Throwable) {
            Build.SERIAL
        }
    } else {
        Build.SERIAL
    }



fun Context.device(): HashMap<String, String> {
    val map = HashMap<String, String>()

    map["appVersion"] = "? (?)"
    try {
        val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        val appVersionName = pInfo.versionName
        val appVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) pInfo.longVersionCode else pInfo.versionCode.toLong()
        map["appVersion"] = "$appVersionCode ($appVersionName)"
    } catch (t: Throwable) {}


    map["osVersion"] = "${Build.VERSION.SDK_INT} (${Build.VERSION.RELEASE})"


    map["psVersion"] = ""
    try {
        var psVersion: String
        psVersion = packageManager.getPackageInfo("com.google.android.gms", 0).versionName
        if (psVersion.contains(" ")) psVersion = psVersion.split(" ".toRegex()).toTypedArray()[0]
        map["psVersion"] = psVersion
    } catch (t: Throwable) {}


    map["deviceImei"] = ""
    if (this.hasPermission(Manifest.permission.READ_PHONE_STATE)) {
        try {
            val telephonyManager = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
            @SuppressLint("MissingPermission", "HardwareIds")
            map["deviceImei"] = telephonyManager?.deviceId.toString()
        } catch (t: Throwable) {}
    }


    map["deviceModel"] = Build.BRAND
    try {
        DeviceName.init(this)
        map["deviceModel"] += " : " + DeviceName.getDeviceName()
    } catch (t: Throwable) {}


    map["cpu"] = ""
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        map["cpu"] = Build.SUPPORTED_ABIS.joinToString(", ")
    } else {
        map["cpu"] = Build.CPU_ABI + ", " + Build.CPU_ABI2
    }


    map["deviceScreenClass"] = "Unknown"
    when {
        this.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == Configuration.SCREENLAYOUT_SIZE_LARGE ->
            map["deviceScreenClass"] = "Large"
        this.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == Configuration.SCREENLAYOUT_SIZE_NORMAL ->
            map["deviceScreenClass"] = "Normal"
        this.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == Configuration.SCREENLAYOUT_SIZE_SMALL ->
            map["deviceScreenClass"] = "Small"
    }


    val metrics = this.resources.displayMetrics
    val density = metrics.density
    map["deviceDpiClass"] = "Unknown"
    when {
        density <= 0.75f -> map["deviceDpiClass"] = "ldpi"
        density <= 1.0f -> map["deviceDpiClass"] = "mdpi"
        density <= 1.5f -> map["deviceDpiClass"] = "hdpi"
        density <= 2.0f -> map["deviceDpiClass"] = "xhdpi"
        density <= 3.0f -> map["deviceDpiClass"] = "xxhdpi"
        density <= 4.0f -> map["deviceDpiClass"] = "xxxhdpi"
    }


    val orientation = this.resources.configuration.orientation
    val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
    val display = wm?.defaultDisplay
    val screenSize = Point()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        display?.getRealSize(screenSize)
    } else {
        display?.getSize(screenSize)
    }
    val screensizeX = screenSize.x
    val screensizeY = screenSize.y
    val width = if (orientation == Configuration.ORIENTATION_PORTRAIT) screensizeX else screensizeY
    val height = if (orientation == Configuration.ORIENTATION_PORTRAIT) screensizeY else screensizeX
    val wi = width.toDouble() / metrics.xdpi.toDouble()
    val hi = height.toDouble() / metrics.ydpi.toDouble()
    val x = wi.pow(2.0)
    val y = hi.pow(2.0)
    val screenInches = sqrt(x + y)
    map["deviceScreenSize"] = String.format(Locale.US, "%.2f", screenInches)


    map["deviceScreenDimensionsDpis"] = (width / density).toInt().toString() + " x " + (height / density).toInt()


    map["deviceScreenDimensionsPixels"] = "$width x $height"


    return map
}


fun Context.isDebug(): Boolean {
    try {
        return 0 != (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE)
    } catch (t: Throwable) {}
    return false
}


fun Context.isDarkMode(): Boolean {
    return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}


fun Context.isServiceRunning(serviceClass: Class<*>): Boolean {
    val className = serviceClass.name
    val manager = activityManager
    return manager?.getRunningServices(Integer.MAX_VALUE)?.any { className == it.service.className } ?: false
}


fun Context.copy(text: CharSequence?, label: String = "clipboard", toast: Boolean = true) {
    copy(text?.toString(), label, toast)
}
fun Context.copy(text: String?, label: String = "clipboard", toast: Boolean = true) {
    val clipboard: ClipboardManager? = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
    val clip: ClipData? = ClipData.newPlainText(label, text)
    clip?.let { clipboard?.setPrimaryClip(it) }
    if (toast)
        toast("کپی شد")
}
