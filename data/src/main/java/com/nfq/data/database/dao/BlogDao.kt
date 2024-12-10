package com.nfq.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nfq.data.database.entity.AttractionEntity
import com.nfq.data.database.entity.BlogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlogDao {
    @Query("SELECT * FROM blog_entity WHERE attractionId = :attractionId")
    fun getBlogsByAttractionId(attractionId: String): Flow<List<BlogEntity>>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertBlogs(blogs: List<BlogEntity>)

    @Query("DELETE FROM blog_entity")
    suspend fun deleteAllBlogs()
}