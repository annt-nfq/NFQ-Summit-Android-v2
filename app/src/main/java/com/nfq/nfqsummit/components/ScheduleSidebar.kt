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
    hourlySegments: List<HourlySegment> = emptyList(),
    label: @Composable (time: LocalTime) -> Unit = { BasicSidebarLabel(time = it) },
) {
    val startTime = hourlySegments.first().startTime.truncatedTo(ChronoUnit.HOURS)
    val firstHourOffsetInMinutes = if (startTime == minTime) 0 else startTime.minute
    val firstHourOffsetDp = 130.dp * (firstHourOffsetInMinutes / 60f)
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        Spacer(modifier = Modifier.height(firstHourOffsetDp))
        hourlySegments.forEach {
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