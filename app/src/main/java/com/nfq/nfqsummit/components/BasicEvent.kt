package com.nfq.nfqsummit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nfq.data.domain.model.SummitEvent
import com.nfq.nfqsummit.model.EventSize
import com.nfq.nfqsummit.model.PositionedEvent
import com.nfq.nfqsummit.model.SplitType
import com.nfq.nfqsummit.screens.dashboard.tabs.home.component.TagItem
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview
import com.nfq.nfqsummit.ui.theme.coloredShadow
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BasicEvent(
    positionedEvent: PositionedEvent,
    modifier: Modifier = Modifier,
    titleMaxLines: Int = 2,
    roundedAvatar: Boolean = false,
    compact: Boolean = false,
    onEventClick: (SummitEvent) -> Unit = {},
) {
    var eventHeight by remember { mutableStateOf(150.dp) }
    val event = positionedEvent.event
    val eventSize = positionedEvent.getEventSize()
    val topRadius =
        if (positionedEvent.splitType == SplitType.Start || positionedEvent.splitType == SplitType.Both) 0.dp else 16.dp
    val bottomRadius =
        if (positionedEvent.splitType == SplitType.End || positionedEvent.splitType == SplitType.Both) 0.dp else 16.dp

    val shape = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = topRadius,
        bottomEnd = bottomRadius,
        bottomStart = 0.dp,
    )

    LaunchedEffect(eventHeight) {
        Timber.i("BasicEvent ${event.name}-$eventHeight")
    }
    Row(
        modifier = modifier
            .fillMaxSize()
            .bounceClick()
            .border(
                width = 0.1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                shape = shape
            )
            .padding(bottom = if (positionedEvent.splitType == SplitType.End) 0.dp else 1.dp)
            .coloredShadow(
                color = MaterialTheme.colorScheme.secondary,
                alpha = 0.08f,
                shadowRadius = 30.dp,
                offsetX = 2.dp,
                offsetY = 6.dp,
            )
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = shape
            )
            .clip(shape = shape)
            .clickable { onEventClick(event) }
            .onGloballyPositioned { coordinates ->
                eventHeight = coordinates.size.height.dp
            }
    ) {
        val contentColor = Color(event.category.contentColor)
        val containerColor = Color(event.category.containerColor)

        Box(
            modifier = Modifier
                .width(9.dp)
                .fillMaxHeight()
                .background(containerColor)
        )

        val (top, end) = when (eventSize) {
            EventSize.XSmall -> 6.dp to 0.dp
            EventSize.Small -> 8.dp to 0.dp
            EventSize.Medium -> 14.dp to 0.dp
            else -> 14.dp to 16.dp
        }
        FlowColumn(
            modifier = Modifier
                .padding(start = 8.dp)
                .padding(top = top)
        ) {

            FlowRow(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = if (eventSize == EventSize.Large) 6.dp else 4.dp)
            ) {
                TagItem(
                    tag = event.category.tag,
                    containerColor = containerColor,
                    contentColor = contentColor,
                    modifier = Modifier.padding(end = 10.dp)
                )
                Spacer(modifier = Modifier.weight(1f))

                if ((eventSize != EventSize.Small && eventSize != EventSize.XSmall) || eventHeight > 580.dp) {
                    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                    val startTime = event.start.format(timeFormatter).lowercase()
                    val endTime = event.end.format(timeFormatter).lowercase()
                    AutoResizedText(
                        text = "$startTime - $endTime",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        minFontSize = 10.sp,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp, end = 16.dp)
                    )
                }
            }
            Text(
                text = event.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    lineHeight = if (eventSize == EventSize.Medium) 16.sp else 19.sp,
                ),
                maxLines = when {
                    eventSize == EventSize.Large || eventHeight in 439.dp..599.dp -> 4
                    eventHeight > 589.dp -> 5
                    else -> 3
                },
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(end = end)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(top = if (eventSize == EventSize.Small) 2.dp else 6.dp)
            ) {
                if (event.speakerAvatar.isNotBlank())
                    AsyncImage(
                        modifier = Modifier
                            .size(21.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE6E6E6)),
                        model = event.speakerAvatar,
                        contentDescription = "speaker profile",
                        contentScale = ContentScale.Crop
                    )
                if (event.speakerName.isNotBlank())
                    Text(
                        text = event.speakerName,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
            }
        }
    }
}

class ColTotalPreviewParameter : PreviewParameterProvider<Int> {
    override val values: Sequence<Int>
        get() = sequenceOf(
            1,
            2
        )
}

@Preview
@Composable
private fun BasicEventPreview(
    @PreviewParameter(ColTotalPreviewParameter::class) colTotal: Int
) {
    NFQSnapshotTestThemeForPreview {
        BasicEvent(
            positionedEvent = PositionedEvent(
                event = SummitEvent(
                    name = "How to migrate from an Android codebase to KMMHow to migrate from an Android codebase to KMM",
                    iconUrl = "https://www.example.com/image.jpg",
                    speakerName = "Speaker Name",
                    speakerAvatar = "https://www.example.com/speaker.jpg",
                    ordering = 4,
                    id = "1",
                    start = java.time.LocalDateTime.now(),
                    end = java.time.LocalDateTime.now().plusHours(2),
                    isFavorite = false
                ),
                splitType = SplitType.None,
                date = LocalDate.now(),
                start = java.time.LocalTime.now(),
                end = java.time.LocalTime.now().plusHours(1),
                colTotal = colTotal
            ),
            modifier = Modifier.height(150.dp)
        )
    }
}

@Preview
@Composable
private fun BasicEventPreview() {
    NFQSnapshotTestThemeForPreview {
        BasicEvent(
            positionedEvent = PositionedEvent(
                event = SummitEvent(
                    name = "How to migrate from an Android codebase to KMMHow to migrate from an Android codebase to KMM",
                    iconUrl = "https://www.example.com/image.jpg",
                    speakerName = "Speaker Name",
                    speakerAvatar = "https://www.example.com/speaker.jpg",
                    ordering = 4,
                    id = "1",
                    start = java.time.LocalDateTime.now(),
                    end = java.time.LocalDateTime.now().plusHours(2),
                    isFavorite = false
                ),
                splitType = SplitType.None,
                date = LocalDate.now(),
                start = java.time.LocalTime.now(),
                end = java.time.LocalTime.now().plusMinutes(30),
                colTotal = 1
            ),
            modifier = Modifier.height(150.dp)
        )
    }
}