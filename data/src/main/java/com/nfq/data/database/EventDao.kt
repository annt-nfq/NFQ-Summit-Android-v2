package com.nfq.data.database

import android.adservices.topics.Topic
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM event_entity ORDER BY `order` DESC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM event_entity WHERE isFavorite = 1 ORDER BY `order` DESC")
    fun getFavoriteEvents(): Flow<List<EventEntity>>

    @Query("UPDATE event_entity SET isFavorite =:isFavorite WHERE id=:eventId")
    suspend fun updateFavorite(
        eventId: String,
        isFavorite: Boolean
    )

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)


    @Query("SELECT * FROM event_entity WHERE id=:eventId")
    suspend fun getEventById(eventId: String): EventEntity
}