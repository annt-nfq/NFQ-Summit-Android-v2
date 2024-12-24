package com.nfq.nfqsummit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
    Row(
        modifier = modifier
            .padding(vertical = 2.dp)
            .fillMaxSize()
            .bounceClick()
            .padding(
                bottom = if (positionedEvent.splitType == SplitType.End) 0.dp else 2.dp
            )
            .coloredShadow(
                MaterialTheme.colorScheme.secondary,
                alpha = 0.1f,
                shadowRadius = 16.dp,
                offsetX = 2.dp,
                offsetY = 8.dp,
            )
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = shape
            )
            .clip(shape = shape)
            .clickable { onEventClick(event) }
    ) {
        /* val color = when (event.ordering) {
             1 -> Color(0xFFEF5350) // Red
             2 -> Color(0xFF66BB6A) // Green
             3 -> Color(0xFF26C6DA) // Blue
             else -> Color(0xFFFFE1CC)      // Default color
         }*/
        val color = Color(0xFFFFE1CC)

        Box(
            modifier = Modifier
                .width(9.dp)
                .fillMaxHeight()
                .background(color)
        )

        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(
                    start = 8.dp,
                    end = if (eventSize == EventSize.Large) 8.dp else 0.dp
                )
                .padding(
                    vertical = if (eventSize == EventSize.Large) 16.dp else 4.dp
                )
        ) {
            FlowRow(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                if (eventSize == EventSize.Large) {
                    TagItem(
                        tag = event.tag,
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .padding(bottom = 12.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }

                val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
                val startTime = event.start.format(timeFormatter).lowercase().replace(":00", "")
                val endTime = event.end.format(timeFormatter).lowercase().replace(":00", "")
                Text(
                    text = "$startTime - $endTime",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Visible
                )
            }
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.widthIn(
                        min = 0.dp,
                        max = 250.dp
                    )
                ) {
                    Text(
                        text = event.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = if (compact) 14.sp else 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = if (eventSize == EventSize.Small) 1 else titleMaxLines,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                if (event.iconUrl != null && eventSize != EventSize.Small)
                    AsyncImage(
                        modifier = Modifier
                            .size(21.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE6E6E6)),
                        model = event.iconUrl,
                        contentDescription = "speaker profile",
                        contentScale = ContentScale.Crop
                    )
                if (event.speakerName != null)
                    Text(
                        text = event.speakerName ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        modifier = Modifier
                            .fillMaxWidth(),
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
                    name = "Event Name",
                    iconUrl = "https://www.example.com/image.jpg",
                    speakerName = "Speaker Name",
                    speakerProfileUrl = "https://www.example.com/speaker.jpg",
                    ordering = 4,
                    id = "1",
                    start = java.time.LocalDateTime.now(),
                    end = java.time.LocalDateTime.now().plusHours(2),
                    isFavorite = false,
                    tag = "\uD83D\uDCBC Summit",
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
                    name = "Event Name",
                    iconUrl = "https://www.example.com/image.jpg",
                    speakerName = "Speaker Name",
                    speakerProfileUrl = "https://www.example.com/speaker.jpg",
                    ordering = 4,
                    id = "1",
                    start = java.time.LocalDateTime.now(),
                    end = java.time.LocalDateTime.now().plusHours(2),
                    isFavorite = false,
                    tag = "\uD83D\uDCBC Summit",
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