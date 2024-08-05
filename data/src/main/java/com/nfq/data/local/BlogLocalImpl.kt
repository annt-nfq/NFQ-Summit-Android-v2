package com.nfq.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.nfq.data.cache.SummitDatabase
import com.nfq.data.remote.model.BlogRemoteModel
import com.nfq.data.remote.model.toRemoteModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BlogLocalImpl @Inject constructor(
    private val database: SummitDatabase
) : BlogLocal {

    override suspend fun getAllBlogs(): List<BlogRemoteModel> {
        return database.summitDatabaseQueries.selectAllBlogs().executeAsList().map {
            it.toRemoteModel()
        }
    }

    override suspend fun getBlogById(id: Int): BlogRemoteModel? {
        return database.summitDatabaseQueries
            .selectBlog(id.toLong())
            .executeAsOneOrNull()
            ?.toRemoteModel()
    }

    override suspend fun insertBlogs(blogs: List<BlogRemoteModel>) {
        database.summitDatabaseQueries.transaction {
            blogs.forEach {
                database.summitDatabaseQueries.insertBlog(
                    id = it.id.toLong(),
                    title = it.title,
                    description = it.description,
                    icon_url = it.iconUrl,
                    content_url = it.contentUrl,
                    category = it.category,
                    large_image_url = it.largeImageUrl,
                    is_favorite = false,
                    attraction_id = it.attractionId?.toLong(),
                    is_recommended = it.isRecommended
                )
            }
        }
    }

    override suspend fun insertBlog(blog: BlogRemoteModel) {
        database.summitDatabaseQueries.insertBlog(
            id = blog.id.toLong(),
            title = blog.title,
            description = blog.description,
            icon_url = blog.iconUrl,
            content_url = blog.contentUrl,
            category = blog.category,
            large_image_url = blog.largeImageUrl,
            is_favorite = false,
            attraction_id = blog.attractionId?.toLong(),
            is_recommended = blog.isRecommended
        )
    }

    override suspend fun getBlogsByCategory(category: String): List<BlogRemoteModel> {
        return database.summitDatabaseQueries.selectBlogsByCategory(category)
            .executeAsList()
            .map {
                it.toRemoteModel()
            }
    }

    override suspend fun getBlogsByAttraction(attractionId: Int): Flow<List<BlogRemoteModel>> {
        return database.summitDatabaseQueries.selectBlogsByAttraction(attractionId.toLong())
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map {
                it.map { it.toRemoteModel() }
            }
    }

    override suspend fun markBlogAsFavorite(blogId: Int, favorite: Boolean) {
        database.summitDatabaseQueries.markBlogAsFavorite(favorite, blogId.toLong())
    }

    override suspend fun getFavoriteBlogs(): Flow<List<BlogRemoteModel>> {
        return database.summitDatabaseQueries.getFavoriteBlogs()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map {
                it.map { it.toRemoteModel() }
            }
    }

    override suspend fun getRecommendedBlogs(): Flow<List<BlogRemoteModel>> {
        return database.summitDatabaseQueries.getRecommendedBlogs()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map {
                it.map { it.toRemoteModel() }
            }
    }

    override suspend fun clearAllBlogs() {
        database.summitDatabaseQueries.clearBlogs()
    }
}