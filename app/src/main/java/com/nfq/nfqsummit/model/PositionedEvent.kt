package com.nfq.nfqsummit.model

import com.nfq.data.domain.model.SummitEvent
import java.time.LocalDate
import java.time.LocalTime

data class PositionedEvent(
    val event: SummitEvent,
    val splitType: SplitType,
    val date: LocalDate,
    val start: LocalTime,
    val end: LocalTime,
    val col: Int = 0,
    val colSpan: Int = 1,
    val colTotal: Int = 1,
)

@JvmInline
value class SplitType private constructor(val value: Int) {
    companion object {
        val None = SplitType(0)
        val Start = SplitType(1)
        val End = SplitType(2)
        val Both = SplitType(3)
    }
}