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
    val category: CategoryType = CategoryType.Summit("Summit")
)


enum class CategoryEnum(val code: String) {
    SUMMIT("summit"),
    K5("k5"),
    TECH_ROCK("tech_rock"),
    PRODUCT("product"),
    BUSINESS("business")
}


@Serializable
sealed class CategoryType(
    val code: String,
    val containerColor: Int,
    val contentColor: Int,
    val tag: String
) {
    data object Unknown : CategoryType("",0,0,"")
    data class Summit(val name: String = "Summit") :
        CategoryType(
            CategoryEnum.SUMMIT.code,
            0xFFFFE1CC.toInt(),
            0xFFFF6B00.toInt(),
            "\uD83D\uDCBC $name"
        )

    data class K5(val name: String) :
        CategoryType(
            CategoryEnum.K5.code,
            0xFFB4EAFF.toInt(),
            0xFF02A5E6.toInt(),
            "\uD83C\uDDE9\uD83C\uDDEA $name"
        )

    data class TechRock(val name: String) :
        CategoryType(
            CategoryEnum.TECH_ROCK.code,
            0xFFE8F5E9.toInt(),
            0xFF43A047.toInt(),
            "\uD83D\uDCBB $name"
        )

    data class Product(val name: String) :
        CategoryType(
            CategoryEnum.PRODUCT.code,
            0xFFFFF3F0.toInt(),
            0xFFFF6B6B.toInt(),
            "\uD83D\uDE80 $name"
        )

    data class Business(val name: String) :
        CategoryType(
            CategoryEnum.BUSINESS.code,
            0xFFE5EDFF.toInt(),
            0xFF42389D.toInt(),
            "\uD83D\uDCC8 $name"
        )

    companion object {
        fun filterTechRock(code: String): Boolean {
            return when {
                code == CategoryEnum.TECH_ROCK.code || code == CategoryEnum.PRODUCT.code || code == CategoryEnum.BUSINESS.code -> true
                else -> false
            }
        }

        fun filterSummitOrK5(code: String): Boolean {
            return when {
                code == CategoryEnum.SUMMIT.code || code == CategoryEnum.K5.code || code == CategoryEnum.SUMMIT.code -> true
                else -> false
            }
        }
    }
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
        isFavorite = isFavorite ?: false
    )
}