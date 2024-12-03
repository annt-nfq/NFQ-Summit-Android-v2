package com.nfq.data.domain.repository

import arrow.core.Either
import com.nfq.data.domain.model.EventDetailsModel
import com.nfq.data.network.exception.DataException

interface NFQSummitRepository {
    suspend fun authenticateWithAttendeeCode(attendeeCode: String): Either<DataException, Unit>
    suspend fun fetchProfile(): Either<DataException, Unit>
    suspend fun fetchEventActivities(): Either<DataException, Unit>
    suspend fun getEventActivityByID(id: String): Either<DataException, EventDetailsModel>
}