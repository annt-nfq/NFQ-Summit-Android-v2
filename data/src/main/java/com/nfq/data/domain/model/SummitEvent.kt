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
    val speakerProfileUrl: String? = null,
    val speakerName: String? = null,
    val speakerPosition: String? = null,
    val isFavorite: Boolean = false,
    val tag: String
)

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
        speakerPosition = speakerPosition,
        isFavorite = isFavorite ?: false,
        tag = tag ?: "\uD83D\uDCBC Summit"
    )
}