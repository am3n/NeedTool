package ir.am3n.needtool

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


inline fun Context.notification(channelId: String, func: NotificationCompat.Builder.() -> Unit): Notification {
    val builder = NotificationCompat.Builder(this, channelId)
    builder.func()
    return builder.build()
}

fun NotificationManagerCompat.areNotificationsFullyEnabled(): Boolean {
    if (!areNotificationsEnabled())
        return false
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        for (notificationChannel in notificationChannels) {
            if (!notificationChannel.isFullyEnabled(this))
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