package com.nfq.nfqsummit.model

import android.graphics.Bitmap

data class VoucherUIModel(
    val type: String,
    val date: String,
    val location: String,
    val price: String,
    val imageUrl: String,
    val imageBitmap : Bitmap? = null,
    val sponsorLogoUrls: List<String>
)
