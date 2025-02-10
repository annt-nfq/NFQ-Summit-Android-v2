package com.nfq.data.remote.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventActivityDetailsResponse(
    @SerialName("id") val id: Int,
    @SerialName("code") val code: String,
    @SerialName("title") val title: String,
    @SerialName("name") val name: String,
    @SerialName("quantity") val quantity: Int,
    @SerialName("isMain") val isMain: Boolean,
    @SerialName("timeStart") val timeStart: String,
    @SerialName("timeEnd") val timeEnd: String,
    @SerialName("isTimeLate") val isTimeLate: Boolean,
    @SerialName("description") val description: String,
    @SerialName("location") val location: String,
    @SerialName("latitude") val latitude: Double?,
    @SerialName("longitude") val longitude: Double?,
    @SerialName("gatherTime") val gatherTime: String,
    @SerialName("gatherLocation") val gatherLocation: String,
    @SerialName("leavingTime") val leavingTime: String,
    @SerialName("leavingLocation") val leavingLocation: String,
    @SerialName("adminNotes") val adminNotes: String,
    @SerialName("images") val images: List<String>,
    @SerialName("order") val order: String,
    @SerialName("category") val category: String,
    @SerialName("eventDay") val eventDay: EventDayResponse,
    @SerialName("qrCodeUrl") val qrCodeUrl: String,
    @SerialName("isRegistered") val isRegistered: Boolean,
    @SerialName("attendees") val attendees: List<AttendeeEventResponse>
)

@Serializable
data class AttendeeEventResponse(
    @SerialName ("id")val id: Int,
    @SerialName("firstName") val firstName: String,
    @SerialName("lastName") val lastName: String,
    @SerialName("email") val email: String,
    @SerialName("checkIn") val checkIn: Boolean,
)
