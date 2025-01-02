package com.nfq.nfqsummit.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview
import java.time.LocalTime
import java.time.temporal.ChronoUnit


@Composable
fun ScheduleSidebar(
    modifier: Modifier = Modifier,
    minTime: LocalTime = LocalTime.MIN,
    maxTime: LocalTime = LocalTime.MAX,
    hourHeights: List<HourHeight> = emptyList(),
    label: @Composable (time: LocalTime) -> Unit = { BasicSidebarLabel(time = it) },
) {
    val firstHour = minTime.truncatedTo(ChronoUnit.HOURS)
    val firstHourOffsetMinutes =
        if (firstHour == minTime) 0 else ChronoUnit.MINUTES.between(minTime, firstHour.plusHours(1))
    val firstHourOffset = 130.dp * (firstHourOffsetMinutes / 60f)
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        Spacer(modifier = Modifier.height(firstHourOffset))
        hourHeights.forEach {
            Box(modifier = Modifier.height(it.height.dp)) {
                label(it.startTime)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleSidebarPreview() {
    NFQSnapshotTestThemeForPreview {
        ScheduleSidebar()
    }
}