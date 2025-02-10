package com.nfq.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class BaseResponse<T>(
    @SerialName("message")
    val message: String?,

    @SerialName("success")
    val success: Boolean?,

    @SerialName("data")
    val data: T?
)

@Serializable
class BaseAttendeeResponse<T>(
    @SerialName("message")
    val message: String?,

    @SerialName("success")
    val success: Boolean?,

    @SerialName("token")
    val token: String?,

    @SerialName("data")
    val data: T?
)