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
    var sidebarWidth by remember { mutableIntStateOf(120) }
    val headerHeight by remember { mutableIntStateOf(0) }
    BoxWithConstraints(modifier = modifier) {

        val dayWidth: Dp = with(LocalDensity.current) {
            (constraints.maxWidth - sidebarWidth).toDp()
        }
        val fullWidth: Dp = with(LocalDensity.current) {
            constraints.maxWidth.toDp()
        }

        val hourHeight: Dp = when (hourSize) {
            is ScheduleSize.FixedSize -> hourSize.size
            is ScheduleSize.FixedCount -> with(LocalDensity.current) { ((constraints.maxHeight - headerHeight) / hourSize.count).toDp() }
            is ScheduleSize.Adaptive -> with(LocalDensity.current) {
                maxOf(
                    ((constraints.maxHeight - headerHeight) / numHours).toDp(),
                    hourSize.minSize
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            ScheduleSidebar(
                hourHeight = hourHeight,
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
                hourHeight = hourHeight,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .align(Alignment.TopEnd)
            )
            CurrentTimeIndicator(
                currentTime = currentTime,
                minDate = minDate,
                maxDate = maxDate,
                minTime = minTime,
                maxTime = maxTime,
                dayWidth = fullWidth,
                hourHeight = hourHeight,
                modifier = Modifier
            )
        }
    }
}

sealed class ScheduleSize {
    class FixedSize(val size: Dp) : ScheduleSize()
    class FixedCount(val count: Float) : ScheduleSize() {
        constructor(count: Int) : this(count.toFloat())
    }

    class Adaptive(val minSize: Dp) : ScheduleSize()
}