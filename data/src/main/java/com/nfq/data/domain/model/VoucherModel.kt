package com.nfq.data.domain.model

data class VoucherModel(
    val type: String,
    val date: String,
    val location: String,
    val price: String,
    val imageUrl: String,
    val sponsorLogoUrls: List<String>
)
