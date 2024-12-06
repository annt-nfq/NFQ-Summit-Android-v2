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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nfq.data.domain.model.SummitEvent
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
    titleMaxLines: Int = 3,
    roundedAvatar: Boolean = false,
    compact: Boolean = false,
    onEventClick: (SummitEvent) -> Unit = {},
) {
    val event = positionedEvent.event
    val topRadius =
        if (positionedEvent.splitType == SplitType.Start || positionedEvent.splitType == SplitType.Both) 0.dp else 10.dp
    val bottomRadius =
        if (positionedEvent.splitType == SplitType.End || positionedEvent.splitType == SplitType.Both) 0.dp else 10.dp

    val shape = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = topRadius,
        bottomEnd = bottomRadius,
        bottomStart = 0.dp,
    )
    Row(
        modifier = modifier
            .padding(vertical = 4.dp)
            .fillMaxSize()
            .bounceClick()
            .padding(
                bottom = if (positionedEvent.splitType == SplitType.End) 0.dp else 2.dp
            )
            .coloredShadow(
                MaterialTheme.colorScheme.secondary,
                alpha = 0.2f,
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
            modifier = Modifier.padding(start = 10.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
        ) {
            FlowRow(
                verticalArrangement = Arrangement.Center,
            ) {
                TagItem(
                    modifier = Modifier.padding(end = 10.dp),
                    tag = event.tag
                )
                Spacer(modifier = Modifier.weight(1f))
                val timeFormatter = DateTimeFormatter.ofPattern("h a")
                val startTime = event.start.format(timeFormatter).lowercase()
                val endTime = event.end.format(timeFormatter).lowercase()
                Text(
                    text = "$startTime - $endTime",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 10.dp, top = 4.dp)
                )
            }
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
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
                        maxLines = titleMaxLines,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            /*if (event.iconUrl != null)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(4.dp)
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .size(if (compact) 40.dp else 44.dp)
                            .clip(if (roundedAvatar) CircleShape else RectangleShape),
                        model = event.iconUrl,
                        contentDescription = null,
                        contentScale = if (roundedAvatar) ContentScale.Crop else ContentScale.Fit
                    )
                    if (event.speakerName != null)
                        Text(
                            text = event.speakerName ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Start,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                }*/
        }
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
                end = java.time.LocalTime.now(),
            ),
            modifier = Modifier.height(150.dp)
        )
    }
}