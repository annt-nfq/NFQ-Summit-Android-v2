package com.nfq.data.remote.model.response


data class BlogResponse(
    val id: String,
    val attractionId: String,
    val contentUrl: String,
    val title: String,
    val description: String,
    val iconUrl: String,
    val parentBlog: String
)