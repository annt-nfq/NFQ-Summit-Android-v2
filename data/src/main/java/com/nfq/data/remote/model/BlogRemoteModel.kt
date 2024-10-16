package com.nfq.data.remote.model

import com.nfq.data.cache.BlogLocalModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class BlogRemoteModel(
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("icon_url")
    val iconUrl: String,
    @SerialName("content_url")
    val contentUrl: String,
    @SerialName("attraction")
    val attractionId: Int?,
    @SerialName("category")
    val category: String,
    @SerialName("large_image_url")
    val largeImageUrl: String?,
    @SerialName("is_recommended")
    val isRecommended: Boolean,
    @Transient
    val isFavorite: Boolean = false
)

fun BlogLocalModel.toRemoteModel(): BlogRemoteModel =
    BlogRemoteModel(
        id = id.toInt(),
        title = title,
        description = description,
        iconUrl = icon_url,
        contentUrl = content_url,
        attractionId = attraction_id?.toInt(),
        category = category,
        largeImageUrl = large_image_url,
        isRecommended = is_recommended,
        isFavorite = is_favorite
    )
