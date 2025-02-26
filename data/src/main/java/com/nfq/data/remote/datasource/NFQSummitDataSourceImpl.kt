package com.nfq.data.remote.datasource

import arrow.core.Either
import com.nfq.data.network.exception.DataException
import com.nfq.data.network.handler.handleCall
import com.nfq.data.remote.model.request.AttendeeRequest
import com.nfq.data.remote.model.response.AttendeeResponse
import com.nfq.data.remote.model.response.EventActivityDetailsResponse
import com.nfq.data.remote.model.response.EventActivityResponse
import com.nfq.data.remote.model.response.ProfileResponse
import com.nfq.data.remote.model.response.VoucherResponse
import com.nfq.data.remote.service.NFQSummitService
import javax.inject.Inject

class NFQSummitDataSourceImpl @Inject constructor(
    private val service: NFQSummitService
) : NFQSummitDataSource {
    override suspend fun authenticateWithAttendeeCode(attendeeCode: String): Either<DataException, AttendeeResponse> {
        return handleCall(
            apiCall = {
                service.authenticateWithAttendeeCode(AttendeeRequest(attendeeCode))
            },
            mapper = { data, _, tk ->
                data.copy(tk = tk)
            }
        )
    }

    override suspend fun logout(): Either<DataException, String> {
        return handleCall(
            apiCall = {
                service.logout()
            },
            mapper = { _, message ->
                message.orEmpty()
            },
            onDataNull = {
                Either.Right(it?.message ?: "Logout successful")
            }
        )
    }

    override suspend fun getProfile(): Either<DataException, ProfileResponse> {
        return handleCall(
            apiCall = {
                service.getProfile()
            },
            mapper = { data, _ ->
                data
            }
        )
    }

    override suspend fun getEventActivities(registrantId: String?): Either<DataException, List<EventActivityResponse>> {
        return handleCall(
            apiCall = {
                service.getEventActivities(registrantId)
            },
            mapper = { data, _ ->
                data
            }
        )
    }

    override suspend fun getEventActivityByID(id: String): Either<DataException, EventActivityDetailsResponse> {
        return handleCall(
            apiCall = {
                service.getEventActivityByID(id)
            },
            mapper = { data, _ ->
                data
            }
        )
    }

    override suspend fun getMealVouchers(): Either<DataException, List<VoucherResponse>> {
        return handleCall(
            apiCall = {
                service.getMealVouchers()
            },
            mapper = { data, _ ->
                data
            }
        )
    }
}