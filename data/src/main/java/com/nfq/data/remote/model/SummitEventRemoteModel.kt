package com.nfq.data.remote.model

import com.nfq.data.cache.Event
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SummitEventRemoteModel(
    @SerialName("id")
    var id: String,
    @SerialName("created_at")
    var createdAt: String,
    @SerialName("title")
    var title: String? = null,
    @SerialName("description")
    var description: String? = null,
    @SerialName("event_start_time")
    var eventStartTime: String? = null,
    @SerialName("event_end_time")
    var eventEndTime: String? = null,
    @SerialName("cover_photo_url")
    var coverPhotoUrl: String? = null,
    @SerialName("location_name")
    var locationName: String? = null,
    @SerialName("icon")
    var iconUrl: String? = null,
    @SerialName("is_conference")
    var isConference: Boolean? = null,
    @SerialName("event_type")
    var eventType: String? = null,
    @SerialName("ordering")
    var ordering: Int? = null,
    @SerialName("speaker_name")
    var speakerName: String? = null,
    @SerialName("speaker_position")
    var speakerPosition: String? = null,
    @SerialName("is_favorite")
    var isFavorite: Boolean? = null
)

fun Event.toSummitEventRemoteModel() : SummitEventRemoteModel =
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
        isFavorite = is_favorite
    )