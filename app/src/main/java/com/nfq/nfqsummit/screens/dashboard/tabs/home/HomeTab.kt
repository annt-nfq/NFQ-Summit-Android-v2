@file:OptIn(ExperimentalPermissionsApi::class)

package com.nfq.nfqsummit.screens.dashboard.tabs.home

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.nfq.nfqsummit.R
import com.nfq.nfqsummit.analytics.TrackScreenViewEvent
import com.nfq.nfqsummit.analytics.helper.LocalAnalyticsHelper
import com.nfq.nfqsummit.analytics.logTapToShowQrCode
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
import com.nfq.nfqsummit.screens.dashboard.tabs.home.component.VouchersDialog
import com.nfq.nfqsummit.screens.eventDetails.EventDetailsBottomSheet
import com.nfq.nfqsummit.screens.eventDetails.HandlePermissionDialogs
import com.nfq.nfqsummit.screens.eventDetails.setUpScheduler
import com.nfq.nfqsummit.screens.qrCode.QRCodeBottomSheet
import com.nfq.nfqsummit.screens.signIn.SignInBottomSheet
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
    val analyticsHelper = LocalAnalyticsHelper.current
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val showReminderDialog by viewModel.showReminderDialog.collectAsState()
    val notificationPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.POST_NOTIFICATIONS
        else Manifest.permission.ACCESS_NOTIFICATION_POLICY
    val notificationPermissionState = rememberPermissionState(permission = notificationPermission)


    var isEventDetailsBottomSheetVisible by remember { mutableStateOf(false) }
    var isSignInBottomSheetVisible by remember { mutableStateOf(false) }
    var selectedEventId by remember { mutableStateOf("") }
    var isQRCodeBottomSheetVisible by remember { mutableStateOf(false) }
    var isVoucherDialogVisible by remember { mutableStateOf(false) }
    var isAlarmRequestVisible by remember { mutableStateOf(false) }
    var isNotificationRequestVisible by remember { mutableStateOf(false) }
    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isAlarmRequestVisible = true
        }
    }

    LaunchedEffect(Unit) {
        pendingAction?.invoke()
    }

    if (uiState.isLoading) {
        Loading()
    }

    if (isQRCodeBottomSheetVisible) {
        QRCodeBottomSheet(
            user = uiState.user!!,
            onDismissRequest = { isQRCodeBottomSheetVisible = false }
        )
    }

    if (isVoucherDialogVisible) {
        VouchersDialog(
            attendeeName = uiState.user!!.name,
            vouchers = uiState.vouchers,
            onDismissRequest = { isVoucherDialogVisible = false }
        )
    }

    if (isEventDetailsBottomSheetVisible) {
        EventDetailsBottomSheet(
            eventId = selectedEventId,
            onDismissRequest = { isEventDetailsBottomSheetVisible = false }
        )
    }

    if (isSignInBottomSheetVisible) {
        SignInBottomSheet(
            onDismissRequest = { isSignInBottomSheetVisible = false },
            goToSignIn = goToSignIn
        )
    }

    if (showReminderDialog) {
        BasicAlertDialog(
            title = "\"NFQ-summit\" Would Like to Send You Notifications",
            body = "Receive reminders 45 minutes and 10 minutes before registered Summit events and saved Tech Rock events start",
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            ),
            confirmButtonText = "Allow",
            confirmButton = {
                if (uiState.upcomingEventsWithoutTechRocks.isEmpty()) {
                    viewModel.updateNotificationSetting(
                        isShownNotificationPermissionDialog = true,
                        isEnabledNotification = true
                    )
                } else {
                    pendingAction = {
                        uiState.upcomingEventsWithoutTechRocks.forEach { event ->
                            setUpScheduler(
                                context = context,
                                setReminder = true,
                                startDateTime = event.startDateTime,
                                eventName = event.name,
                                eventId = event.id,
                                notificationPermissionState = notificationPermissionState,
                                showNotificationRequest = { isNotificationRequestVisible = it },
                                showAlarmRequest = { isAlarmRequestVisible = it },
                                permissionLauncher = {
                                    permissionLauncher.launch(notificationPermission)
                                },
                                updateNotificationSetting = {
                                    viewModel.updateNotificationSetting(
                                        isShownNotificationPermissionDialog = true,
                                        isEnabledNotification = true
                                    )
                                }
                            )
                        }
                    }

                    pendingAction?.invoke()
                }
            },
            dismissButtonText = "Don't Allow",
            dismissButton = {
                viewModel.updateNotificationSetting(
                    isShownNotificationPermissionDialog = true,
                    isEnabledNotification = false
                )
            }
        )
    }

    HandlePermissionDialogs(
        pendingAction = pendingAction,
        isNotificationRequestVisible = isNotificationRequestVisible,
        isAlarmRequestVisible = isAlarmRequestVisible,
        onShowNotificationRequest = { isNotificationRequestVisible = it },
        onShowAlarmRequest = { isAlarmRequestVisible = it }
    )

    HomeTabUI(
        uiState = uiState,
        goToAttractions = goToAttractions,
        onShowSignInBS = { isSignInBottomSheetVisible = true },
        markAsFavorite = { isFavorite, event ->
            pendingAction = {
                setUpScheduler(
                    context = context,
                    setReminder = isFavorite,
                    startDateTime = event.startDateTime,
                    eventName = event.name,
                    eventId = event.id,
                    notificationPermissionState = notificationPermissionState,
                    showNotificationRequest = { isNotificationRequestVisible = it },
                    showAlarmRequest = { isAlarmRequestVisible = it },
                    markEventAsFavorite = viewModel::markAsFavorite,
                    permissionLauncher = { permissionLauncher.launch(notificationPermission) }
                )
            }
            pendingAction?.invoke()
        },
        seeAllSavedEvents = seeAllSavedEvents,
        seeAllEvents = seeAllEvents,
        goToDetails = {
            selectedEventId = it
            isEventDetailsBottomSheetVisible = true
        },
        onShowVoucher = {
            isVoucherDialogVisible = true
        },
        onShowQRCode = {
            uiState.user?.attendeeCode?.let {
                analyticsHelper.logTapToShowQrCode(it)
            }

            isQRCodeBottomSheetVisible = true
        }
    )

    TrackScreenViewEvent(screenName = "home")
}

@Composable
private fun HomeTabUI(
    uiState: HomeUIState,
    goToDetails: (String) -> Unit,
    onShowSignInBS: () -> Unit,
    goToAttractions: () -> Unit,
    seeAllEvents: () -> Unit = {},
    seeAllSavedEvents: () -> Unit = {},
    onShowQRCode: () -> Unit,
    onShowVoucher: () -> Unit,
    markAsFavorite: (favorite: Boolean, event: UpcomingEventUIModel) -> Unit
) {
    val containerColor = if (uiState.user != null) MaterialTheme.colorScheme.surface
    else MaterialTheme.colorScheme.primary
    val contentColor = MaterialTheme.colorScheme.onPrimary

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 24.dp),
            modifier = Modifier.navigationBarsPadding()
        ) {
            if (uiState.user == null) {
                tapToShowSection(
                    modifier = Modifier.padding(top = 12.dp),
                    iconRes = R.drawable.ic_face_id_solid,
                    iconSize = 75.dp,
                    title = "Please sign-in",
                    description = "Sign in with your QR or attendee code to see your registered events.",
                    containerColor = containerColor,
                    contentColor = contentColor,
                    onTap = { onShowSignInBS() }
                )
            }
            if (uiState.user != null) {
                tapToShowSection(
                    modifier = Modifier.padding(top = 12.dp),
                    iconRes = R.drawable.ic_logo_qrcode,
                    iconSize = 75.dp,
                    title = "Tap to show my QR Code",
                    description = "You'll need to show this at NFQ Summit registration \uD83D\uDCCB",
                    containerColor = containerColor,
                    onTap = { onShowQRCode() }
                )
            }

            if (uiState.user != null && uiState.vouchers.isNotEmpty()) {
                tapToShowSection(
                    modifier = Modifier.padding(top = 8.dp),
                    iconRes = R.drawable.ic_voucher,
                    iconSize = 82.dp,
                    title = "Tap to show my vouchers",
                    description = "Quick access to your vouchers at a tap! \uD83C\uDFAB",
                    containerColor = containerColor,
                    onTap = onShowVoucher
                )
            }

            upcomingEventsSection(
                upcomingEvents = uiState.upcomingEvents,
                markAsFavorite = markAsFavorite,
                goToDetails = goToDetails,
                seeAllEvents = seeAllEvents,
                modifier = Modifier.padding(top = 24.dp)
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

private fun LazyListScope.tapToShowSection(
    onTap: () -> Unit,
    iconRes: Int,
    iconSize: Dp,
    title: String,
    description: String,
    containerColor: Color,
    contentColor: Color? = null,
    modifier: Modifier = Modifier
) {
    item {
        Box(
            modifier = modifier.padding(horizontal = 24.dp)
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
                    .clickable { onTap() }
                    .background(containerColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(iconSize)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = contentColor ?: MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            color = contentColor
                                ?: MaterialTheme.colorScheme.onBackground.copy(0.5f)
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
    modifier: Modifier = Modifier,
    markAsFavorite: (isFavorite: Boolean, event: UpcomingEventUIModel) -> Unit
) {
    item {
        if (upcomingEvents.isEmpty()) return@item
        val pagerState = rememberPagerState { upcomingEvents.size }
        Column(modifier = modifier) {
            SectionHeader(title = "Upcoming Events", onSeeAll = seeAllEvents)
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalPager(
                state = pagerState,
                verticalAlignment = Alignment.Top,
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
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    modifier: Modifier = Modifier,
    showSeeAllBtn: Boolean = true,
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
        if (showSeeAllBtn) {
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
            showSeeAllBtn = savedEvents.isNotEmpty(),
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
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        "No Saved Events",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Bookmark your must-attend sessions for easy access!",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.5f)
                    )
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
            onShowSignInBS = {},
            markAsFavorite = { _, _ -> },
            uiState = HomeUIState(
                upcomingEvents = mockUpcomingEvents,
                savedEvents = mockSavedEvents
            ),
            onShowQRCode = {},
            onShowVoucher = {}
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
            onShowSignInBS = {},
            markAsFavorite = { _, _ -> },
            uiState = HomeUIState(
                upcomingEvents = mockUpcomingEvents,
                savedEvents = mockSavedEvents
            ),
            onShowQRCode = {},
            onShowVoucher = {}
        )
    }
}