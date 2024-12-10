package com.nfq.data.domain.model

import com.nfq.data.remote.model.BlogRemoteModel

data class Blog(
    val id: String,
    val title: String,
    val description: String,
    val iconUrl: String,
    val contentUrl: String,
    val attractionId: String?,
    val largeImageUrl: String? = null,
    val isFavorite: Boolean,
    val isRecommended: Boolean
)

fun BlogRemoteModel.toBlog(): Blog =
    Blog(
        id = id.toString(),
        title = title,
        description = description,
        iconUrl = iconUrl,
        contentUrl = contentUrl,
        attractionId = attractionId.toString(),
        largeImageUrl = largeImageUrl,
        isFavorite = isFavorite,
        isRecommended = isRecommended
    )
