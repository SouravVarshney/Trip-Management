package com.app.tripmanagementapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.tripmanagementapp.data.dao.TripDao
import com.app.tripmanagementapp.data.entities.TripDTO

@Database(entities = [TripDTO::class], version = 1, exportSchema = false)
abstract class AppDB : RoomDatabase() {
    abstract fun tripDao(): TripDao
}
