@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)

package com.nfq.nfqsummit.screens.eventDetails

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.nfq.data.domain.model.EventDetailsModel
import com.nfq.data.domain.model.EventLocationsModel
import com.nfq.data.domain.model.SpeakerModel
import com.nfq.nfqsummit.R
import com.nfq.nfqsummit.analytics.TrackEventDetailScreenViewEvent
import com.nfq.nfqsummit.analytics.helper.LocalAnalyticsHelper
import com.nfq.nfqsummit.analytics.logViewLocation
import com.nfq.nfqsummit.components.AnnotatedHtmlStringWithLink
import com.nfq.nfqsummit.components.BasicAlertDialog
import com.nfq.nfqsummit.components.BasicCard
import com.nfq.nfqsummit.components.BasicModalBottomSheet
import com.nfq.nfqsummit.components.bounceClick
import com.nfq.nfqsummit.components.networkImagePainter
import com.nfq.nfqsummit.notification.AlarmReceiver
import com.nfq.nfqsummit.openMapView
import com.nfq.nfqsummit.screens.dashboard.tabs.home.component.BookmarkItem
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview
import com.nfq.nfqsummit.ui.theme.bottomSheetMedium
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun EventDetailsBottomSheet(
    eventId: String,
    onDismissRequest: () -> Unit
) {
    val analyticsHelper = LocalAnalyticsHelper.current
    val skipPartiallyExpanded by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
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
    val viewModel: EventDetailsBottomSheetViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(eventId) {
        viewModel.getEvent(eventId)
    }

    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.POST_NOTIFICATIONS
    } else {
        Manifest.permission.ACCESS_NOTIFICATION_POLICY
    }
    val notificationPermissionState = rememberPermissionState(permission = permission)

    HandlePermissionDialogs(
        pendingAction = pendingAction,
        isNotificationRequestVisible = isNotificationRequestVisible,
        isAlarmRequestVisible = isAlarmRequestVisible,
        onShowNotificationRequest = { isNotificationRequestVisible = it },
        onShowAlarmRequest = { isAlarmRequestVisible = it }
    )

    BasicModalBottomSheet(
        onDismissRequest = onDismissRequest,
        content = {
            uiState.event?.let { event ->
                EventDetailsUI(
                    event = event,
                    markAsFavorite = { isFavorite, _ ->
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
                                markEventAsFavorite = viewModel::markEventAsFavorite,
                                permissionLauncher = { permissionLauncher.launch(permission) }
                            )
                        }
                        pendingAction?.invoke()
                    },
                    onViewLocation = { latitude, longitude, locationName ->
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                analyticsHelper.logViewLocation(
                                    attendeeCode = uiState.attendeeCode,
                                    eventId = event.id,
                                    eventTitle = event.name
                                )
                                context.openMapView(
                                    latitude = latitude,
                                    longitude = longitude,
                                    locationName = locationName
                                )
                            }
                        }
                    }
                )
            }
        }
    )
    TrackEventDetailScreenViewEvent(
        screenName = "event_detail",
        eventId = eventId,
        eventTitle = uiState.event?.name ?: ""
    )
}

@Composable
fun HandlePermissionDialogs(
    isNotificationRequestVisible: Boolean,
    isAlarmRequestVisible: Boolean,
    pendingAction: (() -> Unit)? = null,
    onShowNotificationRequest: (Boolean) -> Unit,
    onShowAlarmRequest: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val alarmScheduler = rememberAlarmScheduler(
        onAlarmSet = { pendingAction?.invoke() },
        onPermissionDenied = {}
    )
    if (isNotificationRequestVisible) {
        BasicAlertDialog(
            title = "Allow notification permission",
            body = "This app requires notification permission to show you reminders of your saved events",
            dismissButtonText = "Deny",
            dismissButton = { onShowNotificationRequest(false) },
            confirmButtonText = "Allow",
            confirmButton = {
                onShowNotificationRequest(false)
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

    if (isAlarmRequestVisible) {
        BasicAlertDialog(
            title = "Allow alarm permission",
            body = "This app requires alarm permission to show you reminders of your saved events",
            dismissButtonText = "Deny",
            dismissButton = { onShowAlarmRequest(false) },
            confirmButtonText = "Allow",
            confirmButton = {
                alarmScheduler.scheduleAlarm()
                onShowAlarmRequest(false)
            }
        )
    }
}

fun setUpScheduler(
    context: Context,
    setReminder: Boolean,
    startDateTime: LocalDateTime,
    eventName: String,
    eventId: String,
    notificationPermissionState: PermissionState,
    showNotificationRequest: (Boolean) -> Unit,
    showAlarmRequest: (Boolean) -> Unit,
    markEventAsFavorite: (isFavorite: Boolean, eventId: String) -> Unit = { _, _ -> },
    updateNotificationSetting: () -> Unit = {},
    permissionLauncher: () -> Unit = {}
) {
    if (!notificationPermissionState.status.isGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (notificationPermissionState.status.shouldShowRationale) {
            showNotificationRequest(true)
        } else {
            permissionLauncher()
        }
        return
    }

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val hasAlarmPermission = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
        true
    } else {
        alarmManager.canScheduleExactAlarms()
    }

    if (!hasAlarmPermission) {
        showAlarmRequest(true)
        return
    }

    updateNotificationSetting()

    if (setReminder) {
        markEventAsFavorite(true, eventId)
        createNotificationChannel(context)
        scheduleNotification(
            context,
            startDateTime.minusMinutes(10),
            eventName,
            "This event is starting in 10 minutes",
            eventId,
            "1"
        )
        scheduleNotification(
            context,
            startDateTime.minusMinutes(45),
            eventName,
            "This event is starting in 45 minutes",
            eventId,
            "2"
        )
    } else {
        markEventAsFavorite(false, eventId)
        cancelNotification(context, eventId, "1")
        cancelNotification(context, eventId, "2")
    }
}


@Composable
fun rememberAlarmScheduler(
    onAlarmSet: () -> Unit = {},
    onPermissionDenied: () -> Unit = {}
): AlarmScheduler {
    val context = LocalContext.current
    val alarmManager = remember {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            when {
                alarmManager.canScheduleExactAlarms() -> onAlarmSet()
                else -> onPermissionDenied()
            }
        }
    }

    return remember(permissionLauncher) {
        AlarmScheduler(
            context = context,
            alarmManager = alarmManager,
            permissionLauncher = permissionLauncher
        )
    }
}

class AlarmScheduler(
    private val context: Context,
    private val alarmManager: AlarmManager,
    private val permissionLauncher: androidx.activity.result.ActivityResultLauncher<Intent>
) {
    fun scheduleAlarm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // Launch permission request for Android 12+
                val intent = Intent(
                    Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                    Uri.parse("package:${context.packageName}")
                )
                permissionLauncher.launch(intent)
            } else {
                scheduleExactAlarm()
            }
        } else {
            // For Android 11 and below, directly schedule the alarm
            scheduleExactAlarm()
        }
    }

    private fun scheduleExactAlarm() {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                pendingIntent
            )
        }
    }
}

@Composable
private fun EventDetailsUI(
    event: EventDetailsModel,
    markAsFavorite: (isFavorite: Boolean, event: String) -> Unit = { _, _ -> },
    onViewLocation: (latitude: Double?, longitude: Double?, locationName: String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
    ) {

        Column {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(5.0.dp))
                    .background(Color(0xFFFFFFFF))
                    .width(70.dp)
                    .height(5.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = event.startTime,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp)
            )

            Surface(
                modifier = Modifier
                    .shadow(
                        elevation = 10.dp,
                        shape = MaterialTheme.shapes.bottomSheetMedium,
                        ambientColor = Color(0xFFC3C9D2).copy(alpha = 0.9f)
                    )
                    .padding(top = 12.dp)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.bottomSheetMedium)
                    .fillMaxHeight(0.92f)
            ) {

                Column(
                    modifier = Modifier.padding(vertical = 32.dp)
                ) {
                    Text(
                        text = event.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    if (event.locations.size > 1) {
                        BookmarkItem(
                            isFavorite = event.isFavorite,
                            id = event.id,
                            markAsFavorite = markAsFavorite,
                            iconTint = if (event.isFavorite) Color.White else MaterialTheme.colorScheme.primary,
                            backgroundColor = if (event.isFavorite) MaterialTheme.colorScheme.primary else Color.Transparent,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .padding(start = 24.dp)
                        )
                        MultipleLocationSection(
                            locations = event.locations,
                            onViewLocation = onViewLocation,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    } else {
                        SingleLocationSection(
                            event = event,
                            markAsFavorite = markAsFavorite,
                            onViewLocation = onViewLocation,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }

                    SpeakersSection(event.speakers)

                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .padding(top = 24.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        if (event.description.isNotEmpty()) {
                            AnnotatedHtmlStringWithLink(
                                htmlText = event.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Image(
                            painter = networkImagePainter(event.coverPhotoUrl),
                            contentDescription = event.name,
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.small)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SingleLocationSection(
    modifier: Modifier = Modifier,
    event: EventDetailsModel,
    markAsFavorite: (isFavorite: Boolean, event: String) -> Unit,
    onViewLocation: (latitude: Double?, longitude: Double?, locationName: String) -> Unit
) {
    Row(
        modifier = modifier.padding(top = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        if (event.locationName.isNotEmpty()) {
            Image(
                painter = painterResource(id = R.drawable.ic_loaction),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 6.dp)
            )
            Text(
                text = event.locationName,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 6.dp)
                    .padding(top = 8.dp)
                    .padding(end = 8.dp)
            )
        }

        BookmarkItem(
            isFavorite = event.isFavorite,
            id = event.id,
            markAsFavorite = markAsFavorite,
            iconTint = if (event.isFavorite) Color.White else MaterialTheme.colorScheme.primary,
            backgroundColor = if (event.isFavorite) MaterialTheme.colorScheme.primary else Color.Transparent
        )

        if (event.locationName.isNotEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(116.dp, 30.dp)
                    .bounceClick()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        onViewLocation(
                            event.latitude,
                            event.longitude,
                            event.locationName
                        )
                    }
                    .graphicsLayer(alpha = 30f, shape = RoundedCornerShape(7.dp))
                    .background(color = MaterialTheme.colorScheme.primary)


            ) {
                Text(
                    text = "View Location",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun MultipleLocationSection(
    modifier: Modifier = Modifier,
    locations: List<EventLocationsModel>,
    onViewLocation: (latitude: Double?, longitude: Double?, locationName: String) -> Unit
) {

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row {
            Text(
                text = "Available Locations",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .padding(start = 24.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.ic_loaction),
                contentDescription = null
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            items(locations) { location ->
                BasicCard(
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = location.name,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            modifier = Modifier
                                .padding(start = 6.dp)
                                .padding(top = 24.dp)
                                .padding(end = 8.dp)
                                .padding(bottom = 16.dp)
                        )
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .padding(horizontal = 24.dp)
                                .padding(bottom = 16.dp)
                                .size(116.dp, 30.dp)
                                .bounceClick()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onViewLocation(
                                        location.latitude,
                                        location.longitude,
                                        location.name
                                    )
                                }
                                .graphicsLayer(alpha = 30f, shape = RoundedCornerShape(7.dp))
                                .background(color = MaterialTheme.colorScheme.primary)


                        ) {
                            Text(
                                text = "View Location",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpeakersSection(
    speakers: List<SpeakerModel>
) {
    if (speakers.isNotEmpty()) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(top = 20.dp)
                .padding(horizontal = 24.dp)
        ) {
            items(speakers) { speaker ->
                if (speaker.name.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Image(
                            painter = if (speaker.avatar.isNotBlank())
                                networkImagePainter(speaker.avatar) else
                                painterResource(id = R.drawable.ic_user),
                            contentDescription = speaker.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                        )
                        Text(
                            text = speaker.name,
                            style = MaterialTheme.typography.displaySmall,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun EventDetailsPreview() {
    NFQSnapshotTestThemeForPreview {
        EventDetailsUI(
            event = EventDetailsModel(
                id = "1",
                name = "Pre-Summit Check in @Vietnam Saigon Office",
                description = "<p>Kicking off your morning at 8 AM with an hour of Muay Thai with our professional trainer . It's your personal dose of adrenaline, focus, and sweat. Let&rsquo;s learn the art of Thai boxing and get stronger together! I know, 8am is hard especially with Jet Lag. As we pay the trainer only for you - if you sign up you must be there! Otherwise I will personally send the Muay Thai Trainer to kick you out of the bed.<\\/p>",
                latitude = 0.0,
                longitude = 0.0,
                coverPhotoUrl = "",
                locationName = "Saigon, Vietnam Office ",
                isFavorite = false,
                startDateTime = LocalDateTime.now(),
                startTime = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("EEE, MMM d • HH:mm")
                ),
                locations = emptyList(),
                speakers = listOf(
                    SpeakerModel(
                        id = 1,
                        name = "John Doe",
                        avatar = ""
                    ),
                    SpeakerModel(
                        id = 2,
                        name = "Jane Doe",
                        avatar = ""
                    )
                )
            ),

            markAsFavorite = { _, _ -> },
            onViewLocation = { _, _, _ -> },
        )
    }
}

@Preview
@Composable
private fun EventDetailsWithMultipleLocationsPreview() {
    NFQSnapshotTestThemeForPreview {
        EventDetailsUI(
            event = EventDetailsModel(
                id = "1",
                name = "Pre-Summit Check in @Vietnam Saigon Office",
                description = "<p>Kicking off your morning at 8 AM with an hour of Muay Thai with our professional trainer . It's your personal dose of adrenaline, focus, and sweat. Let&rsquo;s learn the art of Thai boxing and get stronger together! I know, 8am is hard especially with Jet Lag. As we pay the trainer only for you - if you sign up you must be there! Otherwise I will personally send the Muay Thai Trainer to kick you out of the bed.<\\/p>",
                latitude = 0.0,
                longitude = 0.0,
                coverPhotoUrl = "",
                locationName = "Saigon, Vietnam Office ",
                isFavorite = false,
                startDateTime = LocalDateTime.now(),
                startTime = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("EEE, MMM d • HH:mm")
                ),
                locations = listOf(
                    EventLocationsModel(
                        id = 1,
                        name = "Location 1",
                        address = "Address 1",
                        latitude = 0.0,
                        longitude = 0.0
                    ),
                    EventLocationsModel(
                        id = 2,
                        name = "Location 2",
                        address = "Address 2",
                        latitude = 0.0,
                        longitude = 0.0
                    )
                ),
                speakers = listOf(
                    SpeakerModel(
                        id = 1,
                        name = "John Doe",
                        avatar = ""
                    ),
                    SpeakerModel(
                        id = 2,
                        name = "Jane Doe",
                        avatar = ""
                    )
                )
            ),

            markAsFavorite = { _, _ -> },
            onViewLocation = { _, _, _ -> },
        )
    }
}


@Preview
@Composable
private fun MultipleLocationSectionPreview() {
    NFQSnapshotTestThemeForPreview {
        MultipleLocationSection(
            locations = listOf(
                EventLocationsModel(
                    id = 1,
                    name = "Location 1",
                    address = "Address 1",
                    latitude = 0.0,
                    longitude = 0.0
                ),
                EventLocationsModel(
                    id = 2,
                    name = "Location 2",
                    address = "Address 2",
                    latitude = 0.0,
                    longitude = 0.0
                )
            ),
            onViewLocation = { _, _, _ -> }
        )
    }
}
