package com.nfq.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nfq.data.database.entity.AttractionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttractionDao {
    @Query("SELECT * FROM attraction_entity")
    fun getAttractions(): Flow<List<AttractionEntity>>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertAttractions(attractions : List<AttractionEntity>)

    @Query("DELETE FROM attraction_entity")
    suspend fun deleteAllAttractions()
}