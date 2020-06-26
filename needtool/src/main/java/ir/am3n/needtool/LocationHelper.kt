package ir.am3n.needtool

import android.content.Context
import android.location.LocationManager


val Context.isLocationEnabled: Boolean get() = isLocationGpsEnabled || isLocationNetworkEnabled
val Context.isLocationGpsEnabled: Boolean get() = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true
val Context.isLocationNetworkEnabled: Boolean get() = locationManager?.isProviderEnabled(
    LocationManager.NETWORK_PROVIDER) == true


