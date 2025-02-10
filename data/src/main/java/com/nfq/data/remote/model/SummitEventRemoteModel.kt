package com.nfq.data.remote.model

import com.nfq.data.cache.Event
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SummitEventRemoteModel(
    @SerialName("id")
    val id: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("title")
    val title: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("event_start_time")
    val eventStartTime: String? = null,
    @SerialName("event_end_time")
    val eventEndTime: String? = null,
    @SerialName("cover_photo_url")
    val coverPhotoUrl: String? = null,
    @SerialName("location_name")
    val locationName: String? = null,
    @SerialName("icon")
    val iconUrl: String? = null,
    @SerialName("is_conference")
    val isConference: Boolean? = null,
    @SerialName("event_type")
    val eventType: String? = null,
    @SerialName("ordering")
    val ordering: Int? = null,
    @SerialName("speaker_name")
    val speakerName: String? = null,
    @SerialName("speaker_position")
    val speakerPosition: String? = null,
    @SerialName("is_favorite")
    val isFavorite: Boolean? = null,
    @SerialName("tag")
    val tag: String? = null
)

fun Event.toSummitEventRemoteModel(): SummitEventRemoteModel =
    SummitEventRemoteModel(
        id = id,
        createdAt = created_at ?: "",
        title = title,
        description = description,
        eventStartTime = event_start_time,
        eventEndTime = event_end_time,
        eventType = event_type,
        coverPhotoUrl = cover_photo_url,
        ordering = ordering?.toInt(),
        locationName = location_name,
        isConference = is_conference,
        speakerName = speaker_name,
        speakerPosition = speaker_position,
        iconUrl = icon,
        isFavorite = is_favorite,
        tag = tag
    )