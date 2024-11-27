package com.nfq.nfqsummit.screens.dashboard.tabs.schedule

import android.app.Activity
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nfq.data.domain.model.SummitEvent
import com.nfq.nfqsummit.components.BasicEvent
import com.nfq.nfqsummit.components.Schedule
import com.nfq.nfqsummit.mocks.mockEventDay1
import com.nfq.nfqsummit.mocks.mockEventDay2H1
import com.nfq.nfqsummit.mocks.mockEventDay2H2
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ScheduleTab(
    viewModel: ScheduleViewModel = hiltViewModel(),
    goToEventDetails: (eventId: String) -> Unit
) {
    val window = (LocalView.current.context as Activity).window
    window.statusBarColor = Color.Transparent.toArgb()

    ScheduleTabUI(
        dayEventPair = viewModel.dayEventPair,
        currentTime = viewModel.currentTime,
        selectedDate = viewModel.selectedDate,
        onDayClick = {
            viewModel.selectedDate = it
        },
        onEventClick = {
            goToEventDetails(it.id)
        },
    )
}

@Composable
fun ScheduleTabUI(
    dayEventPair: List<Pair<LocalDate, List<SummitEvent>>>,
    currentTime: LocalTime,
    selectedDate: LocalDate,
    onDayClick: (LocalDate) -> Unit,
    onEventClick: (SummitEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = "Calendar & Events",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {

                        }
                ) {
                    Text(
                        text = "Today",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(13.dp)
                    )
                }
            }

            SummitSchedule(
                dayEventPair = dayEventPair,
                currentTime = currentTime,
                selectedDate = selectedDate,
                onDayClick = {
                    onDayClick(it)
                },
                onEventClick = { onEventClick(it) },
                modifier = modifier
            )
        }
    }
}

@Composable
fun SummitSchedule(
    dayEventPair: List<Pair<LocalDate, List<SummitEvent>>>,
    currentTime: LocalTime,
    selectedDate: LocalDate,
    onDayClick: (LocalDate) -> Unit,
    onEventClick: (SummitEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val verticalScroll = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .horizontalScroll(state = rememberScrollState())
                .animateContentSize()
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            dayEventPair.forEach {
                ScheduleDays(
                    date = it.first.dayOfMonth.toString(),
                    dayOfWeek = it.first.dayOfWeek.getDisplayName(
                        TextStyle.SHORT,
                        Locale.getDefault()
                    ),
                    eventCount = it.second.size,
                    selected = selectedDate.dayOfMonth == it.first.dayOfMonth,
                    onClick = {
                        onDayClick(selectedDate.withDayOfMonth(it.first.dayOfMonth))

                        coroutineScope.launch {
                            verticalScroll.animateScrollTo(0)
                        }
                    }
                )
                Spacer(modifier = Modifier.width(21.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(modifier = Modifier.verticalScroll(state = verticalScroll)) {
            val dailyEvents = dayEventPair.filter {
                it.first.dayOfMonth == selectedDate.dayOfMonth
            }.flatMap { it.second }
            if (dailyEvents.isNotEmpty())
                Surface(color = MaterialTheme.colorScheme.background) {
                    Schedule(
                        events = dailyEvents.sortedBy { it.name },
                        currentTime = currentTime,
                        minTime = dailyEvents.minByOrNull { it.start }!!.start.toLocalTime()
                            .minusHours(1),
                        eventContent = {
                            BasicEvent(
                                positionedEvent = it,
                                modifier = Modifier
                                    .clickable {
                                        onEventClick(it.event)
                                    }
                            )
                        }
                    )
                }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun ScheduleDays(
    date: String,
    dayOfWeek: String,
    eventCount: Int,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val width = animateFloatAsState(if (selected) 76f else 55f, label = "")
    val height = animateFloatAsState(if (selected) 62f else 45f, label = "")


    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .width(width.value.dp)
                .height(height.value.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp,
                pressedElevation = 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            Column(
                modifier = Modifier
                    .clickable { onClick() }
                    .clipToBounds()
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (selected) 25.dp else 18.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(
                                alpha = if (selected) 1f else 0.2f
                            )
                        )
                ) {
                    Text(
                        text = dayOfWeek,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (selected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text(
                        text = date,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = if (selected) 18.sp else 16.sp,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = if (selected) 3.dp else 5.dp)
        ) {
            if (eventCount <= 3)
                repeat(eventCount) {
                    EventCountIndicator()
                }
            else {
                repeat(3) {
                    EventCountIndicator()
                }
                Text(
                    text = "+${eventCount - 3}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }

}

@Composable
fun EventCountIndicator() {
    Box(
        modifier = Modifier
            .padding(1.dp)
            .size(4.dp)
            .background(MaterialTheme.colorScheme.primary, CircleShape)
    )
}

@Preview
@Composable
fun ScheduleDaysSelectedPreview() {
    NFQSnapshotTestThemeForPreview {
        ScheduleDays(
            selected = true,
            date = "1",
            dayOfWeek = "Mon",
            eventCount = 3
        )
    }
}

@Preview
@Composable
fun ScheduleDaysUnselectedPreview() {
    NFQSnapshotTestThemeForPreview {
        ScheduleDays(
            date = "1",
            dayOfWeek = "Mon",
            eventCount = 5
        )
    }
}

@Preview
@Composable
fun ScheduleTabUIPreview() {
    NFQSnapshotTestThemeForPreview {
        ScheduleTabUI(
            dayEventPair = listOf(
                LocalDate.of(2024, 1, 1) to listOf(
                    mockEventDay1
                ),
                LocalDate.of(2024, 1, 2) to listOf(
                    mockEventDay2H1,
                    mockEventDay2H2,
                ),
                LocalDate.of(2024, 1, 3) to listOf(
                    mockEventDay2H1,
                    mockEventDay2H2,
                ),
                LocalDate.of(2024, 1, 4) to listOf(
                    mockEventDay2H1,
                    mockEventDay2H2,
                ),
                LocalDate.of(2024, 1, 5) to listOf(
                    mockEventDay2H1,
                    mockEventDay2H2,
                )
            ),
            currentTime = LocalTime.of(11, 0),
            selectedDate = LocalDate.of(2024, 1, 1),
            onDayClick = {},
            onEventClick = {}
        )
    }
}

@Preview
@Composable
fun ScheduleTabUIDarkPreview() {
    NFQSnapshotTestThemeForPreview(darkTheme = true) {
        ScheduleTabUI(
            dayEventPair = listOf(
                LocalDate.of(2024, 1, 1) to listOf(
                    mockEventDay1
                ),
                LocalDate.of(2024, 1, 2) to listOf(
                    mockEventDay2H1,
                    mockEventDay2H2,
                )
            ),
            currentTime = LocalTime.of(10, 30),
            selectedDate = LocalDate.of(2024, 1, 2),
            onDayClick = {},
            onEventClick = {}
        )
    }
}