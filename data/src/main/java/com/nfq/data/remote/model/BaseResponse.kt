package com.nfq.data.remote.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
class BaseResponse<T>(
    @SerialName("message")
    val message: String?,

    @SerialName("success")
    val success: Boolean?,

    @SerialName("data")
    val data: T?
)