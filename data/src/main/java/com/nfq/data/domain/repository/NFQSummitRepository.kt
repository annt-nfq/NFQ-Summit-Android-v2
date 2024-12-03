package com.nfq.data.domain.repository

import arrow.core.Either
import com.nfq.data.database.EventEntity
import com.nfq.data.domain.model.EventDetailsModel
import com.nfq.data.network.exception.DataException
import kotlinx.coroutines.flow.Flow

interface NFQSummitRepository {
    val events: Flow<List<EventEntity>>
    val savedEvents: Flow<List<EventEntity>>
    suspend fun authenticateWithAttendeeCode(attendeeCode: String): Either<DataException, Unit>
    suspend fun fetchProfile(): Either<DataException, Unit>
    suspend fun fetchEventActivities(): Either<DataException, Unit>
    suspend fun getEventActivityByID(id: String): Either<DataException, EventDetailsModel>
    suspend fun updateFavorite(eventId: String, isFavorite: Boolean): Either<DataException, Unit>
}