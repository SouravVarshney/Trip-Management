package com.app.tripmanagementapp.model

data class TripModel(
    val trip_id: String,
    val start_time: String,
    val end_time: String,
    val locations: List<LocationModel>
)