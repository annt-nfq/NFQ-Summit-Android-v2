package com.nfq.data.mapper

import com.nfq.data.database.entity.AttractionEntity
import com.nfq.data.database.entity.AttractionBlogEntity
import com.nfq.data.database.entity.BlogEntity
import com.nfq.data.database.entity.EventEntity
import com.nfq.data.database.entity.UserEntity
import com.nfq.data.domain.model.Attraction
import com.nfq.data.domain.model.Blog
import com.nfq.data.domain.model.CategoryEnum
import com.nfq.data.domain.model.EventDetailsModel
import com.nfq.data.domain.model.EventLocationsModel
import com.nfq.data.domain.model.SpeakerModel
import com.nfq.data.remote.model.response.AttendeeResponse
import com.nfq.data.remote.model.response.AttractionResponse
import com.nfq.data.remote.model.response.AttractionBlogResponse
import com.nfq.data.remote.model.response.BlogResponse
import com.nfq.data.remote.model.response.EventActivityResponse
import com.nfq.data.remote.model.response.EventLocationResponse
import com.nfq.data.remote.model.response.SpeakerResponse
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
        locations = locations.orEmpty(),
        gatherTime = gatherTime.orEmpty(),
        gatherLocation = gatherLocation.orEmpty(),
        leavingTime = leavingTime.orEmpty(),
        leavingLocation = leavingLocation.orEmpty(),
        adminNotes = adminNotes.orEmpty(),
        images = images.orEmpty(),
        order = order ?: 0,
        category = category,
        isTechRock = filterTechRock(genre?.code ?: category?.code.orEmpty()),
        genre = genre,
        eventDay = eventDay,
        qrCodeUrl = qrCodeUrl.orEmpty(),
        isFavorite = isFavorite ?: false,
        speakers = speakers,
        updatedAt = 0L
    )
}

fun filterTechRock(code: String): Boolean {
    return when {
        code == CategoryEnum.TECH_ROCK.code || code == CategoryEnum.PRODUCT.code || code == CategoryEnum.BUSINESS.code || code == CategoryEnum.TECH.code -> true
        else -> false
    }
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

private const val DATE_TIME_PATTERN = "EEE, MMM d • HH:mm"

fun EventEntity.toEventDetailsModel(): EventDetailsModel {
    return EventDetailsModel(
        id = id,
        startDateTime = timeStart.toLocalDateTime(),
        startTime = timeStart.toFormattedDateTimeString(DATE_TIME_PATTERN),
        name = name,
        description = description,
        locationName = getLocationName(),
        latitude = getLatitude(),
        longitude = getLongitude(),
        locations = locations.map { it.toLocationModel() },
        isFavorite = isFavorite,
        coverPhotoUrl = getFirstValidImageUrl(),
        speakers = speakers?.map { it.toSpeakerModel() }.orEmpty()
    )
}

private fun EventEntity.getLocationName(): String {
    return when {
        location.isNotBlank() -> location
        locations.isNotEmpty() -> locations.first().name
        else -> ""
    }
}

private fun EventEntity.getLatitude(): Double {
    return when {
        latitude != 0.0 -> latitude
        locations.isNotEmpty() -> locations.first().latitude
        else -> 0.0
    }
}

private fun EventEntity.getLongitude(): Double {
    return when {
        longitude != 0.0 -> longitude
        locations.isNotEmpty() -> locations.first().longitude
        else -> 0.0
    }
}

private fun EventEntity.getFirstValidImageUrl(): String {
    return images.firstOrNull { it.isNotBlank() }.orEmpty()
}

private fun EventLocationResponse.toLocationModel(): EventLocationsModel {
    return EventLocationsModel(
        id = id,
        name = name,
        address = address,
        latitude = latitude,
        longitude = longitude
    )
}

private fun SpeakerResponse.toSpeakerModel(): SpeakerModel {
    return SpeakerModel(
        id = id,
        name = name,
        avatar = avatar
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