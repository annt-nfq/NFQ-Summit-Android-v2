package com.nfq.data.remote

import com.nfq.data.remote.model.BlogRemoteModel

interface BlogRemote {
    suspend fun getAllBlogs(): List<BlogRemoteModel>

    suspend fun getBlogById(blogId: Int): BlogRemoteModel

    suspend fun getPaymentBlog(): BlogRemoteModel

    suspend fun getBlogsByAttractionId(attractionId: Int): List<BlogRemoteModel>

    suspend fun getTransportationBlogs(): List<BlogRemoteModel>

    suspend fun getRecommendedBlogs(): List<BlogRemoteModel>
}