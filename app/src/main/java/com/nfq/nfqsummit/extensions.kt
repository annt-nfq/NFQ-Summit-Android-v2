package com.nfq.nfqsummit

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.time.LocalDate

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
                this.substring(this.length - 1)
    } else {
        this // Return as is if not AM/PM format
    }
}

fun LocalDate.isSame(date: LocalDate?): Boolean {
    return date != null && this.year == date.year && this.month == date.month && this.dayOfMonth == date.dayOfMonth
}

fun Context.openWhatsapp(phoneNumber: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse("https://wa.me/$phoneNumber")
    startActivity(intent)
}

fun Context.openWhatsAppGroupInvite(inviteLink: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(inviteLink)
            `package` = "com.whatsapp"  // Specify WhatsApp package
        }
        startActivity(intent)
    } catch (e: Exception) {
        // WhatsApp is not installed, open in browser instead
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(inviteLink))
        startActivity(browserIntent)
    }
}