package com.nfq.nfqsummit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
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
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
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
            ChronoUnit.MINUTES.between(start, other.start) < 30
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
    modifier: Modifier,
    hourlySegments: List<HourlySegment> = emptyList(),
) {
    Layout(
        content = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(15.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(15.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .align(Alignment.CenterStart)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(MaterialTheme.colorScheme.primary)
                        .align(Alignment.Center)
                )
            }
        },
        modifier = modifier.padding(start = 40.dp)
    ) { measureables, constraints ->

        val initialYOffset = if (currentTime.hour <= minTime.hour) 2.dp else
            hourlySegments.first().height.dp * (minTime.minute / 60f)

        val layoutHeight = hourlySegments.sumOf { it.height }.dp.roundToPx()
        val layoutWidth = dayWidth.roundToPx()
        layout(layoutWidth, layoutHeight) {
            val measuredPlaceables = measureables.map { measurable ->
                measurable.measure(
                    constraints.copy(
                        minWidth = dayWidth.toPx().roundToInt(),
                        maxWidth = dayWidth.toPx().roundToInt(),
                        minHeight = 48,
                        maxHeight = 48,
                    )
                )
            }
            val pastHourSegments =
                hourlySegments.filter { it.startTime.hour < currentTime.hour }

            val currentOffsetMinutes =
                pastHourSegments.eventOffsetMinutes(currentTime, minTime)

            val additionalYOffset = hourlySegments
                .find { it.startTime.hour == currentTime.hour }
                ?.height?.dp
                ?.let { (currentOffsetMinutes / 60f) * it.toPx() }
                ?.roundToInt() ?: 0

            val currentYPosition = pastHourSegments
                .sumOf { it.height }.dp.toPx()
                .roundToInt() + additionalYOffset + initialYOffset.roundToPx()

            measuredPlaceables.first().place(0, currentYPosition)
        }
    }
}

@Preview
@Composable
fun BasicSchedulePreview() {
    NFQSnapshotTestThemeForPreview {
        BasicSchedule(
            events = persistentListOf(
                SummitEvent(
                    id = "1",
                    name = "Event 1",
                    start = LocalDate.now().atTime(9, 0),
                    end = LocalDate.now().atTime(10, 30),
                ),
                SummitEvent(
                    id = "2",
                    name = "Event 2",
                    start = LocalDate.now().atTime(10, 0),
                    end = LocalDate.now().atTime(11, 0),
                ),
                SummitEvent(
                    id = "3",
                    name = "Event 3",
                    start = LocalDate.now().atTime(10, 0),
                    end = LocalDate.now().atTime(12, 0),
                ),
            ), dayWidth = 300.dp
        )
    }
}

@Composable
fun BasicSchedule(
    events: PersistentList<SummitEvent>,
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
    hourlySegments: List<HourlySegment> = emptyList(),
) {
    val numDays = ChronoUnit.DAYS.between(minDate, minOf(minDate, maxDate)).toInt() + 1
    val numMinutes = ChronoUnit.MINUTES.between(minTime, maxTime).toInt() + 1
    val numHours = numMinutes / 60
    val dividerColor = Color.LightGray
    val positionedEvents = remember(events) {
        arrangeEvents(splitEvents(events.sortedBy(SummitEvent::start))).filter { it.end > minTime && it.start < maxTime }
    }
    Layout(
        content = {
            positionedEvents.forEach { positionedEvent ->
                Box(
                    modifier = Modifier.eventData(positionedEvent)
                ) {
                    eventContent(positionedEvent)
                }
            }
        },
        modifier = modifier
            .drawBehind {
                val startTime = hourlySegments.first().startTime.truncatedTo(ChronoUnit.HOURS)
                val firstHourOffsetMinutes = if (startTime == minTime) 0 else startTime.minute
                val firstHourOffsetPx = (firstHourOffsetMinutes / 60f) * 130.dp.toPx()
                var accumulatedHeight = 0.dp
                repeat(numHours) {
                    val hourHeight =
                        hourlySegments.find { hourHeight -> hourHeight.hour == it }!!.height
                    drawLine(
                        color = dividerColor,
                        start = Offset(0f, accumulatedHeight.toPx() + firstHourOffsetPx),
                        end = Offset(size.width, accumulatedHeight.toPx() + firstHourOffsetPx),
                        strokeWidth = 1.dp.toPx()
                    )
                    accumulatedHeight += hourHeight.dp
                }
            }
    ) { measureables, constraints ->
        val layoutHeight = hourlySegments.sumOf { it.height }.dp.toPx().roundToInt()
        val layoutWidth = dayWidth.roundToPx() * numDays
        val placeablesWithEvents = measureables.map { measurable ->

            val splitEvent = measurable.parentData as PositionedEvent

            val eventHeight =
                calculateEventHeight(hourlySegments, splitEvent, maxTime).dp.toPx().roundToInt()

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
        layout(layoutWidth, layoutHeight) {
            val firstEvent = placeablesWithEvents.map { it.second }.first()
            val initialYOffset =
                hourlySegments.first().height.dp * (firstEvent.start.minute / 60f)

            placeablesWithEvents.forEach { (placeable, positionedEvent) ->
                val pastHourSegments =
                    hourlySegments.filter { it.startTime.hour < positionedEvent.start.hour }

                val eventOffsetMinutes =
                    pastHourSegments.eventOffsetMinutes(positionedEvent.start, minTime)

                val additionalEventYOffset = hourlySegments
                    .find { it.startTime.hour == positionedEvent.start.hour }
                    ?.height?.dp
                    ?.let { (eventOffsetMinutes / 60f) * it.toPx() }
                    ?.roundToInt() ?: 0

                val eventYPosition = pastHourSegments
                    .sumOf { it.height }.dp.toPx()
                    .roundToInt() + additionalEventYOffset + initialYOffset.roundToPx()

                val eventDayOffset = ChronoUnit.DAYS.between(minDate, positionedEvent.date).toInt()
                val eventXPosition =
                    (eventDayOffset * dayWidth.toPx() + positionedEvent.col * (dayWidth.toPx() / positionedEvent.colTotal)).roundToInt()

                placeable.place(eventXPosition, eventYPosition)
            }
        }
    }
}

private fun List<HourlySegment>.eventOffsetMinutes(
    currentTime: LocalTime,
    minTime: LocalTime = LocalTime.MIN
): Long {
    return this
        .lastOrNull()
        ?.let { ChronoUnit.MINUTES.between(it.endTime, currentTime) }
        ?: ChronoUnit.MINUTES.between(minTime, currentTime).coerceAtLeast(0)

}


/**
 * Calculates the height of an event based on its duration and the heights of the hours it spans.
 *
 * @param hourlySegments A list of `HourHeight` objects representing the heights of each hour in the schedule.
 * @param positionedEvent The `PositionedEvent` for which the height is being calculated.
 * @param maxTime The maximum time considered for the event height calculation. Defaults to `LocalTime.MAX`.
 * @return The calculated height of the event in pixels.
 */
private fun calculateEventHeight(
    hourlySegments: List<HourlySegment>,
    positionedEvent: PositionedEvent,
    maxTime: LocalTime = LocalTime.MAX
): Int {
    val eventDurationMinutes = ChronoUnit.MINUTES.between(
        positionedEvent.start,
        minOf(positionedEvent.end, maxTime)
    )

    val eventDurationHours = eventDurationMinutes / 60
    var remainingMinutes = eventDurationMinutes % 60

    val height = (0 until eventDurationHours.toInt()).sumOf { hour ->
        hourlySegments
            .find { it.startTime.hour == positionedEvent.start.plusHours(hour.toLong()).hour }!!
            .let {
                if (positionedEvent.start.minute > it.startTime.minute) {
                    val minutes = positionedEvent.start.minute - it.startTime.minute
                    remainingMinutes += minutes
                    ((minutes / 60f) * it.height).roundToInt()
                } else {
                    it.height
                }
            }
    }

    val additionalHeight = hourlySegments
        .find { it.startTime.hour == positionedEvent.start.plusHours(eventDurationHours).hour }
        ?.let { it.height * (remainingMinutes / 60f) }
        ?.roundToInt() ?: 0

    return height + additionalHeight
}