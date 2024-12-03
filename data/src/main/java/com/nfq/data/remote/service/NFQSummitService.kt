package com.nfq.data.remote.service

import com.nfq.data.remote.model.BaseResponse
import com.nfq.data.remote.model.request.AttendeeRequest
import com.nfq.data.remote.model.response.AttendeeResponse
import com.nfq.data.remote.model.response.EventActivityDetailsResponse
import com.nfq.data.remote.model.response.EventActivityResponse
import com.nfq.data.remote.model.response.ProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NFQSummitService {
    @POST("attendees/auth")
    suspend fun authenticateWithAttendeeCode(
        @Body request: AttendeeRequest
    ): Response<BaseResponse<AttendeeResponse>>


    @GET("auth/profile")
    suspend fun getProfile(): Response<BaseResponse<ProfileResponse>>

    @GET("event-activities?detailed=1")
    suspend fun getEventActivities(): Response<BaseResponse<List<EventActivityResponse>>>

    @GET("event-activities/{id}")
    suspend fun getEventActivityByID(
        @Path("id") id: String,
    ): Response<BaseResponse<EventActivityDetailsResponse>>
}