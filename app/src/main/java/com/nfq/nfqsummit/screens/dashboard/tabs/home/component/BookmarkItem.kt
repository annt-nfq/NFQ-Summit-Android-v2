package com.nfq.nfqsummit.screens.dashboard.tabs.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.nfq.nfqsummit.R
import com.nfq.nfqsummit.components.bounceClick
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview

@Composable
fun BookmarkItem(
    modifier: Modifier = Modifier,
    isFavorite: Boolean, id: String,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
    markAsFavorite: (isFavorite: Boolean, eventId: String) -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(30.dp)
            .bounceClick()
            .clip(RoundedCornerShape(7.dp))
            .clickable { markAsFavorite(!isFavorite, id) }
            .graphicsLayer(alpha = 30f, shape = RoundedCornerShape(7.dp))
            .background(color = backgroundColor)


    ) {
        Icon(
            painter = painterResource(
                id = if (isFavorite) R.drawable.ic_bookmark
                else R.drawable.ic_unbookmark
            ),
            contentDescription = null,
            modifier = Modifier.width(14.dp),
            tint = iconTint
        )
    }
}

class FavoritePreviewProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(true, false)
}

@Preview
@Composable
private fun BookmarkItemPreview(
    @PreviewParameter(FavoritePreviewProvider::class) isFavorite: Boolean
) {
    NFQSnapshotTestThemeForPreview {
        BookmarkItem(
            isFavorite = isFavorite,
            id = "1",
            markAsFavorite = { _, _ -> }
        )
    }
}

@Preview
@Composable
private fun BookmarkItemPreview2(
    @PreviewParameter(FavoritePreviewProvider::class) isFavorite: Boolean
) {
    NFQSnapshotTestThemeForPreview {
        BookmarkItem(
            isFavorite = isFavorite,
            id = "1",
            markAsFavorite = { _, _ -> },
            iconTint = Color.White,
            backgroundColor = MaterialTheme.colorScheme.primary
        )
    }
}