package com.nfq.data.mapper

import com.nfq.data.database.entity.AttractionEntity
import com.nfq.data.database.entity.AttractionBlogEntity
import com.nfq.data.database.entity.BlogEntity
import com.nfq.data.database.entity.EventEntity
import com.nfq.data.database.entity.UserEntity
import com.nfq.data.domain.model.Attraction
import com.nfq.data.domain.model.Blog
import com.nfq.data.domain.model.EventDetailsModel
import com.nfq.data.remote.model.response.AttendeeResponse
import com.nfq.data.remote.model.response.AttractionResponse
import com.nfq.data.remote.model.response.AttractionBlogResponse
import com.nfq.data.remote.model.response.BlogResponse
import com.nfq.data.remote.model.response.EventActivityResponse
import com.nfq.data.toFormattedDateTimeString
import com.nfq.data.toLocalDateTime
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
        startDateTime = timeStart.toLocalDateTime(),
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

fun AttractionResponse.toAttractionEntity(): AttractionEntity {
    return AttractionEntity(
        id = id,
        country = country,
        iconUrl = iconUrl,
        parentBlog = parentBlog,
        title = title
    )
}

fun List<AttractionEntity>.toAttractions(): List<Attraction> {
    return this.map { it.toAttraction() }
}

fun AttractionEntity.toAttraction(): Attraction {
    return Attraction(
        id = id,
        title = title,
        icon = iconUrl,
        country = country
    )
}

fun AttractionBlogResponse.toBlogEntity(
    attractionId: String,
    isFavorite: Boolean
): BlogEntity {
    return BlogEntity(
        id = id,
        attractionId = attractionId,
        contentUrl = contentUrl,
        title = title,
        description = description,
        iconUrl = iconUrl,
        isFavorite = isFavorite,
        parentBlog = "",
        country = ""
    )
}


fun List<AttractionBlogEntity>.toAttractionBlogs(): List<Blog> {
    return this.map { it.toBlog() }
}

fun AttractionBlogEntity.toBlog(): Blog {
    return Blog(
        id = id,
        attractionId = attractionId,
        contentUrl = contentUrl,
        title = title,
        description = description,
        iconUrl = iconUrl,
        largeImageUrl = "",
        isRecommended = false,
        isFavorite = isFavorite
    )
}

fun BlogResponse.toBlogEntity(): BlogEntity {
    return BlogEntity(
        id = id,
        contentUrl = contentUrl,
        country = country,
        iconUrl = iconUrl,
        parentBlog = parentBlog,
        title = title,
        isFavorite = false,
        description = "",
        attractionId = ""
    )
}

fun List<BlogEntity>.toBlogs(): List<Blog> {
    return this.map { it.toBlog() }
}

fun BlogEntity.toBlog(): Blog {
    return Blog(
        id = id,
        attractionId = "",
        contentUrl = contentUrl,
        title = title,
        description = "",
        iconUrl = iconUrl,
        largeImageUrl = "",
        isRecommended = false,
        isFavorite = false
    )
}