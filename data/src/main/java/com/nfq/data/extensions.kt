package com.nfq.data

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

const val SERVER_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"

fun String?.changeFormat(
    targetPattern: String,
    isUTC: Boolean = true,
    default: String = "-"
): String {
    if (this.isNullOrBlank()) return default
    return try {
        val fromP = DateTimeFormatter.ofPattern(SERVER_FORMAT)
        val toP = DateTimeFormatter.ofPattern(targetPattern)
        val fromLocalDate = LocalDateTime.parse(this, fromP)
        val formattedDate = if (isUTC) {
            fromLocalDate.atZone(ZoneOffset.UTC)
                .withZoneSameInstant(ZoneId.systemDefault())
                .format(toP)
        } else {
            fromLocalDate.format(toP)
        }
        formattedDate
    } catch (e: Exception) {
        default
    }
}

fun String.toLocalDateTime(
    isUTC: Boolean = true,
): LocalDateTime {
    return try {
        val fromP = DateTimeFormatter.ofPattern(SERVER_FORMAT)
        val fromLocalDate = LocalDateTime.parse(this, fromP)
        if (isUTC) {
            fromLocalDate.atZone(ZoneOffset.UTC)
                .withZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime()
        } else {
            fromLocalDate
        }
    } catch (e: Exception) {
        LocalDateTime.now()
    }
}

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
        val localDate = this.toLocalDate()
        localDate.format(DateTimeFormatter.ofPattern(targetPattern))
    } catch (e: Exception) {
        default
    }
}

fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
}

fun Long.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
}