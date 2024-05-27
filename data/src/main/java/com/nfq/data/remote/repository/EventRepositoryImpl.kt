package com.nfq.data.remote.repository

import com.nfq.data.domain.model.Response
import com.nfq.data.domain.model.SummitEvent
import com.nfq.data.domain.model.toSummitEvent
import com.nfq.data.domain.repository.EventRepository
import com.nfq.data.local.EventLocal
import com.nfq.data.remote.EventRemote
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val eventRemote: EventRemote,
    private val eventLocal: EventLocal
) : EventRepository {
    override suspend fun getAllEvents(forceReload: Boolean): Response<List<SummitEvent>> {
        val cachedEvents = eventLocal.getAllEvents()
        return if (cachedEvents.isNotEmpty() && !forceReload)
            Response.Success(cachedEvents.map { it.toSummitEvent() })
        else {
            Response.Success(eventRemote.getAllEvents().also {
                eventLocal.clearAllEvents()
                eventLocal.insertEvents(it)
            }.map { it.toSummitEvent() })
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
}