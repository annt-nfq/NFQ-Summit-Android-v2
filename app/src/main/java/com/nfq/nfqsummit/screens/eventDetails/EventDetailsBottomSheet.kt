@file:OptIn(ExperimentalMaterial3Api::class)

package com.nfq.nfqsummit.screens.eventDetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nfq.data.domain.model.SummitEvent
import com.nfq.nfqsummit.R
import com.nfq.nfqsummit.components.BasicModalBottomSheet
import com.nfq.nfqsummit.components.networkImagePainter
import com.nfq.nfqsummit.screens.dashboard.tabs.home.component.BookmarkItem
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview
import com.nfq.nfqsummit.ui.theme.bottomSheetLarge
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun EventDetailsBottomSheet(
    eventId: String,
    onViewLocation: (latitude: Double?, longitude: Double?) -> Unit,
    onDismissRequest: () -> Unit
) {
    val skipPartiallyExpanded by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded)
    val scope = rememberCoroutineScope()

    val viewModel: EventDetailsViewModel = hiltViewModel()

    LaunchedEffect(key1 = eventId) {
        viewModel.getEvent(eventId)
    }


    BasicModalBottomSheet(
        onDismissRequest = onDismissRequest,
        content = {
            viewModel.event?.let { event ->
                EventDetailsUI(
                    event = event,
                    markAsFavorite = { isFavorite, eventId ->
                        viewModel.markEventAsFavorite(isFavorite, eventId)
                    },
                    onViewLocation = { latitude, longitude ->
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                onViewLocation(latitude, longitude)
                            }
                        }
                    }
                )
            }
        }
    )
}


@Composable
private fun EventDetailsUI(
    event: SummitEvent,
    markAsFavorite: (isFavorite: Boolean, eventId: String) -> Unit = { _, _ -> },
    onViewLocation: (latitude: Double?, longitude: Double?) -> Unit,
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
                text = event.start.format(DateTimeFormatter.ofPattern("EEE, MMM d • HH:mm")),
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
                    .padding(top = 12.dp)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.bottomSheetLarge)
                    .fillMaxHeight(0.92f)
            ) {

                Column(
                    modifier = Modifier
                        .padding(vertical = 32.dp)
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = event.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    Row(
                        modifier = Modifier.padding(top = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_loaction),
                            contentDescription = null,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Text(
                            text = event.locationName ?: "-",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            modifier = Modifier
                                .padding(start = 6.dp)
                                .padding(end = 8.dp)
                        )
                        Spacer(modifier = Modifier.weight(1f))
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
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onViewLocation(event.latitude, event.longitude) }
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
                    Text(
                        text = event.description.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                    Image(
                        painter = networkImagePainter(event.coverPhotoUrl),
                        contentDescription = event.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                            .clip(MaterialTheme.shapes.small)
                            .aspectRatio(345f / 186f)
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
            event = SummitEvent(
                id = "1",
                name = "Pre-Summit Check in @Vietnam Saigon Office",
                start = LocalDateTime.now(),
                end = LocalDateTime.now().plusDays(1),
                description = "Complete this step to confirm your attendance and ensure a smooth entry on the day of the event. Please have your QR code ready for verification.",
                latitude = 0.0,
                longitude = 0.0,
                coverPhotoUrl = "",
                locationName = "Saigon, Vietnam",
                iconUrl = "",
                isConference = false,
                eventType = "",
                ordering = 0,
                speakerName = "",
                speakerPosition = "",
                isFavorite = false
            ),
            onViewLocation = { _, _ -> },
        )
    }
}
