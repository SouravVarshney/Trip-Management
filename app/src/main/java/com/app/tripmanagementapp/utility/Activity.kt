package com.app.tripmanagementapp.utility

import android.app.Activity
import android.content.Context
import android.location.LocationManager
import android.widget.Toast

fun Activity?.toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.isLocationEnabled() =
    (getSystemService(Context.LOCATION_SERVICE) as LocationManager).isProviderEnabled(
        LocationManager.GPS_PROVIDER
    )