package com.nfq.nfqsummit.model

import com.nfq.data.domain.model.CategoryType
import java.time.LocalDateTime

data class UpcomingEventUIModel(
    val id: String,
    val name: String,
    val date: String,
    val imageUrl: String,
    val isFavorite: Boolean,
    val startAndEndTime: String,
    val startDateTime: LocalDateTime,
    val category: CategoryType = CategoryType.Summit()
)
