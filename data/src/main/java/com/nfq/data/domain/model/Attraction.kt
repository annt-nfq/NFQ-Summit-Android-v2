package com.nfq.data.domain.model

import com.nfq.data.remote.model.AttractionRemoteModel

data class Attraction(
    val id: Int,
    val title: String,
    val icon: String
)

fun AttractionRemoteModel.toAttraction() = Attraction(
    id = id,
    title = title,
    icon = icon
)
