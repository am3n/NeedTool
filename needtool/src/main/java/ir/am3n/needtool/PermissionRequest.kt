package ir.am3n.needtool

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionRequest {


    const val RQST_LOCATION_PERMISSION = 1000
    const val RQST_SMS_PERMISSION = 9900
    const val RQST_CALLPHONE_PERMISSION = 1006
    const val RQST_CAMERA_PERMISSION = 1007




    fun hasLocation(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun requestLocation(activity: Activity, requestCode: Int? = null) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            requestCode ?: RQST_LOCATION_PERMISSION
        )
    }




    // -------------- RECEIVE_SMS ------------------------------------------------------------------
    fun hasSms(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECEIVE_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestSms(activity: Activity, requestCode: Int? = null) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.RECEIVE_SMS),
            requestCode ?: RQST_SMS_PERMISSION
        )
    }




    fun hasCallPhone(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestCallPhone(activity: Activity, requestCode: Int? = null) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.CALL_PHONE),
            requestCode ?: RQST_CALLPHONE_PERMISSION
        )
    }




    fun hasCamera(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestCamera(activity: Activity, requestCode: Int? = null) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.CAMERA),
            requestCode ?: RQST_CAMERA_PERMISSION
        )
    }



}
