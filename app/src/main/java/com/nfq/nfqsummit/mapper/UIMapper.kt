package com.nfq.nfqsummit.mapper

import com.nfq.data.database.EventEntity
import com.nfq.data.domain.model.SummitEvent
import com.nfq.data.toFormattedDateTimeString
import com.nfq.data.toLocalDateTime
import com.nfq.nfqsummit.model.SavedEventUIModel
import com.nfq.nfqsummit.model.UpcomingEventUIModel

fun List<EventEntity>.toSavedEventUIModels(): List<SavedEventUIModel> {
    return this.map { it.toSavedEventUIModel() }
}

private fun EventEntity.toSavedEventUIModel(): SavedEventUIModel {
    return SavedEventUIModel(
        id = id,
        imageUrl = images.find { it.isNotBlank() }.orEmpty(),
        name = name,
        date = timeStart.toFormattedDateTimeString(targetPattern = "EEE, MMM d • HH:mm"),
        tag = "\uD83D\uDCBC Summit"
    )
}

fun List<EventEntity>.toUpcomingEventUIModels(): List<UpcomingEventUIModel> {
    return this.map { it.toUpcomingEventUIModel() }
}

private fun EventEntity.toUpcomingEventUIModel(): UpcomingEventUIModel {
    return UpcomingEventUIModel(
        id = id,
        name = name,
        imageUrl = images.find { it.isNotBlank() }.orEmpty(),
        date = timeStart.toFormattedDateTimeString(targetPattern = "dd\nMMM"),
        startAndEndTime = "${timeStart.toFormattedDateTimeString(targetPattern = "HH:mm")} - ${
            timeEnd.toFormattedDateTimeString(targetPattern = "HH:mm")
        }",
        isFavorite = isFavorite,
        tag = "\uD83D\uDCBC Summit"
    )
}


fun List<EventEntity>.toSubmitEvents(): List<SummitEvent> {
    return this.map { it.toSubmitEvent() }
}

private fun EventEntity.toSubmitEvent(): SummitEvent {
    return SummitEvent(
        id = id,
        name = name,
        start = timeStart.toLocalDateTime(),
        end = timeEnd.toLocalDateTime(),
        description = description,
        latitude = latitude,
        longitude = longitude,
        coverPhotoUrl = images.find { it.isNotBlank() }.orEmpty(),
        locationName = location,
        iconUrl = images.find { it.isNotBlank() }.orEmpty(),
        isConference = isMain,
        eventType = category?.name,
        ordering = order,
        speakerName = gatherLocation,
        speakerPosition = gatherTime,
        isFavorite = isFavorite,
        tag = "\uD83D\uDCBC Summit"
    )
}