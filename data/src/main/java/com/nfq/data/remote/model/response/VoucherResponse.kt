package com.nfq.data.remote.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class VoucherResponse(
    @SerialName("id") val id: Int,
    @SerialName("date") val date: String,
    @SerialName("type") val type: String,
    @SerialName("price") val price: Double,
    @SerialName("currency") val currency: String,
    @SerialName("locations") val locations: List<LocationResponse>,
    @SerialName("sponsorLogoUrls") val sponsorLogoUrls: List<String>,
    @SerialName("imageUrl") val imageUrl: String
)


@Serializable
data class LocationResponse(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("address") val address: String
)
