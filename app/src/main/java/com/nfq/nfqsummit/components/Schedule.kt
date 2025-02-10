package com.nfq.nfqsummit.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nfq.data.domain.model.SummitEvent
import com.nfq.nfqsummit.model.PositionedEvent
import kotlinx.collections.immutable.PersistentList
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

@Composable
fun Schedule(
    events: PersistentList<SummitEvent>,
    currentTime: LocalTime,
    modifier: Modifier = Modifier,
    eventContent: @Composable (positionedEvent: PositionedEvent) -> Unit,
    timeLabel: @Composable (time: LocalTime) -> Unit = { BasicSidebarLabel(time = it) },
    minDate: LocalDate = events.minByOrNull(SummitEvent::start)?.start?.toLocalDate()
        ?: LocalDate.now(),
    maxDate: LocalDate = events.maxByOrNull(SummitEvent::end)?.end?.toLocalDate()
        ?: LocalDate.now(),
    minTime: LocalTime = LocalTime.MIN,
    maxTime: LocalTime = LocalTime.MAX,
    daySize: ScheduleSize = ScheduleSize.FixedSize(500.dp),
    hourSize: ScheduleSize = ScheduleSize.FixedSize(130.dp),
) {
    val numMinutes = ChronoUnit.MINUTES.between(minTime, maxTime).toInt() + 1
    val numHours = numMinutes.toFloat() / 60f
    val hourlySegments = calculateHourlySegments(events, minTime, numHours.toInt())
    var sidebarWidth by remember { mutableIntStateOf(120) }
    val showTimeIndicator = remember(minDate, maxDate) {
        val today = LocalDate.now()
        !today.isBefore(minDate) && !today.isAfter(maxDate)
    }

    BoxWithConstraints(modifier = modifier) {

        val dayWidth: Dp = with(LocalDensity.current) {
            (constraints.maxWidth - sidebarWidth).toDp()
        }
        val fullWidth: Dp = with(LocalDensity.current) {
            constraints.maxWidth.toDp()
        }

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            ScheduleSidebar(
                hourlySegments = hourlySegments,
                minTime = minTime,
                maxTime = maxTime,
                label = timeLabel,
                modifier = Modifier
                    .onGloballyPositioned { sidebarWidth = it.size.width }
                    .align(Alignment.TopStart)
            )
            BasicSchedule(
                events = events,
                eventContent = eventContent,
                minDate = minDate,
                maxDate = maxDate,
                minTime = minTime,
                maxTime = maxTime,
                dayWidth = dayWidth,
                hourlySegments = hourlySegments,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .align(Alignment.TopEnd)
            )
            if (showTimeIndicator) {
                CurrentTimeIndicator(
                    currentTime = currentTime,
                    minDate = minDate,
                    maxDate = maxDate,
                    minTime = minTime,
                    maxTime = maxTime,
                    dayWidth = fullWidth,
                    hourlySegments = hourlySegments,
                    modifier = Modifier
                )
            }
        }
    }
}

data class HourlySegment(
    val hour: Int,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val height: Int
)

private fun calculateHourlySegments(
    events: PersistentList<SummitEvent>,
    minTime: LocalTime,
    numHours: Int
): List<HourlySegment> {
    return (0..numHours).map { hour ->
        val startTime = minTime.truncatedTo(ChronoUnit.HOURS).plusHours(hour.toLong())
        val endTime = if (startTime.hour == 23) {
            startTime.plusMinutes(59)
        } else {
            startTime.plusHours(1)
        }

        val height = when {
            events.any { it.start.hour == startTime.hour && it.getDurationMinutes() < 30 } -> 390
            events.any { it.start.hour == startTime.hour && it.getDurationMinutes() < 60 } -> 260
            else -> 130
        }

        HourlySegment(hour, startTime, endTime, height)
    }
}

private fun SummitEvent.getDurationMinutes(): Long {
    val end = if (start.hour == 23) start.plusMinutes(59) else end
    return ChronoUnit.MINUTES.between(start, end)
}

sealed class ScheduleSize {
    class FixedSize(val size: Dp) : ScheduleSize()
    class FixedCount(val count: Float) : ScheduleSize() {
        constructor(count: Int) : this(count.toFloat())
    }

    class Adaptive(val minSize: Dp) : ScheduleSize()
}