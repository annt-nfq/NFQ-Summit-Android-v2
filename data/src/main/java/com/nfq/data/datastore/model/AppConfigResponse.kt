package com.nfq.data.datastore.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppConfigResponse(
    @SerialName("is_completed_onboarding") val isCompletedOnboarding: Boolean
)