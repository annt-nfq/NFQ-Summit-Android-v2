package com.nfq.nfqsummit.screens.dashboard.tabs.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nfq.nfqsummit.components.BasicCard
import com.nfq.nfqsummit.components.networkImagePainter
import com.nfq.nfqsummit.model.SavedEventUIModel
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview

@Composable
fun SavedEventCard(
    modifier: Modifier = Modifier,
    uiModel: SavedEventUIModel,
    goToEventDetails: (eventId: String) -> Unit
) {
    BasicCard(
        modifier = modifier,
        onClick = { goToEventDetails(uiModel.id) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Image(
                painter = networkImagePainter(uiModel.imageUrl),
                contentDescription = uiModel.id,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .size(83.35.dp, 68.dp)
            )

            Column(
                modifier = Modifier.padding(start = 18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uiModel.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    TagItem(
                        tag = uiModel.category.tag,
                        containerColor = Color(uiModel.category.containerColor),
                        contentColor = Color(uiModel.category.contentColor),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Text(
                    text = uiModel.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun SavedEventCardPreview() {
    NFQSnapshotTestThemeForPreview {
        SavedEventCard(
            modifier = Modifier.padding(16.dp),
            uiModel = SavedEventUIModel(
                id = "1",
                imageUrl = "",
                name = "E-Commerce Conference - Thai Market",
                date = "Wed, Jun 28 •17:00"
            ),
            goToEventDetails = {}
        )
    }
}