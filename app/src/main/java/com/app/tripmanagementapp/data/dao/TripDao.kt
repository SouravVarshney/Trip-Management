package com.app.tripmanagementapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.app.tripmanagementapp.data.entities.TripDTO

@Dao
interface TripDao {
    @Insert
    fun insert(trip: TripDTO?)

    @Query("SELECT * FROM trip")
    fun getTrip(): List<TripDTO?>

    @Query("SELECT trip_id FROM trip GROUP BY trip_id")
    fun getAllTripId(): List<String?>

    @Query("SELECT * FROM trip WHERE trip_id LIKE :tripId")
    fun getLocationTripById(tripId: String): List<TripDTO?>
}