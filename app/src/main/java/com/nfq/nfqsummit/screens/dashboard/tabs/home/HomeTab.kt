@file:OptIn(ExperimentalPermissionsApi::class)

package com.nfq.nfqsummit.screens.dashboard.tabs.home

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.nfq.nfqsummit.R
import com.nfq.nfqsummit.components.BasicAlertDialog
import com.nfq.nfqsummit.components.BasicCard
import com.nfq.nfqsummit.components.Loading
import com.nfq.nfqsummit.components.bounceClick
import com.nfq.nfqsummit.mocks.mockSavedEvents
import com.nfq.nfqsummit.mocks.mockUpcomingEvents
import com.nfq.nfqsummit.model.SavedEventUIModel
import com.nfq.nfqsummit.model.UpcomingEventUIModel
import com.nfq.nfqsummit.screens.dashboard.tabs.home.component.SavedEventCard
import com.nfq.nfqsummit.screens.dashboard.tabs.home.component.UpcomingEventCard
import com.nfq.nfqsummit.screens.eventDetails.EventDetailsBottomSheet
import com.nfq.nfqsummit.screens.eventDetails.scheduleAlarm
import com.nfq.nfqsummit.screens.eventDetails.setUpScheduler
import com.nfq.nfqsummit.screens.qrCode.QRCodeBottomSheet
import com.nfq.nfqsummit.ui.theme.NFQOrange
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview
import com.nfq.nfqsummit.ui.theme.boxShadow

@Composable
fun HomeTab(
    viewModel: HomeViewModel = hiltViewModel(),
    goToAttractions: () -> Unit,
    goToSignIn: () -> Unit,
    seeAllEvents: () -> Unit = {},
    seeAllSavedEvents: () -> Unit = {},
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showEventDetailsBottomSheet by remember { mutableStateOf(false) }
    var eventId by remember { mutableStateOf("") }
    var showQRCodeBottomSheet by remember { mutableStateOf(false) }

    val permission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.POST_NOTIFICATIONS
        else Manifest.permission.ACCESS_NOTIFICATION_POLICY
    val notificationPermissionState = rememberPermissionState(permission = permission)

    var showAlarmRequest by remember { mutableStateOf(false) }
    var showNotificationRequest by remember { mutableStateOf(false) }

    if (uiState.isLoading) {
        Loading()
    }

    if (showQRCodeBottomSheet) {
        QRCodeBottomSheet(
            user = uiState.user!!,
            onDismissRequest = { showQRCodeBottomSheet = false }
        )
    }

    if (showEventDetailsBottomSheet) {
        EventDetailsBottomSheet(
            eventId = eventId,
            onDismissRequest = { showEventDetailsBottomSheet = false }
        )
    }

    if (showNotificationRequest) {
        BasicAlertDialog(
            title = "Allow notification permission",
            body = "This app requires notification permission to show you reminders of your saved events",
            dismissButtonText = "Deny",
            dismissButton = { showNotificationRequest = false },
            confirmButtonText = "Allow",
            confirmButton = {
                showNotificationRequest = false
                context.startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:${context.packageName}")
                    ).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                )
            }
        )
    }

    if (showAlarmRequest) {
        BasicAlertDialog(
            title = "Allow alarm permission",
            body = "This app requires alarm permission to show you reminders of your saved events",
            dismissButtonText = "Deny",
            dismissButton = { showAlarmRequest = false },
            confirmButtonText = "Allow",
            confirmButton = {
                showAlarmRequest = false
                scheduleAlarm(context)
            }
        )
    }


    HomeTabUI(
        uiState = uiState,
        goToAttractions = goToAttractions,
        goToSignIn = goToSignIn,
        markAsFavorite = { isFavorite, event ->
            setUpScheduler(
                context = context,
                isFavorite = isFavorite,
                startDateTime = event.startDateTime,
                eventName = event.name,
                eventId = event.id,
                notificationPermissionState = notificationPermissionState,
                showAlarmRequest = { showAlarmRequest = it },
                showNotificationRequest = { showNotificationRequest = it },
                markEventAsFavorite = viewModel::markAsFavorite
            )
        },
        seeAllSavedEvents = seeAllSavedEvents,
        seeAllEvents = seeAllEvents,
        goToDetails = {
            eventId = it
            showEventDetailsBottomSheet = true
        },
        onShowQRCode = {
            showQRCodeBottomSheet = true
        }
    )
}

@Composable
private fun HomeTabUI(
    uiState: HomeUIState,
    goToDetails: (String) -> Unit,
    goToSignIn: () -> Unit,
    goToAttractions: () -> Unit,
    seeAllEvents: () -> Unit = {},
    seeAllSavedEvents: () -> Unit = {},
    onShowQRCode: () -> Unit,
    markAsFavorite: (favorite: Boolean, event: UpcomingEventUIModel) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 24.dp),
            modifier = Modifier.navigationBarsPadding()
        ) {

            showQRCodeSection(
                onShowQRCode = {
                    if (uiState.user == null) {
                        goToSignIn()
                    } else {
                        onShowQRCode()
                    }
                }
            )

            upcomingEventsSection(
                upcomingEvents = uiState.upcomingEvents,
                markAsFavorite = markAsFavorite,
                goToDetails = goToDetails,
                seeAllEvents = seeAllEvents
            )
            savedEventSection(
                savedEvents = uiState.savedEvents,
                goToDetails = goToDetails,
                goToAttractions = goToAttractions,
                seeAllSavedEvents = seeAllSavedEvents
            )
        }
    }
}

private fun LazyListScope.showQRCodeSection(
    onShowQRCode: () -> Unit,
) {
    item {
        Box(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
                .padding(top = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .bounceClick()
                    .height(height = 117.dp)
                    .boxShadow(
                        color = Color(0xFF1E1C2E).copy(alpha = 0.08f),
                        blurRadius = 48.dp,
                        spreadRadius = 0.dp,
                        offset = DpOffset(0.dp, 24.dp)
                    )
                    .clip(RoundedCornerShape(32.dp))
                    .clickable { onShowQRCode() }
                    .background(MaterialTheme.colorScheme.surface)) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_logo_qrcode),
                        contentDescription = null,
                        modifier = Modifier.size(75.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Tap to show my QR Code",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "You'll need to show this at NFQ Summit registration \uD83D\uDCCB",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onBackground.copy(0.5f)
                            )
                        )
                    }
                }
            }
        }
    }
}

fun LazyListScope.upcomingEventsSection(
    upcomingEvents: List<UpcomingEventUIModel>,
    goToDetails: (String) -> Unit,
    seeAllEvents: () -> Unit = {},
    markAsFavorite: (isFavorite: Boolean, event: UpcomingEventUIModel) -> Unit
) {
    item {
        if (upcomingEvents.isEmpty()) return@item
        val pagerState = rememberPagerState { upcomingEvents.size }
        Column {
            SectionHeader(title = "Upcoming Events", onSeeAll = seeAllEvents)
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(start = 24.dp, end = 134.dp),
                pageSpacing = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                val uiModel = upcomingEvents[it]
                UpcomingEventCard(
                    uiModel = uiModel,
                    goToDetails = goToDetails,
                    markAsFavorite = { isFavorite, _ ->
                        markAsFavorite(isFavorite, uiModel)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    modifier: Modifier = Modifier,
    title: String, onSeeAll: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 24.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .bounceClick()
                .clip(RoundedCornerShape(8.dp))
                .clickable { onSeeAll() }
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

private fun LazyListScope.savedEventSection(
    savedEvents: List<SavedEventUIModel>,
    goToDetails: (String) -> Unit,
    goToAttractions: () -> Unit,
    seeAllSavedEvents: () -> Unit = {}
) {
    item {
        SectionHeader(
            title = "Saved Event",
            onSeeAll = seeAllSavedEvents,
            modifier = Modifier
                .padding(top = 24.dp)
                .padding(bottom = 16.dp)
        )
    }

    if (savedEvents.isNotEmpty()) {
        items(savedEvents) { uiModel ->
            SavedEventCard(
                uiModel = uiModel,
                goToEventDetails = goToDetails,
                modifier = Modifier
                    .animateItem(
                        fadeInSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        placementSpec = spring(
                            stiffness = Spring.StiffnessMediumLow,
                            visibilityThreshold = IntOffset.VisibilityThreshold
                        ),
                        fadeOutSpec = spring(stiffness = Spring.StiffnessMediumLow)
                    )
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 8.dp)
            )
        }
    } else {
        item {
            BasicCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        "No Favorites Yet!",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "You have not marked any favorites",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("View Attractions",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = NFQOrange, textDecoration = TextDecoration.Underline
                        ),
                        modifier = Modifier.clickable {
                            goToAttractions()
                        })
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeTabUIPreview() {
    NFQSnapshotTestThemeForPreview {
        HomeTabUI(
            goToDetails = {},
            goToAttractions = {},
            goToSignIn = {},
            markAsFavorite = { _, _ -> },
            uiState = HomeUIState(
                upcomingEvents = mockUpcomingEvents,
                savedEvents = mockSavedEvents
            ),
            onShowQRCode = {}
        )
    }
}

@Preview
@Composable
fun HomeTabUIDarkPreview() {
    NFQSnapshotTestThemeForPreview(darkTheme = true) {
        HomeTabUI(
            goToDetails = {},
            goToAttractions = {},
            goToSignIn = {},
            markAsFavorite = { _, _ -> },
            uiState = HomeUIState(
                upcomingEvents = mockUpcomingEvents,
                savedEvents = mockSavedEvents
            ),
            onShowQRCode = {},
        )
    }
}