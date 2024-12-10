package com.nfq.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nfq.data.database.entity.AttractionBlogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttractionBlogDao {
    @Query("SELECT * FROM attraction_blog_entity WHERE attractionId = :attractionId")
    fun getBlogsByAttractionId(attractionId: String): Flow<List<AttractionBlogEntity>>

    @Query("SELECT * FROM attraction_blog_entity WHERE id = :blogId")
    fun getBlogDeatils(blogId: String): Flow<AttractionBlogEntity>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertBlogs(blogs: List<AttractionBlogEntity>)

    @Query("UPDATE attraction_blog_entity SET is_favorite =:isFavorite WHERE id=:blogId")
    suspend fun updateFavouriteBlog(
        blogId: String,
        isFavorite: Boolean
    )


    @Query("DELETE FROM attraction_blog_entity")
    suspend fun deleteAllBlogs()
}