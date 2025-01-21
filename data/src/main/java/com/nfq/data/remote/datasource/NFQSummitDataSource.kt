package com.nfq.data.remote.datasource

import arrow.core.Either
import com.nfq.data.network.exception.DataException
import com.nfq.data.remote.model.response.AttendeeResponse
import com.nfq.data.remote.model.response.EventActivityDetailsResponse
import com.nfq.data.remote.model.response.EventActivityResponse
import com.nfq.data.remote.model.response.ProfileResponse
import com.nfq.data.remote.model.response.VoucherResponse

interface NFQSummitDataSource {

    suspend fun authenticateWithAttendeeCode(
        attendeeCode: String
    ): Either<DataException, AttendeeResponse>

    suspend fun logout(): Either<DataException, String>

    suspend fun getProfile(): Either<DataException, ProfileResponse>

    suspend fun getEventActivities(registrantId: String? = ""): Either<DataException, List<EventActivityResponse>>

    suspend fun getEventActivityByID(
        id: String
    ): Either<DataException, EventActivityDetailsResponse>

    suspend fun getMealVouchers(): Either<DataException, List<VoucherResponse>>
}