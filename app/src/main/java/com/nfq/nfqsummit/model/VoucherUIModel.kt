package com.nfq.nfqsummit.model

import android.graphics.Bitmap

data class VoucherUIModel(
    val type: String,
    val date: String,
    val locations: List<LocationUIModel>,
    val price: String,
    val imageUrl: String,
    val imageBitmap : Bitmap? = null,
    val sponsorLogoUrls: List<String>
)

data class LocationUIModel(
    val id: Int,
    val name: String,
    val address: String
)