package com.nfq.data.remote.repository

import com.nfq.data.domain.model.Response
import com.nfq.data.domain.model.SummitEvent
import com.nfq.data.domain.model.toSummitEvent
import com.nfq.data.domain.repository.EventRepository
import com.nfq.data.local.EventLocal
import com.nfq.data.remote.EventRemote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val eventRemote: EventRemote,
    private val eventLocal: EventLocal
) : EventRepository {
    override val events: Flow<List<SummitEvent>>
        get() = eventLocal
            .events
            .map { it.map { event -> event.toSummitEvent() } }

    override val savedEvents: Flow<List<SummitEvent>>
        get() = eventLocal
            .savedEvents
            .map { it.map { event -> event.toSummitEvent() } }

    override suspend fun fetchAllEvents(forceReload: Boolean): Response<List<SummitEvent>> {
        return try {
            val cachedEvents = eventLocal.getAllEvents()
            val remoteEvents = eventRemote.getAllEvents()
            val allEvents = (cachedEvents + remoteEvents).distinctBy { it.id }
            eventLocal.insertEvents(allEvents)
            Response.Success(allEvents.map { it.toSummitEvent() })
        } catch (e: Exception) {
            return Response.Failure(e)
        }
    }

    override suspend fun getTechRocksEvents(forceReload: Boolean): Response<List<SummitEvent>> {
        val cachedEvents = eventLocal.getTechRocksEvents()
        return if (cachedEvents.isNotEmpty() && !forceReload)
            Response.Success(cachedEvents.map { it.toSummitEvent() })
        else {
            Response.Success(eventRemote.getTechRocksEvents().map { it.toSummitEvent() })
        }
    }

    override suspend fun getEventById(eventId: String): Response<SummitEvent> {
        return eventLocal.getEventById(eventId)?.let {
            return Response.Success(it.toSummitEvent())
        } ?: Response.Success(eventRemote.getEventById(eventId).toSummitEvent())
    }

    override suspend fun saveFavoriteEvent(eventId: String) {
        eventLocal.saveFavoriteEvent(eventId)
    }

    override suspend fun isEventFavorite(eventId: String): Boolean {
        return eventLocal.getFavoriteEvent(eventId) != null
    }

    override suspend fun removeFavoriteEvent(eventId: String) {
        eventLocal.removeFavoriteEvent(eventId)
    }

    override suspend fun markEventAsFavorite(isFavorite: Boolean, eventId: String) {
        eventLocal.markEventAsFavorite(isFavorite, eventId)
    }
}