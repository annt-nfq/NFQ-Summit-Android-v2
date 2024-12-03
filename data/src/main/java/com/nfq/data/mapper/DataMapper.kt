package com.nfq.data.mapper

import com.nfq.data.database.EventEntity
import com.nfq.data.remote.model.response.EventActivityResponse

fun List<EventActivityResponse>.toEventEntities() : List<EventEntity>{
    return this.map { it.toEventEntity() }
}
fun EventActivityResponse.toEventEntity(): EventEntity {
    return EventEntity(
        id = id,
        code = code.orEmpty(),
        title = title.orEmpty(),
        name = name.orEmpty(),
        quantity = quantity ?: 0,
        isMain = isMain ?: false,
        timeStart = timeStart.orEmpty(),
        timeEnd = timeEnd.orEmpty(),
        isTimeLate = isTimeLate ?: false,
        description = description.orEmpty(),
        location = location.orEmpty(),
        latitude = latitude.orEmpty(),
        longitude = longitude.orEmpty(),
        gatherTime = gatherTime.orEmpty(),
        gatherLocation = gatherLocation.orEmpty(),
        leavingTime = leavingTime.orEmpty(),
        leavingLocation = leavingLocation.orEmpty(),
        adminNotes = adminNotes.orEmpty(),
        images = images.orEmpty(),
        order = order.orEmpty(),
        category = category.orEmpty(),
        eventDay = eventDay,
        qrCodeUrl = qrCodeUrl.orEmpty(),
        isFavorite = isFavorite ?: false
    )
}