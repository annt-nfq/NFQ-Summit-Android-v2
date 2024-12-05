package com.nfq.data

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

const val SERVER_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"

fun String?.toLocalDateTimeInMillis(
    isUTC: Boolean = true
): Long {
    if (this.isNullOrBlank()) return 0L
    return try {
        val fromP = DateTimeFormatter.ofPattern(SERVER_FORMAT)
        val fromLocalDate = LocalDateTime.parse(this, fromP)
        val zonedDateTime = if (isUTC) {
            fromLocalDate.atZone(ZoneOffset.UTC)
        } else {
            fromLocalDate.atZone(ZoneId.systemDefault())
        }
        zonedDateTime.toInstant().toEpochMilli()
    } catch (e: Exception) {
        0L
    }
}

fun Long.toFormattedDateTimeString(
    targetPattern: String,
    default: String = "-"
): String {
    return try {
        val localDate = this.toLocalDateTime()
        localDate.format(DateTimeFormatter.ofPattern(targetPattern))
    } catch (e: Exception) {
        default
    }
}

fun Long.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
}