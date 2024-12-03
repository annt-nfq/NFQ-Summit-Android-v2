package com.nfq.data.remote.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    @SerialName("id") val id: Int,
    @SerialName("role_id") val roleId: Int,
    @SerialName("name") val name: String,
    @SerialName("email") val email: String,
    @SerialName("avatar") val avatar: String,
    @SerialName("email_verified_at") val emailVerifiedAt: String,
    @SerialName("settings") val settings: SettingResponse,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
)


@Serializable
data class SettingResponse(
    @SerialName("locale") val locale: String
)