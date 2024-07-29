package com.nfq.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    val largeImageUrl: String?
)
