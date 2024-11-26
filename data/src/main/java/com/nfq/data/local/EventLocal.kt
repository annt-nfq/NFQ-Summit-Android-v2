package com.nfq.data.local

import com.nfq.data.remote.model.SummitEventRemoteModel
import kotlinx.coroutines.flow.Flow

interface EventLocal {

    val events: Flow<List<SummitEventRemoteModel>>

    val savedEvents: Flow<List<SummitEventRemoteModel>>

    suspend fun clearAllEvents()

    suspend fun getAllEvents(): List<SummitEventRemoteModel>

    suspend fun getTechRocksEvents(): List<SummitEventRemoteModel>

    suspend fun insertEvents(events: List<SummitEventRemoteModel>)

    suspend fun getEventById(id: String): SummitEventRemoteModel?

    suspend fun saveFavoriteEvent(eventId: String)

    suspend fun getFavoriteEvent(eventId: String): String?

    suspend fun removeFavoriteEvent(eventId: String)

    suspend fun markEventAsFavorite(isFavorite: Boolean, eventId: String)
}