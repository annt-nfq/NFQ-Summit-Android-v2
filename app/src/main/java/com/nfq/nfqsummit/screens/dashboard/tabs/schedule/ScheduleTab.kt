package com.nfq.nfqsummit.screens.dashboard.tabs.schedule

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
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
import com.nfq.nfqsummit.components.ScheduleSize
import com.nfq.nfqsummit.components.SegmentedControl
import com.nfq.nfqsummit.isSame
import com.nfq.nfqsummit.mocks.mockEventDay1
import com.nfq.nfqsummit.mocks.mockEventDay1H1
import com.nfq.nfqsummit.mocks.mockEventDay1H12
import com.nfq.nfqsummit.mocks.mockEventDay1H13
import com.nfq.nfqsummit.mocks.mockEventDay2H1
import com.nfq.nfqsummit.mocks.mockEventDay2H2
import com.nfq.nfqsummit.screens.eventDetails.EventDetailsBottomSheet
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ScheduleTab(
    viewModel: ScheduleViewModel = hiltViewModel()
) {
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
        }
    )
}

@Composable
fun ScheduleTabUI(
    uiState: ScheduleUIState,
    onDayClick: (LocalDate) -> Unit,
    onEventClick: (SummitEvent) -> Unit
) {

    val verticalScroll = rememberScrollState()
    val pagerState = rememberPagerState { 2 }

    Scaffold(
        topBar = {
            ScheduleHeader(
                uiState = uiState,
                pagerState = pagerState,
                verticalScroll = verticalScroll,
                onDayClick = onDayClick
            )
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = uiState.summitEvents.isNotEmpty() && uiState.techRockEvents.isNotEmpty(),
            modifier = Modifier.padding(innerPadding)
        ) { page ->
            Surface(
                color = Color(0xFFF8F8FA),
                modifier = Modifier.fillMaxSize()
            ) {
                when (page) {
                    0 -> {
                        SummitSchedules(
                            dailyEvents = uiState.summitEvents,
                            currentTime = uiState.currentTime,
                            verticalScroll = verticalScroll,
                            onEventClick = onEventClick
                        )
                    }

                    else -> {
                        SummitSchedules(
                            dailyEvents = uiState.techRockEvents,
                            currentTime = uiState.currentTime,
                            hourSize = uiState.hourSize,
                            verticalScroll = verticalScroll,
                            onEventClick = onEventClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleHeader(
    uiState: ScheduleUIState,
    pagerState: PagerState,
    verticalScroll: ScrollState = rememberScrollState(),
    onDayClick: (LocalDate) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(uiState) {
        if (uiState.summitEvents.isEmpty() || uiState.techRockEvents.isEmpty()) {
            coroutineScope.launch {
                if (uiState.techRockEvents.isNotEmpty()) {
                    pagerState.scrollToPage(1)
                } else {
                    pagerState.scrollToPage(0)
                }
            }
        }
    }

    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    var delayIfTheFirstTime by remember { mutableStateOf(true) }

    LaunchedEffect(uiState.selectedDate) {

        val index = uiState.dayEventPairs.indexOfFirst { it.first.isSame(uiState.selectedDate) }
        if (index != -1) {
            // Delay if the first time only
            if (delayIfTheFirstTime) {
                delay(300L)
                delayIfTheFirstTime = false
            }

            // Convert measurements to pixels
            val itemWidthPx = with(density) { 80.dp.toPx() }
            val screenWidthPx = with(density) { screenWidth.toPx() }

            // Calculate center position
            val itemPosition = index * itemWidthPx
            val centerOffset = (screenWidthPx - itemWidthPx) / 2
            val targetScroll = (itemPosition - centerOffset).coerceIn(
                0f,
                scrollState.maxValue.toFloat()
            )
            // Delay if the first time only
            if (targetScroll == 0f) {
                delay(300L)
            }
            // Animate to center position
            scrollState.animateScrollTo(targetScroll.toInt())
        }
    }

    Surface {
        Column(
            modifier = Modifier.animateContentSize()
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(state = scrollState)
                    .animateContentSize()
                    .padding(vertical = 24.dp)
            ) {
                Spacer(modifier = Modifier.width(16.dp))

                uiState.dayEventPairs.forEach { (date, events) ->
                    ScheduleDays(
                        date = date.dayOfMonth.toString(),
                        dayOfWeek = date.dayOfWeek.getDisplayName(
                            TextStyle.SHORT,
                            Locale.getDefault()
                        ),
                        eventCount = Pair(
                            events.toSummitEvents().size,
                            events.toTechRockEvents().size
                        ),
                        selected = uiState.selectedDate.isSame(date),
                        onClick = {
                            onDayClick(date)

                            coroutineScope.launch {
                                verticalScroll.animateScrollTo(0)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(21.dp))
                }
            }
            if (uiState.summitEvents.isNotEmpty() && uiState.techRockEvents.isNotEmpty()) {
                SegmentedControl(
                    items = listOf(
                        "Summit \uD83D\uDCBC",
                        "Tech Rock \uD83E\uDD16 "
                    ),
                    selectedIndex = pagerState.currentPage,
                    onItemSelection = {
                        coroutineScope.launch { pagerState.animateScrollToPage(it) }
                    },
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 24.dp)
                        .padding()
                )
            }

            HorizontalDivider(color = Color(0xFFCFCAE4), thickness = 1.dp)
        }
    }
}

@Composable
fun SummitSchedules(
    modifier: Modifier = Modifier,
    dailyEvents: PersistentList<SummitEvent>,
    currentTime: LocalTime,
    hourSize: ScheduleSize = ScheduleSize.FixedSize(130.dp),
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
            .padding(start = 8.dp)
            .padding(top = 16.dp)
            .navigationBarsPadding()
    ) {

        Schedule(
            events = dailyEvents.sortedBy { it.name }.toPersistentList(),
            currentTime = currentTime,
            hourSize = hourSize,
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
    eventCount: Pair<Int, Int>,
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
            blurRadius = 10.dp,
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
            val totalEventCount = eventCount.first + eventCount.second
            val primaryColor = MaterialTheme.colorScheme.primary
            val secondaryColor = Color(0xFF42389D)

            if (totalEventCount <= 3) {
                repeat(eventCount.first) {
                    EventCountIndicator(primaryColor)
                }
                repeat(eventCount.second) {
                    EventCountIndicator(secondaryColor)
                }
            } else {
                val primaryCount =
                    if (eventCount.first < 3) eventCount.first else if (eventCount.second == 0) 3 else 2
                val secondaryCount = if (eventCount.first == 0) 3 else if (eventCount.second != 0) 1 else 0

                repeat(primaryCount) {
                    EventCountIndicator(primaryColor)
                }
                repeat(secondaryCount) {
                    EventCountIndicator(secondaryColor)
                }
                Text(
                    text = "+${totalEventCount - 3}",
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
            text = "No events",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EventCountIndicator(
    color: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = Modifier
            .padding(1.dp)
            .size(4.dp)
            .background(color, CircleShape)
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
                eventCount = Pair(2, 1),
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
            eventCount = Pair(2, 1),
        )
    }
}

val dayEventPair = persistentListOf(
    LocalDate.of(2024, 1, 1) to persistentListOf(
        mockEventDay1,
        mockEventDay1H1,
        mockEventDay1H1,
        mockEventDay1H12,
        mockEventDay1H12,
        mockEventDay1H12,
        mockEventDay1H13
    ),
    LocalDate.of(2024, 1, 2) to persistentListOf(
        mockEventDay2H1,
        mockEventDay2H2,
    )
)

val uiState = ScheduleUIState(
    dayEventPairs = dayEventPair,
    summitEvents = dayEventPair[0].second,
    currentTime = LocalTime.of(11, 40),
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