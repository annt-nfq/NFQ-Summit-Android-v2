package com.nfq.nfqsummit.model

data class UserUIModel(
    val id: String,
    val name: String,
    val email: String,
    val qrCodeUrl: String,
    val attendeeCode: String
)