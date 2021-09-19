package com.app.tripmanagementapp.model

data class LocationModel(
    val latitude: Double,
    val longitude: Double,
    val timestamp: String,
    val accuracy: Float
)