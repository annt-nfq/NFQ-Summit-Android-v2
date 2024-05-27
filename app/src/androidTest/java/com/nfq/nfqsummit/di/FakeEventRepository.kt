package com.nfq.nfqsummit.di

import com.nfq.data.domain.model.Response
import com.nfq.data.domain.model.SummitEvent
import com.nfq.data.domain.repository.EventRepository
import java.time.LocalDateTime

class FakeEventRepository: EventRepository {
    override suspend fun getAllEvents(forceReload: Boolean): Response<List<SummitEvent>> {
        return Response.Success(
            listOf(
                SummitEvent(
                    id = "1",
                    name = "Event 1",
                    start = LocalDateTime.of(2024, 1, 6, 10, 0),
                    end = LocalDateTime.of(2024, 1, 6, 11, 0)
                ),
                SummitEvent(
                    id = "2",
                    name = "Event 2",
                    start = LocalDateTime.of(2024, 1, 6, 11, 0),
                    end = LocalDateTime.of(2024, 1, 6, 12, 0)
                ),
                SummitEvent(
                    id = "3",
                    name = "Event 3",
                    start = LocalDateTime.of(2024, 1, 6, 12, 0),
                    end = LocalDateTime.of(2024, 1, 6, 13, 0)
                )
            )
        )
    }

    override suspend fun getTechRocksEvents(forceReload: Boolean): Response<List<SummitEvent>> {
        TODO("Not yet implemented")
    }

    override suspend fun getEventById(eventId: String): Response<SummitEvent> {
        TODO("Not yet implemented")
    }

    override suspend fun saveFavoriteEvent(eventId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun isEventFavorite(eventId: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun removeFavoriteEvent(eventId: String) {
        TODO("Not yet implemented")
    }
}