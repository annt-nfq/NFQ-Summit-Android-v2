package com.nfq.data.remote.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttendeeResponse(
    @SerialName("id") val id: Int,
    @SerialName("firstName") val firstName: String?,
    @SerialName("lastName") val lastName: String?,
    @SerialName("email") val email: String?,
    @SerialName("qrCodeUrl") val qrCodeUrl: String?,
    @SerialName("attendeeCode") val attendeeCode: String?,
    val tk: String?,
)
