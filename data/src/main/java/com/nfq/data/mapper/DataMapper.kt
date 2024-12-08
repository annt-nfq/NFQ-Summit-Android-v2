package com.nfq.data.mapper

import com.nfq.data.database.entity.EventEntity
import com.nfq.data.database.entity.UserEntity
import com.nfq.data.domain.model.EventDetailsModel
import com.nfq.data.remote.model.response.AttendeeResponse
import com.nfq.data.remote.model.response.EventActivityResponse
import com.nfq.data.toFormattedDateTimeString
import com.nfq.data.toLocalDateTimeInMillis

fun List<EventActivityResponse>.toEventEntities(): List<EventEntity> {
    return this.map { it.toEventEntity() }
}

fun EventActivityResponse.toEventEntity(): EventEntity {
    return EventEntity(
        id = id.toString(),
        code = code.orEmpty(),
        title = title.orEmpty(),
        name = name.orEmpty(),
        quantity = quantity ?: 0,
        isMain = isMain ?: false,
        timeStart = timeStart.toLocalDateTimeInMillis(),
        timeEnd = timeEnd.toLocalDateTimeInMillis(),
        isTimeLate = isTimeLate ?: false,
        description = description.orEmpty(),
        location = location.orEmpty(),
        latitude = latitude ?: 0.0,
        longitude = longitude ?: 0.0,
        gatherTime = gatherTime.orEmpty(),
        gatherLocation = gatherLocation.orEmpty(),
        leavingTime = leavingTime.orEmpty(),
        leavingLocation = leavingLocation.orEmpty(),
        adminNotes = adminNotes.orEmpty(),
        images = images.orEmpty(),
        order = order ?: 0,
        category = category,
        eventDay = eventDay,
        qrCodeUrl = qrCodeUrl.orEmpty(),
        isFavorite = isFavorite ?: false
    )
}

fun AttendeeResponse.toUserEntity(): UserEntity {
    return UserEntity(
        id = id.toString(),
        firstName = firstName.orEmpty(),
        lastName = lastName.orEmpty(),
        email = email.orEmpty(),
        qrCodeUrl = qrCodeUrl.orEmpty(),
        attendeeCode = attendeeCode.orEmpty(),
        tk = tk.orEmpty()
    )
}

fun EventEntity.toEventDetailsModel(): EventDetailsModel {
    val startTime = this
        .timeStart
        .toFormattedDateTimeString(targetPattern = "EEE, MMM d • HH:mm")
    return EventDetailsModel(
        id = id,
        startTime = startTime,
        name = name,
        description = description,
        locationName = location,
        latitude = latitude,
        longitude = longitude,
        isFavorite = isFavorite,
        coverPhotoUrl = images.find { image -> image.isNotBlank() }.orEmpty()
    )
}