package com.nfq.nfqsummit.model

import java.time.LocalDateTime

data class UpcomingEventUIModel(
    val id: String,
    val name: String,
    val date: String,
    val imageUrl: String,
    val isFavorite: Boolean,
    val startAndEndTime: String,
    val startDateTime: LocalDateTime,
    val tag: String
)
