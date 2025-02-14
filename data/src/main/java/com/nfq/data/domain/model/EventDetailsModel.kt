package com.nfq.data.domain.model

import java.time.LocalDateTime

data class EventDetailsModel(
    val id: String,
    val startDateTime: LocalDateTime,
    val startTime: String,
    val name: String,
    val description: String,
    val locationName: String,
    val latitude: Double?,
    val longitude: Double?,
    val locations: List<EventLocationsModel>,
    val coverPhotoUrl: String,
    val isFavorite: Boolean,
    val speakers: List<SpeakerModel>
)

data class EventLocationsModel(
    val id: Int,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)
