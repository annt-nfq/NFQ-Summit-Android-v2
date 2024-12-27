package com.nfq.nfqsummit.mapper

import com.nfq.data.database.entity.EventEntity
import com.nfq.data.database.entity.UserEntity
import com.nfq.data.domain.model.CategoryEnum
import com.nfq.data.domain.model.SummitEvent
import com.nfq.data.remote.model.response.CategoryResponse
import com.nfq.data.toFormattedDateTimeString
import com.nfq.data.toLocalDateTime
import com.nfq.nfqsummit.model.SavedEventUIModel
import com.nfq.nfqsummit.model.UpcomingEventUIModel
import com.nfq.nfqsummit.model.UserUIModel

fun List<EventEntity>.toSavedEventUIModels(): List<SavedEventUIModel> {
    return this.map { it.toSavedEventUIModel() }
}

private fun EventEntity.toSavedEventUIModel(): SavedEventUIModel {
    val categoryEnum = category.toCategoryEnum()
    return SavedEventUIModel(
        id = id,
        imageUrl = images.find { it.isNotBlank() }.orEmpty(),
        name = name,
        date = timeStart.toFormattedDateTimeString(targetPattern = "EEE, MMM d • HH:mm"),
        tag = category.toCategoryEnum().tag,
        category = categoryEnum
    )
}

fun List<EventEntity>.toUpcomingEventUIModels(): List<UpcomingEventUIModel> {
    return this.map { it.toUpcomingEventUIModel() }
}

private fun EventEntity.toUpcomingEventUIModel(): UpcomingEventUIModel {
    val categoryEnum = category.toCategoryEnum()
    return UpcomingEventUIModel(
        id = id,
        name = name,
        imageUrl = images.find { it.isNotBlank() }.orEmpty(),
        date = timeStart.toFormattedDateTimeString(targetPattern = "dd\nMMM"),
        startAndEndTime = "${timeStart.toFormattedDateTimeString(targetPattern = "HH:mm")} - ${
            timeEnd.toFormattedDateTimeString(targetPattern = "HH:mm")
        }",
        startDateTime = timeStart.toLocalDateTime(),
        isFavorite = isFavorite,
        tag = categoryEnum.tag,
        category = categoryEnum
    )
}


fun List<EventEntity>.toSubmitEvents(): List<SummitEvent> {
    return this.map { it.toSubmitEvent() }
}

private fun EventEntity.toSubmitEvent(): SummitEvent {
    val categoryEnum = category.toCategoryEnum()
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
        speakerName = speaker?.name.orEmpty(),
        speakerAvatar = speaker?.avatar.orEmpty(),
        speakerPosition = gatherTime,
        isFavorite = isFavorite,
        tag = categoryEnum.tag,
        category = categoryEnum
    )
}

private fun CategoryResponse?.toCategoryEnum(): CategoryEnum {
    return when (this?.code) {
        CategoryEnum.SUMMIT.code -> CategoryEnum.SUMMIT
        CategoryEnum.K5.code -> CategoryEnum.K5
        CategoryEnum.TECH_ROCK.code -> CategoryEnum.TECH_ROCK
        else -> CategoryEnum.SUMMIT
    }
}

fun UserEntity.toUserUIModel(): UserUIModel {
    return UserUIModel(
        id = id,
        name = "$firstName $lastName",
        email = email,
        qrCodeUrl = qrCodeUrl,
        attendeeCode = attendeeCode
    )
}