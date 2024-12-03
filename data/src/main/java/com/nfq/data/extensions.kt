package com.nfq.data

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