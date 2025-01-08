package ir.am3n.needtool

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


inline fun Context.notification(channelId: String, func: NotificationCompat.Builder.() -> Unit): Notification {
    val builder = NotificationCompat.Builder(this, channelId)
    builder.func()
    return builder.build()
}


fun Context.areNotificationsFullyEnabled(): Boolean {
    val nmc = NotificationManagerCompat.from(this)
    if (!nmc.areNotificationsEnabled())
        return false
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        for (notificationChannel in nmc.notificationChannels) {
            if (!notificationChannel.isFullyEnabled(nmc))
                return false
        }
    }
    return true
}

@RequiresApi(Build.VERSION_CODES.O)
fun NotificationChannel.isFullyEnabled(notificationManager: NotificationManagerCompat): Boolean {
    if (importance == NotificationManager.IMPORTANCE_NONE)
        return false
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        if (notificationManager.getNotificationChannelGroup(group)?.isBlocked == true)
            return false
    }
    return true
}


@RequiresApi(Build.VERSION_CODES.M)
fun NotificationManager.isNotificationVisible(id: Int): Boolean {
    var isVisible = false
    val notifications = activeNotifications ?: emptyArray()
    for (notification in notifications) {
        if (notification.id == id) {
            isVisible = true
            break
        }
    }
    return isVisible
}
