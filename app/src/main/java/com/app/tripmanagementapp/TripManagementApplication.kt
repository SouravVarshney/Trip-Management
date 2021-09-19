package com.app.tripmanagementapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TripManagementApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        sInstance = this
    }

    companion object {
        private var sInstance: TripManagementApplication? = null

        @Synchronized
        fun getInstance(): TripManagementApplication? {
            return sInstance
        }
    }
}