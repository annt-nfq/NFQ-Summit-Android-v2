package com.nfq.nfqsummit.screens.dashboard.tabs.techRocks

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nfq.data.domain.model.SummitEvent
import com.nfq.nfqsummit.components.BasicEvent
import com.nfq.nfqsummit.components.Schedule
import com.nfq.nfqsummit.components.ScheduleSize
import com.nfq.nfqsummit.ui.theme.NFQOrange
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview
import java.time.LocalDate
import java.time.LocalTime

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TechRocksTab(
    viewModel: TechRocksViewModel = hiltViewModel(),
    goToEventDetails: (eventId: String) -> Unit
) {
    val window = (LocalView.current.context as Activity).window
    window.statusBarColor = Color.Transparent.toArgb()

    Scaffold {
        Column {
            ConferenceSchedule(
                events = viewModel.events,
                currentTime = viewModel.currentTime,
                onEventClick = {
                    goToEventDetails(it.id)
                }
            )
        }
    }
}

@Composable
fun SegmentedControl(
    items: List<String>,
    selectedIndex: Int,
    onItemSelection: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var itemWidths by remember { mutableStateOf(List(items.size) { 0.dp }) }

    fun calculateIndicatorOffset(selectedIndex: Int): Dp {
        return itemWidths.take(selectedIndex).fold(0.dp) { acc, dp -> acc + dp }
    }

    val indicatorOffset by animateDpAsState(
        targetValue = calculateIndicatorOffset(selectedIndex),
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f), label = ""
    )

    val indicatorWidth by animateDpAsState(
        targetValue = itemWidths.getOrNull(selectedIndex) ?: 0.dp,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f), label = ""
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color.LightGray.copy(alpha = 0.3f))
            .padding(4.dp)
    ) {
        // Animated Indicator
        Box(
            Modifier
                .offset(x = indicatorOffset)
                .width(indicatorWidth)
                .fillMaxHeight()
                .clip(RoundedCornerShape(20.dp))
                .background(NFQOrange)
        )

        // Items
        Row(modifier = Modifier.fillMaxWidth()) {
            items.forEachIndexed { index, item ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onItemSelection(index) }
                        .onGloballyPositioned { coordinates ->
                            itemWidths = itemWidths
                                .toMutableList()
                                .also { list ->
                                    list[index] = with(density) { coordinates.size.width.toDp() }
                                }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        color = if (selectedIndex == index) Color.White else MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ConferenceSchedule(
    events: List<SummitEvent>,
    currentTime: LocalTime,
    onEventClick: (SummitEvent) -> Unit,
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    Column(
        modifier = Modifier.statusBarsPadding()
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Tech Rocks Asia",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        SegmentedControl(
            items = listOf("Business", "Product", "Tech"),
            selectedIndex = selectedIndex,
            onItemSelection = { selectedIndex = it },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(48.dp)
        )
        Column(Modifier.verticalScroll(rememberScrollState())) {
            Surface(color = MaterialTheme.colorScheme.background) {
                Schedule(
                    hourSize = ScheduleSize.FixedSize(220.dp),
                    events = events.filter { it.ordering == selectedIndex + 1 },
                    currentTime = currentTime,
                    minTime = LocalTime.of(10, 0, 0, 0),
                    maxTime = LocalTime.of(18, 0, 0, 0),
                    eventContent = {
                        BasicEvent(
                            positionedEvent = it,
                            titleMaxLines = 2,
                            compact = true,
                            roundedAvatar = true,
                            modifier = Modifier
                                .clickable {
                                    onEventClick(it.event)
                                }
                        )
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConferenceSchedulePreview() {
    NFQSnapshotTestThemeForPreview {
        ConferenceSchedule(
            events = listOf(
                SummitEvent(
                    "1",
                    "Event 1",
                    LocalDate.now().atTime(9, 0),
                    LocalDate.now().atTime(10, 0),
                ),
                SummitEvent(
                    "2",
                    "Event 2",
                    LocalDate.now().atTime(10, 0),
                    LocalDate.now().atTime(11, 0),
                    iconUrl = "",
                    ordering = 1,
                    speakerName = "Speaker One"
                ),
                SummitEvent(
                    "3",
                    "Event 3",
                    LocalDate.now().atTime(10, 0),
                    LocalDate.now().atTime(11, 0),
                    iconUrl = "",
                    ordering = 2,
                    speakerName = "Speaker Two"
                ),
                SummitEvent(
                    "4",
                    "Event 4",
                    LocalDate.now().atTime(10, 0),
                    LocalDate.now().atTime(11, 0),
                    iconUrl = "",
                    ordering = 3,
                    speakerName = "Speaker Three"
                ),
            ),
            currentTime = LocalTime.now(),
        ) {
        }
    }
}