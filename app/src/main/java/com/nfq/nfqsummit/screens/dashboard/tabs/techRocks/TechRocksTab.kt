package com.nfq.nfqsummit.screens.dashboard.tabs.techRocks

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nfq.data.domain.model.SummitEvent
import com.nfq.nfqsummit.components.BasicEvent
import com.nfq.nfqsummit.components.Schedule
import com.nfq.nfqsummit.components.ScheduleSize
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview
import java.time.LocalDate
import java.time.LocalTime

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TechRocksTab(
    viewModel: TechRocksViewModel = hiltViewModel(),
    goToEventDetails: (eventId: String) -> Unit
) {
    Scaffold {
        ConferenceSchedule(
            events = viewModel.events,
            currentTime = viewModel.currentTime,
            onEventClick = {
                goToEventDetails(it.id)
            }
        )
    }
}

@Composable
fun ConferenceSchedule(
    events: List<SummitEvent>,
    currentTime: LocalTime,
    onEventClick: (SummitEvent) -> Unit,
) {
    Column(
        modifier = Modifier.statusBarsPadding()
    ) {
        Text(
            text = "Tech Rocks Asia",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(Modifier.verticalScroll(rememberScrollState())) {
            Surface(color = MaterialTheme.colorScheme.background) {
                Schedule(
                    hourSize = ScheduleSize.FixedSize(320.dp),
                    events = events.sortedBy { it.ordering },
                    currentTime = currentTime,
                    minTime = LocalTime.of(9, 0, 0, 0),
                    maxTime = LocalTime.of(18, 0, 0, 0),
                    eventContent = {
                        BasicEvent(
                            positionedEvent = it,
                            titleMaxLines = 4,
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
                    ordering = 2
                    ,
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