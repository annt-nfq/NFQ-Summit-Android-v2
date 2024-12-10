package com.nfq.data.domain.model

import com.nfq.data.remote.model.AttractionRemoteModel

data class Attraction(
    val id: String,
    val title: String,
    val icon: String,
    val country: String = ""
)

fun AttractionRemoteModel.toAttraction() = Attraction(
    id = id.toString(),
    title = title,
    icon = icon
)
