package ir.am3n.needtool

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat


inline fun Context.notification(channelId: String, func: NotificationCompat.Builder.() -> Unit): Notification {
    val builder = NotificationCompat.Builder(this, channelId)
    builder.func()
    return builder.build()
}
