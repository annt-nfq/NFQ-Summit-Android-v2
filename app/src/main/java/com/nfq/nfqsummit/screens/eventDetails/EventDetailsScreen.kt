@file:OptIn(ExperimentalMaterial3Api::class)

package com.nfq.nfqsummit.screens.eventDetails

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.nfq.data.domain.model.SummitEvent
import com.nfq.nfqsummit.components.shimmerBrush
import com.nfq.nfqsummit.notification.AlarmReceiver
import com.nfq.nfqsummit.ui.theme.NFQOrange
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private val headerHeight = 360.dp
private val toolbarHeight = 56.dp


fun createNotificationChannel(context: Context) {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    val channel = NotificationChannel("reminder", "Reminder", NotificationManager.IMPORTANCE_HIGH)
    val manager = context.getSystemService(NotificationManager::class.java) as NotificationManager
    manager.createNotificationChannel(channel)
}

fun scheduleNotification(
    context: Context,
    dateTime: LocalDateTime,
    title: String,
    body: String,
    eventId: String
) {
    val intent = Intent(context, AlarmReceiver::class.java).apply {
        putExtra("title", title)
        putExtra("body", body)
        putExtra("eventId", eventId)
    }


    val id = BigInteger(eventId.encodeToByteArray()).toInt()

    val pendingIntent = PendingIntent.getBroadcast(
        context, id, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager


    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        dateTime.toInstant(ZoneOffset.ofHours(7)).toEpochMilli(),
        pendingIntent
    )
}

fun cancelNotification(context: Context, eventId: String) {
    val intent = Intent(context, AlarmReceiver::class.java)
    val id = BigInteger(eventId.encodeToByteArray()).toInt()
    val pendingIntent = PendingIntent.getBroadcast(
        context, id, intent,
        PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(pendingIntent)
}

@Composable
fun CollapsingToolbar(
    event: SummitEvent,
    goBack: () -> Unit = {},
) {

    val scroll: ScrollState = rememberScrollState(0)
    val headerHeightPx = with(LocalDensity.current) { headerHeight.toPx() }
    val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.toPx() }

    Box(modifier = Modifier.fillMaxSize()) {
        Body(
            scroll = scroll,
            event = event,
            modifier = Modifier.fillMaxSize()
        )
        Header(
            scroll = scroll,
            headerHeightPx = headerHeightPx,
            imageUrl = event.coverPhotoUrl,
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight),
            onClose = {
                goBack()
            }
        )
        Toolbar(
            onClose = {
                goBack()
            },
            scroll = scroll,
            headerHeightPx = headerHeightPx,
            toolbarHeightPx = toolbarHeightPx
        )
//        Title(
//            scroll = scroll,
//            title = event.name
//        )
    }
}

@Composable
fun Header(
    modifier: Modifier = Modifier,
    scroll: ScrollState,
    headerHeightPx: Float,
    imageUrl: String?,
    onClose: () -> Unit = {},
) {

    Box(modifier = modifier
        .fillMaxWidth()
        .height(headerHeight)
        .graphicsLayer {
            translationY = -scroll.value.toFloat() / 2f // Parallax effect
        }
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = BrushPainter(
                Brush.linearGradient(
                    listOf(
                        Color(color = 0xFFFFFFFF),
                        Color(color = 0xFF949494),
                    )
                )
            ),
        )

        Box(
            Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            Color.Transparent,
                            MaterialTheme.colorScheme.background
                        ),
//                        startY = 1 * headerHeightPx / 4 // to wrap the title only
                    )
                )
        )

        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(horizontal = 16.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null
            )
        }

    }
}

@Composable
fun Body(
    scroll: ScrollState,
    event: SummitEvent,
    modifier: Modifier = Modifier
) {

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.background)
            .verticalScroll(scroll)
            .padding(16.dp)
    ) {
        Spacer(Modifier.height(headerHeight))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            val hourFormatter = DateTimeFormatter.ofPattern("HH:mm")
            Column(modifier = Modifier.weight(1f)) {
                if (event.eventType != null)
                    Text(
                        text = event.eventType!!,
                        style = MaterialTheme.typography.labelLarge.copy(
//                            color = LabelGrey
                        ),
                    )
                Text(
                    text = event.name,
                    style = MaterialTheme.typography.displayMedium,
                )
                Text(
                    text = event.start.toLocalTime()
                        .format(hourFormatter) + " - " +
                            event.end.toLocalTime().format(hourFormatter),
                    style = MaterialTheme.typography.labelLarge.copy(
//                        color = LabelGrey
                    ),
                )
            }

            Column(
                modifier = Modifier
                    .size(75.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val monthFormatter = DateTimeFormatter.ofPattern("MMM")
                val dayFormatter = DateTimeFormatter.ofPattern("dd")
                Text(
                    text = event.start.toLocalDate().format(monthFormatter),
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                )
                Text(
                    text = event.start.toLocalDate().format(dayFormatter),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 28.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (!event.speakerName.isNullOrEmpty() && !event.speakerPosition.isNullOrEmpty())
            Text(
                modifier = androidx.compose.ui.Modifier
                    .background(NFQOrange, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                text = if (event.ordering == 1) "Business" else if (event.ordering == 2) "Product Development" else "Tech",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )
        Spacer(modifier = Modifier.height(16.dp))
        if (!event.speakerName.isNullOrEmpty() && !event.speakerPosition.isNullOrEmpty()) {
            Text(
                text = "Speaker",
                style = MaterialTheme.typography.displaySmall,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                    model = event.iconUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = BrushPainter(
                        Brush.linearGradient(
                            listOf(
                                Color(color = 0xFFFFFFFF),
                                Color(color = 0xFFDDDDDD),
                            )
                        )
                    ),
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = event.speakerName!!,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.speakerPosition!!,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        Text(
            text = event.description ?: "",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Justify,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
        )
        Spacer(modifier = Modifier.height(140.dp))
    }
}

@Composable
fun Toolbar(
    modifier: Modifier = Modifier,
    scroll: ScrollState,
    headerHeightPx: Float,
    toolbarHeightPx: Float,
    onClose: () -> Unit,
) {
    val toolbarBottom by remember {
        mutableStateOf(headerHeightPx - toolbarHeightPx)
    }

    val showToolbar by remember {
        derivedStateOf {
            scroll.value >= toolbarBottom
        }
    }

    AnimatedVisibility(
        modifier = modifier,
        visible = showToolbar,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300))
    ) {
        TopAppBar(
            modifier = modifier.background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        Color.Transparent,
                    ),
                )
            ),
            actions = {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .padding(24.dp)
                        .size(24.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            },
            title = {},
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )
    }
}


@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun EventDetailsScreen(
    eventId: String?,
    goBack: () -> Unit,
    viewModel: EventDetailsViewModel = hiltViewModel()
) {
    val showAlarmRequest = remember { mutableStateOf(false) }
    val showNotificationRequest = remember { mutableStateOf(false) }

    val context = LocalContext.current

    val notificationPermissionState =
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

    val window = (LocalView.current.context as Activity).window
    window.statusBarColor = Color.Transparent.toArgb()
    WindowCompat.setDecorFitsSystemWindows(window, false)

    LaunchedEffect(viewModel) {
        launch {
            viewModel.getEvent(eventId ?: "")
        }
    }

    if (showNotificationRequest.value) {
        AlertDialog(
            onDismissRequest = { showNotificationRequest.value = false },
            dismissButton = {
                TextButton(
                    onClick = {
                        showNotificationRequest.value = false
                    }
                ) {
                    Text(text = "Deny")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showNotificationRequest.value = false
                        context.startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:${context.packageName}")
                            ).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                        )
                    }
                ) {
                    Text(text = "Allow")
                }
            },
            title = {
                Text(text = "Allow notification permission")
            },
            text = {
                Text(text = "This app requires notification permission to show you reminders of your saved events")
            },
        )
    }
    if (showAlarmRequest.value) {
        AlertDialog(
            onDismissRequest = { showAlarmRequest.value = false },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAlarmRequest.value = false
                    }
                ) {
                    Text(text = "Deny")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAlarmRequest.value = false
                        context.startActivity(
                            Intent(
                                Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                                Uri.parse("package:${context.packageName}")
                            )
                        )
                    }
                ) {
                    Text(text = "Allow")
                }
            },
            title = {
                Text(text = "Allow alarm permission")
            },
            text = {
                Text(text = "This app requires alarm permission to show you reminders of your saved events")
            },
        )
    }

    if (viewModel.event != null) {
        EventDetailsUI(
            event = viewModel.event!!,
            onFavoriteClick = { event ->
                val alarmManager =
                    context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (Build.VERSION.SDK_INT > 30 && !alarmManager.canScheduleExactAlarms()) {
                    showAlarmRequest.value = true
                } else {
                    if (notificationPermissionState.status.isGranted) {
                        viewModel.isBookmarked = !viewModel.isBookmarked
                        if (viewModel.isBookmarked) {
                            viewModel.insertSavedEvent(viewModel.event!!.id)
                            createNotificationChannel(context)
                            scheduleNotification(
                                context,
                                event.start.minusMinutes(15),
                                event.name,
                                "This event is starting in 15 minutes",
                                event.id
                            )
                        } else {
                            viewModel.deleteSavedEvent(event.id)
                            cancelNotification(context, event.id)
                        }
                    } else {
                        if (notificationPermissionState.status.shouldShowRationale) {
                            showNotificationRequest.value = true
                        } else {
                            notificationPermissionState.launchPermissionRequest()
                        }
                    }
                }
            },
            goBack = goBack,
            isBookmarked = viewModel.isBookmarked
        )
    } else ShimmeringEventDetails()
}


@Composable
fun EventDetailsUI(
    event: SummitEvent,
    goBack: () -> Unit,
    onFavoriteClick: (event: SummitEvent) -> Unit = {},
    isBookmarked: Boolean,
) {
    val sheetScaffoldState = rememberBottomSheetScaffoldState()
    val context = LocalContext.current

    BottomSheetScaffold(
        scaffoldState = sheetScaffoldState,
//            sheetSwipeEnabled = false,
        sheetPeekHeight = 128.dp,
        sheetDragHandle = null,
        sheetContent = {
            Row(
                modifier = Modifier.padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    modifier = Modifier
                        .weight(0.8f)
                        .height(56.dp),
                    onClick = {
                        val geoUri =
                            "http://maps.google.com/maps?q=loc:" +
                                    "${event.latitude}," +
                                    "${event.longitude} " +
                                    "(${event.locationName})"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
                        context.startActivity(intent)

                    }) {
                    Text(text = "Get directions".uppercase())
                }
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(
                    modifier = Modifier
                        .weight(0.2f)
                        .size(40.dp),
                    onClick = {
                        onFavoriteClick(event)
                    }) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null
                    )
                }
            }
        }) {
        CollapsingToolbar(
            event = event,
            goBack = goBack
        )
    }
}

@Preview
@Composable
fun EventDetailsUIPreview() {
    NFQSnapshotTestThemeForPreview {
        EventDetailsUI(
            event = SummitEvent(
                "1",
                "Event Title",
                LocalDateTime.of(2022, 1, 1, 1, 0),
                LocalDateTime.of(2022, 1, 1, 2, 0),
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                0.0, 0.0,
                "",
                "Bangkok, Thailand",
                iconUrl = "",
                speakerName = "Speaker Name",
                speakerPosition = "Speaker Position",
            ),
            goBack = { },
            isBookmarked = true
        )
    }
}

@Composable
fun ShimmeringEventDetails() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .padding(16.dp)
            .background(
                shimmerBrush(
                    targetValue = 1300f,
                    showShimmer = true
                ),
                shape = RoundedCornerShape(16.dp)
            )
    )
}