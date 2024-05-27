package com.nfq.nfqsummit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nfq.nfqsummit.model.PositionedEvent
import com.nfq.nfqsummit.model.SplitType
import com.nfq.nfqsummit.ui.theme.NFQOrange
import com.nfq.nfqsummit.ui.theme.OnSecondaryContainerLight
import com.nfq.nfqsummit.ui.theme.coloredShadow

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BasicEvent(
    positionedEvent: PositionedEvent,
    modifier: Modifier = Modifier,
    titleMaxLines: Int = 3,
    roundedAvatar: Boolean = false,
    compact: Boolean = false,
) {
    val event = positionedEvent.event
    val topRadius =
        if (positionedEvent.splitType == SplitType.Start || positionedEvent.splitType == SplitType.Both) 0.dp else 4.dp
    val bottomRadius =
        if (positionedEvent.splitType == SplitType.End || positionedEvent.splitType == SplitType.Both) 0.dp else 4.dp
    Row(
        modifier = modifier
            .padding(vertical = 4.dp)
            .fillMaxSize()
            .padding(
                end = 2.dp,
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
                color = when (event.ordering) {
                    1 -> Color(0xFFFFCDD2)
                    2 -> Color(0xFFC8E6C9)
                    3 -> Color(0xFFB2EBF2)
                    else -> Color(0xFFFBE9E7)
                },
                shape = RoundedCornerShape(
                    topStart = topRadius,
                    topEnd = topRadius,
                    bottomEnd = bottomRadius,
                    bottomStart = bottomRadius,
                )
            )
            .clip(
                RoundedCornerShape(
                    topStart = topRadius,
                    topEnd = topRadius,
                    bottomEnd = bottomRadius,
                    bottomStart = bottomRadius,
                )
            )
    ) {
        when (event.ordering) {
            1 -> Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(Color(0xFFEF5350))
            )

            2 -> Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(Color(0xFF66BB6A))
            )

            3 -> Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(Color(0xFF26C6DA))
            )

            else -> Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(NFQOrange)
            )
        }
        Column(verticalArrangement = Arrangement.SpaceBetween) {
            FlowRow(
                modifier = Modifier
                    .padding(if (compact) 4.dp else 8.dp)
                    .fillMaxWidth(),
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
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = if (compact) 16.sp else 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = titleMaxLines,
                        overflow = TextOverflow.Ellipsis,
                        color = OnSecondaryContainerLight
                    )
                }
            }
            if (event.iconUrl != null)
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
                        contentScale = if (roundedAvatar) ContentScale.Crop else ContentScale.Fit,
                        placeholder = BrushPainter(
                            Brush.linearGradient(
                                listOf(
                                    Color(color = 0xFFFFFFFF),
                                    Color(color = 0xFFDDDDDD),
                                )
                            )
                        ),
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
                            color = OnSecondaryContainerLight
                        )
                }
        }
    }
}