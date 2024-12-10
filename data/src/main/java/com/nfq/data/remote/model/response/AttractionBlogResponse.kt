package com.nfq.data.remote.model.response


data class AttractionBlogResponse(
    val id: String,
    val attractionId: String,
    val contentUrl: String,
    val title: String,
    val description: String,
    val iconUrl: String
)