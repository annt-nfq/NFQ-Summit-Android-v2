package com.nfq.data.remote.model

import com.nfq.data.cache.AttractionLocalModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttractionRemoteModel(
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("icon")
    val icon: String
)

fun AttractionLocalModel.toRemoteModel(): AttractionRemoteModel =
    AttractionRemoteModel(
        id = id.toInt(),
        title = title,
        icon = icon
    )
