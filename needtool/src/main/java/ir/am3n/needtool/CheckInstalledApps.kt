package ir.am3n.needtool

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Process
import java.util.*

object CheckInstalledApps {

    fun getInstalledFrom(context: Context, packageNames: Array<String>, listener: Listener) {
        Thread {
            try {
                Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST)
                val packageInfos = getInstalledApps(context)
                val copy: MutableList<PackageInfo> = ArrayList(packageInfos)
                for (packageInfo in packageInfos) {
                    var found = false
                    for (packageName in packageNames)
                        if (packageInfo.packageName == packageName)
                            found = true
                    if (!found)
                        copy.remove(packageInfo)
                }
                listener.onResponse(copy)
            } catch (t: Throwable) {
                t.printStackTrace()
                listener.onResponse(ArrayList())
            }
        }.start()
    }

    private fun getInstalledApps(context: Context): List<PackageInfo> {
        try {
            val packageInfos =
                context.packageManager.getInstalledPackages(PackageManager.GET_GIDS)
            // List<ApplicationInfo> packageInfos = context.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
            var i = 0
            while (i < packageInfos.size) {
                if (isSystemPackage(packageInfos[i]))
                    packageInfos.removeAt(i--)
                i++
            }
            return packageInfos
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return ArrayList()
    }

    private fun isSystemPackage(packageInfo: PackageInfo): Boolean {
        return packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }

    interface Listener {
        fun onResponse(packageInfoList: List<PackageInfo>?)
    }

}