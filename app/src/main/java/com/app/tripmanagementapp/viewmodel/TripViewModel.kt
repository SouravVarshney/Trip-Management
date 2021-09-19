package com.app.tripmanagementapp.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.tripmanagementapp.data.entities.TripDTO
import com.app.tripmanagementapp.repository.TripRepository
import com.app.tripmanagementapp.utility.AppUtils
import com.app.tripmanagementapp.utility.Result
import com.app.tripmanagementapp.utility.isLocationEnabled
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import dagger.hilt.android.qualifiers.ApplicationContext


@SuppressLint("StaticFieldLeak")
class TripViewModel @ViewModelInject constructor(
    @ApplicationContext val context: Context,
    private val tripRepository: TripRepository,
    private val locationRequest: LocationRequest,
) :
    ViewModel() {

    val enableLocation: MutableLiveData<Result<Boolean>> = MutableLiveData()

    val location: MutableLiveData<List<TripDTO>> = MutableLiveData()

    val handler = Handler(Looper.getMainLooper())
    fun locationSetup() {
        enableLocation.value = Result.Loading()
        LocationServices.getSettingsClient(context)
            .checkLocationSettings(
                LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)
                    .setAlwaysShow(true)
                    .build()
            )
            .addOnSuccessListener { enableLocation.value = Result.Success(true) }
            .addOnFailureListener {
                enableLocation.value = Result.Error(it)
            }
    }

    fun trackLocation() {
        val tripId = (1..1000000).random()
        handler.postDelayed(object : Runnable {
            override fun run() {
                getFusedLocation(tripId)
                handler.postDelayed(this, 5000)
            }
        }, 0)
    }

    fun stopTrackLocation() {
        handler.removeCallbacksAndMessages(null);
    }

    fun getFusedLocation(tripId: Int) {
        if (context.isLocationEnabled() && AppUtils.isLocationPermissionEnabled()) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            Log.d("Location", "getFusedLocation")
            LocationServices.getFusedLocationProviderClient(context).lastLocation.addOnSuccessListener {
                if (it != null) {
                    tripRepository.saveLocation(
                        TripDTO(
                            0,
                            tripId.toString(),
                            it.latitude,
                            it.longitude,
                            System.currentTimeMillis(),
                            it.accuracy
                        )
                    )
                }
            }
        }
    }

    /*fun getAllSavedLocation() = tripRepository.getAllSavedLocation()*/

    fun getAllTripId() = tripRepository.getAllTripId()

    fun getLocationTripById(tripId: String) = tripRepository.getLocationTripById(tripId)

    fun getLocationTripByIdWithoutLiveData(tripId: String) =
        tripRepository.getLocationTripByIdNew(tripId)
}