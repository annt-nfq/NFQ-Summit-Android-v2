package com.nfq.nfqsummit.mapper

import android.content.Context
import android.graphics.Bitmap
import com.nfq.data.database.entity.EventEntity
import com.nfq.data.database.entity.UserEntity
import com.nfq.data.database.entity.VoucherEntity
import com.nfq.data.domain.model.CategoryEnum
import com.nfq.data.domain.model.CategoryType
import com.nfq.data.domain.model.SpeakerModel
import com.nfq.data.domain.model.SummitEvent
import com.nfq.data.remote.model.response.CategoryResponse
import com.nfq.data.remote.model.response.GenreResponse
import com.nfq.data.toFormattedDateTimeString
import com.nfq.data.toLocalDateTime
import com.nfq.nfqsummit.components.ImageCache
import com.nfq.nfqsummit.model.SavedEventUIModel
import com.nfq.nfqsummit.model.UpcomingEventUIModel
import com.nfq.nfqsummit.model.UserUIModel
import com.nfq.nfqsummit.model.VoucherUIModel

fun List<EventEntity>.toSavedEventUIModels(): List<SavedEventUIModel> {
    return this.map { it.toSavedEventUIModel() }
}

suspend fun List<VoucherEntity>.toVoucherUIModels(context: Context): Map<String,List<VoucherUIModel>> {
    val imageCache =  ImageCache(context = context)
    return this.map {
        VoucherUIModel(
            type = it.type,
            date = it.date,
            location = it.location,
            price = it.price,
            imageUrl = it.imageUrl,
            imageBitmap = imageCache.getImage(it.imageUrl),
            sponsorLogoUrls = it.sponsorLogoUrls
        )
    }.groupBy { it.date }
}

private fun EventEntity.toSavedEventUIModel(): SavedEventUIModel {
    val categoryEnum = category.toCategoryType(genre)
    return SavedEventUIModel(
        id = id,
        imageUrl = images.find { it.isNotBlank() }.orEmpty(),
        name = name,
        date = timeStart.toFormattedDateTimeString(targetPattern = "EEE, MMM d • HH:mm"),
        category = categoryEnum
    )
}

fun List<EventEntity>.toUpcomingEventUIModels(): List<UpcomingEventUIModel> {
    return this.map { it.toUpcomingEventUIModel() }
}

private fun EventEntity.toUpcomingEventUIModel(): UpcomingEventUIModel {
    val categoryType = category.toCategoryType(genre)
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
        category = categoryType
    )
}


fun List<EventEntity>.toSubmitEvents(): List<SummitEvent> {
    return this.map { it.toSubmitEvent() }
}

private fun EventEntity.toSubmitEvent(): SummitEvent {
    val categoryType = category.toCategoryType(genre)
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
        speakers = speakers?.map {
            SpeakerModel(
                id = it.id,
                name = it.name,
                avatar = it.avatar
            )
        }.orEmpty(),
        speakerPosition = gatherTime,
        isFavorite = isFavorite,
        category = categoryType
    )
}


private fun CategoryResponse?.toCategoryType(genre: GenreResponse?): CategoryType {
    val (code, name) = Pair(
        genre?.code ?: this?.code.orEmpty(),
        genre?.name ?: this?.name.orEmpty()
    )
    return when (code) {
        CategoryEnum.SUMMIT.code -> CategoryType.Summit(name)
        CategoryEnum.K5.code -> CategoryType.K5(name)
        CategoryEnum.PRODUCT.code -> CategoryType.Product(name)
        CategoryEnum.BUSINESS.code -> CategoryType.Business(name)
        CategoryEnum.TECH.code -> CategoryType.Tech(name)
        CategoryEnum.TECH_ROCK.code -> CategoryType.TechRock("Scaling Business")
        else -> CategoryType.Summit("Summit")
    }
}

fun UserEntity.toUserUIModel(qrCodeBitmap: Bitmap?): UserUIModel {
    return UserUIModel(
        id = id,
        name = "$firstName $lastName",
        email = email,
        qrCodeUrl = qrCodeUrl,
        attendeeCode = attendeeCode,
        qrCodeBitmap = qrCodeBitmap
    )
}