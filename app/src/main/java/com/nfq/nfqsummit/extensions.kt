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