package com.nfq.nfqsummit.model

import android.graphics.Bitmap

data class UserUIModel(
    val id: String,
    val name: String,
    val email: String,
    val qrCodeUrl: String,
    val qrCodeBitmap: Bitmap?,
    val attendeeCode: String
)