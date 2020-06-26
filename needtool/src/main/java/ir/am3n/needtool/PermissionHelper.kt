package ir.am3n.needtool

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat


fun Context.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

val Context.hasLocationPerm: Boolean get() = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
