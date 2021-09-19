package com.app.tripmanagementapp.utility

import android.app.Application
import androidx.room.Room
import com.app.tripmanagementapp.data.AppDB
import com.google.android.gms.location.LocationRequest
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun locationRequest(): LocationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = 1 * 1000
        fastestInterval = 1 * 1000
    }

    @Provides
    @Singleton
    fun provideDatabase(application: Application): AppDB =
        Room.databaseBuilder(application, AppDB::class.java, "tripLocation")
            .fallbackToDestructiveMigration().allowMainThreadQueries().build()
}