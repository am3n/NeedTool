package ir.am3n.needtool

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.*
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.os.AsyncTask
import android.os.Build
import androidx.annotation.IdRes
import androidx.core.graphics.drawable.IconCompat

object AppShortcut {

    fun create(activity: Activity, scClass: Class<*>, appName: String?, @IdRes appIcon: Int) {
        if (Build.VERSION.SDK_INT >= 26)
            postApi26CreateShortcut(activity, scClass, appName ?:"app", appIcon)
        else
            preApi26CreateShortcut(activity, scClass, appName ?:"app", appIcon)
    }

    private fun preApi26CreateShortcut(
        activity: Activity,
        scClass: Class<*>,
        appName: String,
        appIcon: Int
    ) {
        // Get preference value to check the app run first time.
        val appPreferences = activity.getSharedPreferences("def", Context.MODE_PRIVATE)
        val isFirstRun = appPreferences.getBoolean("isFirstRun", false)
        if (!isFirstRun) { // Create an explict intent it will be used to call Our application by click on the short cut
            val shortcutIntent = Intent(activity, scClass)
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val addIntent = Intent()
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent)
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName)
            val icon = Intent.ShortcutIconResource.fromContext(activity, appIcon)
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon)
            addIntent.action = "com.android.launcher.action.INSTALL_SHORTCUT"
            addIntent.putExtra("duplicate", false)
            activity.sendBroadcast(addIntent)
            // Set preference  as true
            val editor: SharedPreferences.Editor = appPreferences.edit()
            editor.putBoolean("isFirstRun", true)
            editor.apply()
        }
    }

    @SuppressLint("NewApi")
    private fun postApi26CreateShortcut(
        activity: Activity?,
        scClass: Class<*>,
        appName: String,
        appIcon: Int
    ) {
        val sm = activity?.getSystemService(ShortcutManager::class.java)
        if (sm != null && sm.isRequestPinShortcutSupported) {
            var shortcutExists = false
            // We create the shortcut multiple times if given the
            // opportunity.  If the shortcut exists, put up
            // a toast message and exit.
            val shortcuts = sm.pinnedShortcuts
            for (i in 0 until shortcuts.size) {
                shortcutExists = shortcuts[i].id == appName
                if (shortcutExists)
                    break
            }
            if (!shortcutExists) {
                // this intent is used to wake up the broadcast receiver.
                // I couldn't get createShortcutResultIntent to work but
                // just a simple intent as used for a normal broadcast
                // intent works fine.
                val broadcastIntent = Intent(appName)
                broadcastIntent.putExtra("msg", "approve");
                // wait up to N seconds for user input, then continue
                // on assuming user's choice was deny.
                val waitFor = WaitFor(activity, 10).execute()
                // create an anonymous broadcaster.  Unregister when done.
                activity.registerReceiver(object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        activity.unregisterReceiver(this)
                        waitFor.cancel(true)
                    }
                }, IntentFilter(appName))
                // this is the intent that actually creates the shortcut.
                val shortcutIntent = Intent(activity, scClass)
                shortcutIntent.action = appName
                val shortcutInfo = ShortcutInfo
                    .Builder(activity, appName)
                    .setShortLabel(appName)
                    .setIcon(IconCompat.createWithResource(activity, appIcon).toIcon(activity))
                    .setIntent(shortcutIntent)
                    .build()
                val successCallback = PendingIntent.getBroadcast(activity, 99, broadcastIntent, 0)
                // Shortcut gets created here.
                sm.requestPinShortcut(shortcutInfo, successCallback.intentSender)
            }
        }
    }

    class WaitFor(private val activity: Activity?, n: Int) : AsyncTask<Void, Void, Void?>() {
        private var waitPeriod: Long = n * 1000L
        override fun doInBackground(vararg params: Void?): Void? {
            try {
                Thread.sleep(waitPeriod)
                val bi = Intent(params[0].toString())
                bi.putExtra("msg", "deny")
                activity?.sendBroadcast(bi)
            } catch (ignore: Throwable) {}
            return null
        }
    }

}