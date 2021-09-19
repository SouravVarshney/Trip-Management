package com.app.tripmanagementapp.utility

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.app.tripmanagementapp.TripManagementApplication
import java.text.SimpleDateFormat

class AppUtils {

    companion object {

        const val LOCATION_WORK_TAG = "LOCATION_WORK_TAG"

        fun isLocationPermissionEnabled(): Boolean {
            return (TripManagementApplication.getInstance()?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } == PackageManager.PERMISSION_GRANTED
                    || TripManagementApplication.getInstance()?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } == PackageManager.PERMISSION_GRANTED)
        }

        fun millisTodate(millis: Long): String {
            return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(millis)
        }
    }
}