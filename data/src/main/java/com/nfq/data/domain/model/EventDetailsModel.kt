package com.nfq.data.domain.model

data class EventDetailsModel(
    val id: Int,
    val startTime: String,
    val name: String,
    val description: String,
    val locationName : String,
    val latitude: String?,
    val longitude: String?,
    val isFavorite: Boolean,
)
