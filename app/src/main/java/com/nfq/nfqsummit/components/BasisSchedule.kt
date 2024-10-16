package com.nfq.nfqsummit.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nfq.data.domain.model.SummitEvent
import com.nfq.nfqsummit.model.PositionedEvent
import com.nfq.nfqsummit.model.SplitType
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

private class EventDataModifier(
    val positionedEvent: PositionedEvent,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = positionedEvent
}


private fun Modifier.eventData(positionedEvent: PositionedEvent) =
    this.then(EventDataModifier(positionedEvent))

private fun splitEvents(events: List<SummitEvent>): List<PositionedEvent> {
    return events
        .map { event ->
            val startDate = event.start.toLocalDate()
            val endDate = event.end.toLocalDate()
            if (startDate == endDate) {
                listOf(
                    PositionedEvent(
                        event,
                        SplitType.None,
                        event.start.toLocalDate(),
                        event.start.toLocalTime(),
                        event.end.toLocalTime()
                    )
                )
            } else {
                val days = ChronoUnit.DAYS.between(startDate, endDate)
                val splitEvents = mutableListOf<PositionedEvent>()
                for (i in 0..days) {
                    val date = startDate.plusDays(i)
                    splitEvents += PositionedEvent(
                        event,
                        splitType = if (date == startDate) SplitType.End else if (date == endDate) SplitType.Start else SplitType.Both,
                        date = date,
                        start = if (date == startDate) event.start.toLocalTime() else LocalTime.MIN,
                        end = if (date == endDate) event.end.toLocalTime() else LocalTime.MAX,
                    )
                }
                splitEvents
            }
        }
        .flatten()
}

private fun PositionedEvent.overlapsWith(other: PositionedEvent): Boolean {
    return date == other.date &&
            start < other.end &&
            end > other.start &&
            ChronoUnit.MINUTES.between(start, other.start) < 70
}

private fun List<PositionedEvent>.timesOverlapWith(event: PositionedEvent): Boolean {
    return any { it.overlapsWith(event) }
}

private fun arrangeEvents(events: List<PositionedEvent>): List<PositionedEvent> {
    val positionedEvents = mutableListOf<PositionedEvent>()
    val groupEvents: MutableList<MutableList<PositionedEvent>> = mutableListOf()

    fun resetGroup() {
        groupEvents.forEachIndexed { colIndex, col ->
            col.forEach { e ->
                positionedEvents.add(e.copy(col = colIndex, colTotal = groupEvents.size))
            }
        }
        groupEvents.clear()
    }

    events.forEach { event ->
        var firstFreeCol = -1
        var numFreeCol = 0
        for (i in 0 until groupEvents.size) {
            val col = groupEvents[i]
            if (col.timesOverlapWith(event)) {
                if (firstFreeCol < 0) continue else break
            }
            if (firstFreeCol < 0) firstFreeCol = i
            numFreeCol++
        }

        when {
            // Overlaps with all, add a new column
            firstFreeCol < 0 -> {
                groupEvents += mutableListOf(event)
                // Expand anything that spans into the previous column and doesn't overlap with this event
                for (ci in 0 until groupEvents.size - 1) {
                    val col = groupEvents[ci]
                    col.forEachIndexed { ei, e ->
                        if (ci + e.colSpan == groupEvents.size - 1 && !e.overlapsWith(event)) {
                            col[ei] = e.copy(colSpan = e.colSpan + 1)
                        }
                    }
                }
            }
            // No overlap with any, start a new group
            numFreeCol == groupEvents.size -> {
                resetGroup()
                groupEvents += mutableListOf(event)
            }
            // At least one column free, add to first free column and expand to as many as possible
            else -> {
                groupEvents[firstFreeCol] += event.copy(colSpan = numFreeCol)
            }
        }
    }
    resetGroup()
    return positionedEvents
}

@Composable
fun CurrentTimeIndicator(
    currentTime: LocalTime,
    minDate: LocalDate = LocalDate.now(),
    maxDate: LocalDate = LocalDate.now(),
    minTime: LocalTime = LocalTime.MIN.plusHours(5),
    maxTime: LocalTime = LocalTime.MAX.minusHours(5),
    dayWidth: Dp,
    hourHeight: Dp,
    modifier: Modifier,
) {
    val numDays = ChronoUnit.DAYS.between(minDate, maxDate).toInt() + 1
    val numMinutes = ChronoUnit.MINUTES.between(minTime, maxTime).toInt() + 1
    Layout(
        content = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                        .align(Alignment.CenterStart)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.Red)
                        .align(Alignment.Center)
                )
            }
        },
        modifier = modifier
    ) { measureables, constraints ->
        val height = (hourHeight.toPx() * (numMinutes / 60f)).roundToInt()
        val width = dayWidth.roundToPx() * numDays
        layout(width, height) {
            val placeables = measureables.map { measurable ->
                measurable.measure(
                    constraints.copy(
                        minWidth = dayWidth.toPx().roundToInt(),
                        maxWidth = dayWidth.toPx().roundToInt(),
                        minHeight = 48,
                        maxHeight = 48,
                    )
                )
            }
            val eventOffsetMinutes =
                if (currentTime > minTime) ChronoUnit.MINUTES.between(
                    minTime,
                    currentTime
                ) else 0
            val eventY = ((eventOffsetMinutes / 60f) * hourHeight.toPx()).roundToInt()
            placeables.first().place(0, eventY)
        }

    }
}

@Preview
@Composable
fun BasicSchedulePreview() {
    NFQSnapshotTestThemeForPreview {
        BasicSchedule(
            events = listOf(
                SummitEvent(
                    "1",
                    "Event 1",
                    LocalDate.now().atTime(9, 0),
                    LocalDate.now().atTime(10, 0)
                ),
                SummitEvent(
                    "2",
                    "Event 2",
                    LocalDate.now().atTime(10, 0),
                    LocalDate.now().atTime(11, 0)
                ),
                SummitEvent(
                    "3",
                    "Event 3",
                    LocalDate.now().atTime(10, 0),
                    LocalDate.now().atTime(12, 0)
                ),
            ), dayWidth = 300.dp, hourHeight = 100.dp
        )
    }
}

@Composable
fun BasicSchedule(
    events: List<SummitEvent>,
    modifier: Modifier = Modifier,
    eventContent: @Composable (positionedEvent: PositionedEvent) -> Unit = {
        BasicEvent(
            positionedEvent = it
        )
    },
    minDate: LocalDate = events.minByOrNull(SummitEvent::start)?.start?.toLocalDate()
        ?: LocalDate.now(),
    maxDate: LocalDate = events.maxByOrNull(SummitEvent::end)?.end?.toLocalDate()
        ?: LocalDate.now(),
    minTime: LocalTime = LocalTime.MIN.plusHours(5),
    maxTime: LocalTime = LocalTime.MAX.minusHours(5),
    dayWidth: Dp,
    hourHeight: Dp,
) {
    val numDays = ChronoUnit.DAYS.between(minDate, maxDate).toInt() + 1
    val numMinutes = ChronoUnit.MINUTES.between(minTime, maxTime).toInt() + 1
    val numHours = numMinutes / 60
    val dividerColor = Color.LightGray
    val positionedEvents =
        remember(events) { arrangeEvents(splitEvents(events.sortedBy(SummitEvent::start))).filter { it.end > minTime && it.start < maxTime } }
    Layout(
        content = {
            positionedEvents.forEach { positionedEvent ->
                Box(modifier = Modifier.eventData(positionedEvent)) {
                    eventContent(positionedEvent)
                }
            }
        },
        modifier = modifier
            .drawBehind {
                val firstHour = minTime.truncatedTo(ChronoUnit.HOURS)
                val firstHourOffsetMinutes =
                    if (firstHour == minTime) 0 else ChronoUnit.MINUTES.between(
                        minTime,
                        firstHour.plusHours(1)
                    )
                val firstHourOffset = (firstHourOffsetMinutes / 60f) * hourHeight.toPx()
                repeat(numHours) {
                    drawLine(
                        dividerColor,
                        start = Offset(0f, it * hourHeight.toPx() + firstHourOffset),
                        end = Offset(size.width, it * hourHeight.toPx() + firstHourOffset),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }
    ) { measureables, constraints ->
        val height = (hourHeight.toPx() * (numMinutes / 60f)).roundToInt()
        val width = dayWidth.roundToPx() * numDays
        val placeablesWithEvents = measureables.map { measurable ->

            val splitEvent = measurable.parentData as PositionedEvent
            val eventDurationMinutes =
                ChronoUnit.MINUTES.between(splitEvent.start, minOf(splitEvent.end, maxTime))
            val eventHeight = ((eventDurationMinutes / 60f) * hourHeight.toPx()).roundToInt()
            val eventWidth =
                ((splitEvent.colSpan.toFloat() / splitEvent.colTotal.toFloat()) * dayWidth.toPx()).roundToInt()
            val placeable = measurable.measure(
                constraints.copy(
                    minWidth = eventWidth,
                    maxWidth = eventWidth,
                    minHeight = eventHeight,
                    maxHeight = eventHeight
                )
            )
            Pair(placeable, splitEvent)
        }
        layout(width, height) {
            placeablesWithEvents.forEach { (placeable, splitEvent) ->
                val eventOffsetMinutes =
                    if (splitEvent.start > minTime) ChronoUnit.MINUTES.between(
                        minTime,
                        splitEvent.start
                    ) else 0
                val eventY = ((eventOffsetMinutes / 60f) * hourHeight.toPx()).roundToInt()
                val eventOffsetDays = ChronoUnit.DAYS.between(minDate, splitEvent.date).toInt()
                val eventX =
                    eventOffsetDays * dayWidth.roundToPx() + (splitEvent.col * (dayWidth.toPx() / splitEvent.colTotal.toFloat())).roundToInt()
                placeable.place(eventX, eventY)
            }
        }
    }
}