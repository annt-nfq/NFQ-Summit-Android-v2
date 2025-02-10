package com.nfq.data.remote.model.response


data class BlogResponse(
    val id: String,
    val contentUrl: String,
    val country: String,
    val iconUrl: String,
    val parentBlog: String,
    val title: String
)