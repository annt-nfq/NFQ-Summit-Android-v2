package com.nfq.data.remote.model.response


data class AttractionResponse(
    val id: String,
    val blogs: List<AttractionBlogResponse>,
    val country: String,
    val iconUrl: String,
    val parentBlog: String,
    val title: String,
)

