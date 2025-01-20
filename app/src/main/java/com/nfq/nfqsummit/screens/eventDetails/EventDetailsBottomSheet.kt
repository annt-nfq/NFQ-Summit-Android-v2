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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.nfq.nfqsummit.R
import com.nfq.nfqsummit.components.BasicAlertDialog
import com.nfq.nfqsummit.components.BasicModalBottomSheet
import com.nfq.nfqsummit.components.HtmlText
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
    val skipPartiallyExpanded by remember { mutableStateOf(false) }

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    var pendingAction = {}
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pendingAction.invoke()
        }
    }
    val viewModel: EventDetailsBottomSheetViewModel = hiltViewModel()

    LaunchedEffect(key1 = eventId) {
        viewModel.getEvent(eventId)
    }

    val permission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.POST_NOTIFICATIONS
        else Manifest.permission.ACCESS_NOTIFICATION_POLICY
    val notificationPermissionState = rememberPermissionState(permission = permission)

    var showAlarmRequest by remember { mutableStateOf(false) }
    var showNotificationRequest by remember { mutableStateOf(false) }

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

    BasicModalBottomSheet(
        onDismissRequest = onDismissRequest,
        content = {
            viewModel.event?.let { event ->
                EventDetailsUI(
                    event = event,
                    markAsFavorite = { isFavorite, _ ->
                        pendingAction = {
                            setUpScheduler(
                                context = context,
                                alarmManager = alarmManager,
                                setReminder = isFavorite,
                                startDateTime = event.startDateTime,
                                eventName = event.name,
                                eventId = event.id,
                                notificationPermissionState = notificationPermissionState,
                                showAlarmRequest = { showAlarmRequest = it },
                                showNotificationRequest = { showNotificationRequest = it },
                                markEventAsFavorite = viewModel::markEventAsFavorite,
                                permissionLauncher = { permissionLauncher.launch(permission) },
                            )
                        }
                        pendingAction()
                    },
                    onViewLocation = { latitude, longitude, locationName ->
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
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
}

fun scheduleAlarm(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        context.startActivity(
            Intent(
                Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                Uri.parse("package:${context.packageName}")
            )
        )
    } else {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // For Android 30 and below, you can use setExactAndAllowWhileIdle
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                pendingIntent
            )
        }
    }
}

fun setUpScheduler(
    context: Context,
    alarmManager: AlarmManager,
    setReminder: Boolean,
    startDateTime: LocalDateTime,
    eventName: String,
    eventId: String,
    notificationPermissionState: PermissionState,
    showAlarmRequest: (Boolean) -> Unit,
    showNotificationRequest: (Boolean) -> Unit,
    markEventAsFavorite: (isFavorite: Boolean, eventId: String) -> Unit = { _, _ -> },
    updateNotificationSetting: () -> Unit = {},
    permissionLauncher: () -> Unit = {}
) {

    val hasAlarmPermission: Boolean = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
        // For Android 30 and below
        true
    } else {
        // For Android 31+
        alarmManager.canScheduleExactAlarms()
    }

    if (!hasAlarmPermission) {
        showAlarmRequest(true)
        return
    }

    if (!notificationPermissionState.status.isGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (notificationPermissionState.status.shouldShowRationale) {
            showNotificationRequest(true)
        } else {
            permissionLauncher.invoke()
        }
        return
    }
    updateNotificationSetting()

    if (setReminder) {
        markEventAsFavorite(true, eventId)
        createNotificationChannel(context)
        scheduleNotification(
            context,
            startDateTime.minusMinutes(30),
            eventName,
            "This event is starting in 30 minutes",
            eventId
        )
    } else {
        markEventAsFavorite(false, eventId)
        cancelNotification(context, eventId)
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
                    modifier = Modifier
                        .padding(vertical = 32.dp)
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState())

                ) {
                    Text(
                        text = event.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    Row(
                        modifier = Modifier.padding(top = 16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_loaction),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .padding(top = 8.dp)
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
                        BookmarkItem(
                            isFavorite = event.isFavorite,
                            id = event.id,
                            markAsFavorite = markAsFavorite,
                            iconTint = Color.White,
                            backgroundColor = MaterialTheme.colorScheme.primary
                        )
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

                    Text(
                        text = "Please show your QR code to our staff\nfor check-in",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                    HtmlText(
                        html = event.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                    Image(
                        painter = networkImagePainter(event.coverPhotoUrl),
                        contentDescription = event.name,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                            .clip(MaterialTheme.shapes.small)
                    )
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
                )
            ),

            markAsFavorite = { _, _ -> },
            onViewLocation = { _, _, _ -> },
        )
    }
}
