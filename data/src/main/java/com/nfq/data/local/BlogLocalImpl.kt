package com.nfq.data.local

import com.nfq.data.cache.SummitDatabase
import com.nfq.data.remote.model.BlogRemoteModel
import com.nfq.data.remote.model.toRemoteModel
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
                    it.id.toLong(),
                    it.title,
                    it.description,
                    it.iconUrl,
                    it.contentUrl,
                    it.category,
                    it.largeImageUrl,
                    false,
                    it.attractionId?.toLong()
                )
            }
        }
    }

    override suspend fun insertBlog(blog: BlogRemoteModel) {
        database.summitDatabaseQueries.insertBlog(
            blog.id.toLong(),
            blog.title,
            blog.description,
            blog.iconUrl,
            blog.contentUrl,
            blog.category,
            blog.largeImageUrl,
            false,
            blog.attractionId?.toLong()
        )
    }

    override suspend fun getBlogsByCategory(category: String): List<BlogRemoteModel> {
        return database.summitDatabaseQueries.selectBlogsByCategory(category)
            .executeAsList()
            .map {
                it.toRemoteModel()
            }
    }

    override suspend fun getBlogsByAttraction(attractionId: Int): List<BlogRemoteModel> {
        return database.summitDatabaseQueries.selectBlogsByAttraction(attractionId.toLong())
            .executeAsList()
            .map {
                it.toRemoteModel()
            }
    }

    override suspend fun markBlogAsFavorite(blogId: Int, favorite: Boolean) {
        database.summitDatabaseQueries.markBlogAsFavorite(favorite, blogId.toLong())
    }

    override suspend fun getFavoriteBlogs(): List<BlogRemoteModel> {
        return database.summitDatabaseQueries.getFavoriteBlogs()
            .executeAsList()
            .map {
                it.toRemoteModel()
            }
    }

    override suspend fun clearAllBlogs() {
        database.summitDatabaseQueries.clearBlogs()
    }
}