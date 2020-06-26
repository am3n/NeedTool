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


    fun hasLocation(context: Context?): Boolean {
        return context?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestLocation(context: Activity?) {
        context?.let {
            ActivityCompat.requestPermissions(
                it,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                RQST_LOCATION_PERMISSION
            )
        }
    }




    // -------------- RECEIVE_SMS ------------------------------------------------------------------
    fun hasSms(context: Context?): Boolean {
        return context?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.RECEIVE_SMS
            )
        } == PackageManager.PERMISSION_GRANTED
    }

    fun requestSms(context: Activity?) {
        context?.let {
            ActivityCompat.requestPermissions(
                it,
                arrayOf(Manifest.permission.RECEIVE_SMS),
                RQST_SMS_PERMISSION
            )
        }
    }

    fun requestSms(context: Activity, request_code: Int) {
        ActivityCompat.requestPermissions(
            context,
            arrayOf(Manifest.permission.RECEIVE_SMS),
            request_code
        )
    }




    fun hasCallPhone(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestCallPhone(context: Activity) {
        ActivityCompat.requestPermissions(
            context,
            arrayOf(Manifest.permission.CALL_PHONE),
            RQST_CALLPHONE_PERMISSION
        )
    }

}
