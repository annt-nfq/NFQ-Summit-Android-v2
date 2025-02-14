package com.nfq.nfqsummit.screens.savedEvents

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nfq.nfqsummit.R
import com.nfq.nfqsummit.analytics.TrackScreenViewEvent
import com.nfq.nfqsummit.mocks.mockSavedEvents
import com.nfq.nfqsummit.model.SavedEventUIModel
import com.nfq.nfqsummit.screens.dashboard.tabs.home.component.SavedEventCard
import com.nfq.nfqsummit.screens.eventDetails.EventDetailsBottomSheet
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview

@Composable
fun SavedEventTab(
    navigateUp: () -> Unit
) {
    val viewModel: SavedEventViewModel = hiltViewModel()
    val savedEvents by viewModel.savedEvents.collectAsState()
    var showEventDetailsBottomSheet by remember { mutableStateOf(false) }
    var eventId by remember { mutableStateOf("") }

    if (showEventDetailsBottomSheet) {
        EventDetailsBottomSheet(
            eventId = eventId,
            onDismissRequest = { showEventDetailsBottomSheet = false }
        )
    }

    SavedEventUI(
        savedEvents = savedEvents,
        navigateUp = navigateUp,
        goToDetails = {
            eventId = it
            showEventDetailsBottomSheet = true
        }
    )

    TrackScreenViewEvent(screenName = "SavedEvent")
}

@Composable
private fun SavedEventUI(
    savedEvents: List<SavedEventUIModel>,
    navigateUp: () -> Unit,
    goToDetails: (String) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .padding(top = 8.dp)
            ) {
                IconButton(
                    onClick = { navigateUp() }
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = null
                    )
                }
                Text(
                    text = "Saved Events",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(savedEvents) { uiModel ->
                SavedEventCard(
                    uiModel = uiModel,
                    goToEventDetails = goToDetails,
                )
            }
        }
    }
}

@Preview
@Composable
private fun SavedEventUIPreview() {
    NFQSnapshotTestThemeForPreview {
        SavedEventUI(
            savedEvents = mockSavedEvents,
            goToDetails = {},
            navigateUp = {}
        )
    }
}