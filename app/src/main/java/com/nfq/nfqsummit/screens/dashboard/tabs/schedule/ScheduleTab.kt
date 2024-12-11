package com.nfq.nfqsummit.screens.dashboard.tabs.schedule

import android.app.Activity
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ScrollState
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nfq.data.domain.model.SummitEvent
import com.nfq.nfqsummit.components.BasicCard
import com.nfq.nfqsummit.components.BasicEvent
import com.nfq.nfqsummit.components.Schedule
import com.nfq.nfqsummit.components.bounceClick
import com.nfq.nfqsummit.isSame
import com.nfq.nfqsummit.mocks.mockEventDay1
import com.nfq.nfqsummit.mocks.mockEventDay2H1
import com.nfq.nfqsummit.mocks.mockEventDay2H2
import com.nfq.nfqsummit.screens.eventDetails.EventDetailsBottomSheet
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

    val uiState by viewModel.uiState.collectAsState()
    var showEventDetailsBottomSheet by remember { mutableStateOf(false) }
    var eventId by remember { mutableStateOf("") }

    if (showEventDetailsBottomSheet) {
        EventDetailsBottomSheet(
            eventId = eventId,
            onDismissRequest = { showEventDetailsBottomSheet = false }
        )
    }
    ScheduleTabUI(
        uiState = uiState,
        onDayClick = viewModel::onDateSelected,
        onEventClick = {
            eventId = it.id
            showEventDetailsBottomSheet = true
        },
    )
}

@Composable
fun ScheduleTabUI(
    uiState: ScheduleUIState,
    onDayClick: (LocalDate) -> Unit,
    onEventClick: (SummitEvent) -> Unit
) {

    val verticalScroll = rememberScrollState()

    Scaffold(
        topBar = {
            ScheduleHeader(
                dayEventPairs = uiState.dayEventPairs,
                selectedDate = uiState.selectedDate,
                verticalScroll = verticalScroll,
                onDayClick = onDayClick
            )
        }
    ) { innerPadding ->
        SummitSchedules(
            dailyEvents = uiState.dailyEvents,
            currentTime = uiState.currentTime,
            onEventClick = { onEventClick(it) },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun ScheduleHeader(
    dayEventPairs: List<Pair<LocalDate, List<SummitEvent>>>,
    selectedDate: LocalDate,
    verticalScroll: ScrollState = rememberScrollState(),
    onDayClick: (LocalDate) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    Surface {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 12.dp)
            ) {
                Text(
                    text = "Calendar & Events",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .bounceClick()
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            onDayClick(LocalDate.now())
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
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .horizontalScroll(state = rememberScrollState())
                    .animateContentSize()
                    .padding(vertical = 24.dp)
            ) {
                Spacer(modifier = Modifier.width(16.dp))
                dayEventPairs.forEach { (date, events) ->
                    ScheduleDays(
                        date = date.dayOfMonth.toString(),
                        dayOfWeek = date.dayOfWeek.getDisplayName(
                            TextStyle.SHORT,
                            Locale.getDefault()
                        ),
                        eventCount = events.size,
                        selected = selectedDate.isSame(date),
                        onClick = {
                            onDayClick(date)

                            coroutineScope.launch {
                                verticalScroll.animateScrollTo(0)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(21.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
            }
            HorizontalDivider(color = Color(0xFFCFCAE4), thickness = 1.dp)
        }
    }
}

@Composable
fun SummitSchedules(
    modifier: Modifier = Modifier,
    dailyEvents: List<SummitEvent>,
    currentTime: LocalTime,
    verticalScroll: ScrollState = rememberScrollState(),
    onEventClick: (SummitEvent) -> Unit
) {

    if (dailyEvents.isEmpty()) {
        EmptyEvent(modifier)
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(state = verticalScroll)
            .padding(start = 32.dp)
            .padding(top = 16.dp)
            .navigationBarsPadding()

    ) {

        Schedule(
            events = dailyEvents.sortedBy { it.name },
            currentTime = currentTime,
            modifier = Modifier
                .fillMaxWidth(),
            minTime = dailyEvents
                .minByOrNull { it.start }!!.start.toLocalTime()
                .minusHours(1),
            eventContent = {
                BasicEvent(
                    positionedEvent = it,
                    onEventClick = onEventClick
                )
            }
        )
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
    val scale = animateFloatAsState(if (selected) 1.4f else 1f, label = "")
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = if (selected) 10.dp else 0.dp)
            .padding(bottom = if (selected) 8.dp else 0.dp)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
    ) {
        BasicCard(
            shape = RoundedCornerShape(10.dp),
            blurRadius = 20.dp,
            shadowColor = MaterialTheme.colorScheme.secondary
        ) {
            Column(
                modifier = Modifier
                    .clickable { onClick() }
                    .clipToBounds()
                    .width(55.dp)
                    .height(45.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(18.dp)
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
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = if (selected) 0.dp else 2.dp)
                .height(12.dp)

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
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp
                )
            }
        }
    }

}

@Composable
private fun EmptyEvent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No events for today",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = modifier
                .padding(16.dp)
                .fillMaxSize()
        )
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
        Box(
            modifier = Modifier.padding(10.dp)
        ) {
            ScheduleDays(
                selected = true,
                date = "1",
                dayOfWeek = "Mon",
                eventCount = 3,
            )
        }

    }
}

@Preview
@Composable
fun ScheduleDaysUnselectedPreview() {
    NFQSnapshotTestThemeForPreview {
        ScheduleDays(
            date = "1",
            dayOfWeek = "Mon",
            eventCount = 5,
        )
    }
}

val dayEventPair = listOf(
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
)

val uiState = ScheduleUIState(
    dailyEvents = dayEventPair.flatMap { it.second },
    dayEventPairs = dayEventPair,
    currentTime = LocalTime.of(11, 0),
    selectedDate = LocalDate.of(2024, 1, 1),
)

@Preview
@Composable
fun ScheduleTabUIPreview() {
    NFQSnapshotTestThemeForPreview {

        ScheduleTabUI(
            uiState = uiState,
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
            uiState = uiState,
            onDayClick = {},
            onEventClick = {}
        )
    }
}