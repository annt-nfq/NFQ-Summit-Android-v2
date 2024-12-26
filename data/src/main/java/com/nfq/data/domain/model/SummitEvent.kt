package com.nfq.data.domain.model

import com.nfq.data.remote.model.SummitEventRemoteModel
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class SummitEvent(
    val id: String,
    val name: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val start: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val end: LocalDateTime,
    val description: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val coverPhotoUrl: String? = null,
    val locationName: String? = null,
    val iconUrl: String? = null,
    val isConference: Boolean = false,
    val eventType: String? = null,
    val ordering: Int = 0,
    val speakerAvatar: String = "",
    val speakerName: String = "",
    val speakerPosition: String? = null,
    val isFavorite: Boolean = false,
    val tag: String,
    val category: CategoryEnum = CategoryEnum.SUMMIT
)


enum class CategoryEnum(
    val code: String,
    val containerColor: Int,
    val contentColor: Int,
    val tag: String
) {
    SUMMIT("summit", 0xFFFFE1CC.toInt(), 0xFFFF6B00.toInt(), "\uD83D\uDCBC Summit"),
    K5("k5", 0xFFB4EAFF.toInt(), 0xFF02A5E6.toInt(), "\uD83C\uDDE9\uD83C\uDDEA K5"),
    TECH_ROCK("tech_rock", 0xFFE5EDFF.toInt(), 0xFF42389D.toInt(), "\uD83E\uDD16 Tech Rock")
}


object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        val string = value.toString()
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        val string = decoder.decodeString()
        return LocalDateTime.parse(string)
    }
}


fun SummitEventRemoteModel.toSummitEvent(): SummitEvent {
    return SummitEvent(
        id = id,
        name = title ?: "",
        description = description,
        start = ZonedDateTime.parse(eventStartTime, DateTimeFormatter.ISO_ZONED_DATE_TIME)
            .toLocalDateTime(),
        end = ZonedDateTime.parse(eventEndTime, DateTimeFormatter.ISO_ZONED_DATE_TIME)
            .toLocalDateTime(),
        coverPhotoUrl = coverPhotoUrl,
        locationName = locationName,
        iconUrl = iconUrl,
        isConference = isConference ?: false,
        eventType = eventType ?: "",
        ordering = ordering ?: 0,
        speakerName = speakerName ?: "",
        speakerAvatar = "",
        speakerPosition = speakerPosition,
        isFavorite = isFavorite ?: false,
        tag = tag ?: "\uD83D\uDCBC Summit"
    )
}