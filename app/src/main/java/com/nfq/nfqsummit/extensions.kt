package com.nfq.nfqsummit

import android.content.Context
import android.content.Intent
import android.net.Uri

fun Context.openMapView(latitude: Double?, longitude: Double?, locationName: String) {
    val geoUri = "http://maps.google.com/maps?q=loc:" +
            "${latitude}," +
            "$longitude " +
            "(${locationName})"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
    startActivity(intent)
}

fun String.convertTimeFormat(): String {
    return if (this.endsWith("AM", true) || this.endsWith("PM", true)) {
        this.substring(0, this.length - 2) + this.substring(this.length - 2, this.length - 1) +
                this.substring(this.length - 1).lowercase()
    } else {
        this // Return as is if not AM/PM format
    }
}