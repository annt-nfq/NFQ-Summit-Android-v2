package com.nfq.data.domain.repository

import com.nfq.data.domain.model.Blog
import com.nfq.data.domain.model.Response

interface BlogRepository {
    suspend fun getAllBlogs(forceReload: Boolean = false): Response<List<Blog>>

    suspend fun getBlogById(blogId: Int): Response<Blog>

    suspend fun getPaymentBlog(): Response<Blog>

    suspend fun getTransportationBlogs(): Response<List<Blog>>

    suspend fun getBlogsByAttractionId(attractionId: Int): Response<List<Blog>>
}