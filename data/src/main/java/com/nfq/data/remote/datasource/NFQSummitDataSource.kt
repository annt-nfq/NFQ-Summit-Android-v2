package com.nfq.data.remote.datasource

import arrow.core.Either
import com.nfq.data.network.exception.DataException
import com.nfq.data.remote.model.response.AttendeeResponse
import com.nfq.data.remote.model.response.EventActivityDetailsResponse
import com.nfq.data.remote.model.response.EventActivityResponse
import com.nfq.data.remote.model.response.ProfileResponse

interface NFQSummitDataSource {

    suspend fun authenticateWithAttendeeCode(
        attendeeCode: String
    ): Either<DataException, AttendeeResponse>

    suspend fun getProfile(): Either<DataException, ProfileResponse>

    suspend fun getEventActivities(): Either<DataException, List<EventActivityResponse>>

    suspend fun getEventActivityByID(
        id: String
    ): Either<DataException, EventActivityDetailsResponse>
}