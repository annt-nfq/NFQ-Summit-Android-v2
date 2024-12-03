package com.nfq.data.domain.model

data class EventDetailsModel(
    val id: String,
    val startTime: String,
    val name: String,
    val description: String,
    val locationName : String,
    val latitude: Double?,
    val longitude: Double?,
    val coverPhotoUrl: String,
    val isFavorite: Boolean,
)
