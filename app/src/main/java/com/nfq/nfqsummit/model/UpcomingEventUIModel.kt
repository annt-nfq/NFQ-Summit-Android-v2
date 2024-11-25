package com.nfq.nfqsummit.model

data class UpcomingEventUIModel(
    val id: String,
    val name: String,
    val date: String,
    val imageUrl: String,
    val isFavorite: Boolean,
    val startAndEndTime: String,
    val tag: String
)
