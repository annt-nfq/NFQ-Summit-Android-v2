package com.nfq.data.domain.model

import com.nfq.data.remote.model.BlogRemoteModel

data class Blog(
    val id: Int,
    val title: String,
    val description: String,
    val iconUrl: String,
    val contentUrl: String,
    val attractionId: Int?,
    val largeImageUrl: String? = null,
    val isFavorite: Boolean,
    val isRecommended: Boolean
)

fun BlogRemoteModel.toBlog(): Blog =
    Blog(
        id = id,
        title = title,
        description = description,
        iconUrl = iconUrl,
        contentUrl = contentUrl,
        attractionId = attractionId,
        largeImageUrl = largeImageUrl,
        isFavorite = isFavorite,
        isRecommended = isRecommended
    )
