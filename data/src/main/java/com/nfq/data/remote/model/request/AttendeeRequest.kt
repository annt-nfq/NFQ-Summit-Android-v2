package com.nfq.data.remote.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttendeeRequest(
    @SerialName("attendeeCode") val attendeeCode: String,
)
