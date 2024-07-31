package com.nfq.data.local

import com.nfq.data.remote.model.BlogRemoteModel
import kotlinx.coroutines.flow.Flow

interface BlogLocal {
    suspend fun getAllBlogs(): List<BlogRemoteModel>

    suspend fun getBlogById(id: Int): BlogRemoteModel?

    suspend fun insertBlogs(blogs: List<BlogRemoteModel>)

    suspend fun insertBlog(blog: BlogRemoteModel)

    suspend fun getBlogsByCategory(category: String): List<BlogRemoteModel>

    suspend fun getBlogsByAttraction(attractionId: Int): Flow<List<BlogRemoteModel>>

    suspend fun markBlogAsFavorite(blogId: Int, favorite: Boolean)

    suspend fun getFavoriteBlogs(): List<BlogRemoteModel>

    suspend fun clearAllBlogs()
}