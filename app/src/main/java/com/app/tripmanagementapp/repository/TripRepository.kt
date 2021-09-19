package com.app.tripmanagementapp.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.tripmanagementapp.data.AppDB
import com.app.tripmanagementapp.data.entities.TripDTO
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class TripRepository @Inject constructor(
    @ApplicationContext val context: Context,
    val database: AppDB
) {
    private val locationResponse = MutableLiveData<List<TripDTO>>()
    private val tripID = MutableLiveData<List<String>>()
    private val locationResponseById = MutableLiveData<List<TripDTO>>()
    private val tripId = mutableListOf<TripDTO>()

    fun saveLocation(location: TripDTO) =
        GlobalScope.launch { database.tripDao().insert(location) }

    fun getAllSavedLocation(): LiveData<List<TripDTO>> {
        val response = database.tripDao().getTrip()
        locationResponse.postValue(response as List<TripDTO>)
        return locationResponse
    }

    fun getAllTripId(): LiveData<List<String>> {
        val response = database.tripDao().getAllTripId()
        tripID.postValue(response as List<String>)
        return tripID
    }

    fun getLocationTripById(tripId: String): LiveData<List<TripDTO>> {
        val response = database.tripDao().getLocationTripById(tripId)
        locationResponseById.postValue(response as List<TripDTO>)
        return locationResponseById
    }

    fun getLocationTripByIdNew(tripId: String): List<TripDTO?> {
        return database.tripDao().getLocationTripById(tripId)
    }
}