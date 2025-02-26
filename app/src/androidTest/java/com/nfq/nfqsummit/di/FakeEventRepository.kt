package com.nfq.nfqsummit.di

import com.nfq.data.domain.model.Response
import com.nfq.data.domain.model.SummitEvent
import com.nfq.data.domain.repository.EventRepository
import com.nfq.nfqsummit.mocks.mockEventDay1
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import javax.inject.Inject

class FakeEventRepository @Inject constructor(): EventRepository {
    override val events: Flow<List<SummitEvent>>
        get() = flow {
            emit(
                listOf(
                    SummitEvent(
                        id = "1",
                        name = "Event 1",
                        start = LocalDateTime.of(2024, 1, 6, 10, 0),
                        end = LocalDateTime.of(2024, 1, 6, 11, 0),
                        tag = "\uD83D\uDCBC Summit"
                    ),
                    SummitEvent(
                        id = "2",
                        name = "Event 2",
                        start = LocalDateTime.of(2024, 1, 6, 11, 0),
                        end = LocalDateTime.of(2024, 1, 6, 12, 0),
                        tag = "\uD83D\uDCBC Summit"
                    ),
                    SummitEvent(
                        id = "3",
                        name = "Event 3",
                        start = LocalDateTime.of(2024, 1, 6, 12, 0),
                        end = LocalDateTime.of(2024, 1, 6, 13, 0),
                        tag = "\uD83D\uDCBC Summit"
                    )
                )
            )
        }
    override val savedEvents: Flow<List<SummitEvent>>
        get() = flow { emit(listOf()) }

    override suspend fun fetchAllEvents(forceReload: Boolean): Result<List<SummitEvent>> {
        return Result.success(
            listOf(
                SummitEvent(
                    id = "1",
                    name = "Event 1",
                    start = LocalDateTime.of(2024, 1, 6, 10, 0),
                    end = LocalDateTime.of(2024, 1, 6, 11, 0),
                    tag = "\uD83D\uDCBC Summit"
                ),
                SummitEvent(
                    id = "2",
                    name = "Event 2",
                    start = LocalDateTime.of(2024, 1, 6, 11, 0),
                    end = LocalDateTime.of(2024, 1, 6, 12, 0),
                    tag = "\uD83D\uDCBC Summit"
                ),
                SummitEvent(
                    id = "3",
                    name = "Event 3",
                    start = LocalDateTime.of(2024, 1, 6, 12, 0),
                    end = LocalDateTime.of(2024, 1, 6, 13, 0),
                    tag = "\uD83D\uDCBC Summit"
                )
            )
        )
    }

    override suspend fun getTechRocksEvents(forceReload: Boolean): Response<List<SummitEvent>> {
        return Response.Success(listOf())
    }

    override suspend fun getEventById(eventId: String): Response<SummitEvent> {
        return Response.Success(mockEventDay1)
    }

    override suspend fun saveFavoriteEvent(eventId: String) {
    }

    override suspend fun isEventFavorite(eventId: String): Boolean = true

    override suspend fun removeFavoriteEvent(eventId: String) {
    }

    override suspend fun markEventAsFavorite(isFavorite: Boolean, eventId: String) {
        TODO("Not yet implemented")
    }
}