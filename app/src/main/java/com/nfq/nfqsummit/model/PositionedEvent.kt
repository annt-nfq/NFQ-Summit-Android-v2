package com.nfq.nfqsummit.model

import com.nfq.data.domain.model.SummitEvent
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

data class PositionedEvent(
    val event: SummitEvent,
    val splitType: SplitType,
    val date: LocalDate,
    val start: LocalTime,
    val end: LocalTime,
    val col: Int = 0,
    val colSpan: Int = 1,
    val colTotal: Int = 1
) {
    fun getEventSize(): EventSize {
        val durationMinutes = ChronoUnit.MINUTES.between(start, end)
        return when {
            durationMinutes < 30 -> if (colTotal > 1) EventSize.XSmall else EventSize.Small
            durationMinutes < 60 -> if (colTotal > 1) EventSize.Small else EventSize.Medium
            else -> if (colTotal > 1) EventSize.Medium else EventSize.Large
        }
    }
}

@JvmInline
value class SplitType private constructor(val value: Int) {
    companion object {
        val None = SplitType(0)
        val Start = SplitType(1)
        val End = SplitType(2)
        val Both = SplitType(3)
    }
}

@JvmInline
value class EventSize private constructor(val value: Int) {
    companion object {
        val XSmall = EventSize(-1)
        val Small = EventSize(0)
        val Medium = EventSize(1)
        val Large = EventSize(2)
    }
}