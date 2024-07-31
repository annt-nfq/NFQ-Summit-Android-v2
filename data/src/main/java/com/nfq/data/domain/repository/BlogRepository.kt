package com.nfq.data.domain.repository

import com.nfq.data.domain.model.Blog
import com.nfq.data.domain.model.Response
import kotlinx.coroutines.flow.Flow

interface BlogRepository {
    suspend fun getAllBlogs(forceReload: Boolean = false): Response<List<Blog>>

    suspend fun getBlogById(blogId: Int): Response<Blog>

    suspend fun getPaymentBlog(): Response<Blog>

    suspend fun getTransportationBlogs(): Response<List<Blog>>

    suspend fun getBlogsByAttractionId(attractionId: Int): Flow<Response<List<Blog>>>

    fun getFavoriteBlogs(): Flow<Response<List<Blog>>>

    suspend fun markBlogAsFavorite(blog: Blog, favorite: Boolean)
}