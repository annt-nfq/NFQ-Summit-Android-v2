package com.nfq.nfqsummit.screens.dashboard.tabs.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.nfq.nfqsummit.R
import com.nfq.nfqsummit.components.BasicCard
import com.nfq.nfqsummit.components.networkImagePainter
import com.nfq.nfqsummit.model.UpcomingEventUIModel
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview
import java.time.LocalDateTime

@Composable
fun UpcomingEventCard(
    modifier: Modifier = Modifier,
    uiModel: UpcomingEventUIModel,
    goToDetails: (eventId: String) -> Unit,
    markAsFavorite: (isFavorite: Boolean, eventId: String) -> Unit
) {
    BasicCard(
        modifier = modifier,
        onClick = { goToDetails(uiModel.id) }
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = networkImagePainter(uiModel.imageUrl),
                    contentDescription = uiModel.id,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .aspectRatio(221f / 131f)
                )
                BookmarkItem(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    isFavorite = uiModel.isFavorite,
                    id = uiModel.id,
                    markAsFavorite = markAsFavorite,
                )

                Text(
                    text = uiModel.date,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.background,
                        textAlign = TextAlign.Center,
                        lineHeight = 1.em,
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        ),
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .sizeIn(minWidth = 45.dp, minHeight = 45.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .graphicsLayer(alpha = 30f, shape = RoundedCornerShape(7.dp))
                        .background(color = MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                )

            }
            Text(
                text = uiModel.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 8.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp),
                        )
                        .padding(8.dp)
                ) {
                    Text(
                        text = uiModel.startAndEndTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.background,
                        fontWeight = FontWeight.Bold
                    )
                }
                TagItem(
                    modifier = Modifier.padding(start = 16.dp),
                    tag = uiModel.tag
                )
            }
        }
    }
}


@Preview
@Composable
private fun UpcomingEventPreview() {
    NFQSnapshotTestThemeForPreview {
        Surface {
            UpcomingEventCard(
                uiModel = UpcomingEventUIModel(
                    id = "1",
                    name = "Pre-Summit Check-in",
                    date = "10\nJun",
                    imageUrl = "",
                    isFavorite = false,
                    startAndEndTime = "10:00 - 12:00",
                    startDateTime = LocalDateTime.now(),
                    tag = "\uD83D\uDCBC Summit"
                ),
                goToDetails = { },
                markAsFavorite = { _, _ -> },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}