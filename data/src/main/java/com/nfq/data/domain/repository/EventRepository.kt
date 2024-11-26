package com.nfq.data.domain.repository

import com.nfq.data.domain.model.Response
import com.nfq.data.domain.model.SummitEvent
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    val events: Flow<List<SummitEvent>>

    val savedEvents: Flow<List<SummitEvent>>

    suspend fun fetchAllEvents(forceReload: Boolean = false): Response<List<SummitEvent>>

    suspend fun getTechRocksEvents(forceReload: Boolean = false): Response<List<SummitEvent>>

    suspend fun getEventById(eventId: String): Response<SummitEvent>

    suspend fun saveFavoriteEvent(eventId: String)

    suspend fun isEventFavorite(eventId: String): Boolean

    suspend fun removeFavoriteEvent(eventId: String)

    suspend fun markEventAsFavorite(isFavorite: Boolean, eventId: String)
}