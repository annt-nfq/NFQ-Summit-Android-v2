package com.nfq.nfqsummit.screens.dashboard.tabs.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nfq.nfqsummit.components.AutoResizedText
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview

@Composable
fun TagItem(
    modifier: Modifier = Modifier,
    tag: String,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    containerColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
) {
    Box(
        modifier = modifier
            .background(
                color = containerColor,
                shape = RoundedCornerShape(8.dp),
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        AutoResizedText(
            text = tag,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium
            ),
            minFontSize = 9.sp,
            maxLines = 1,
            color = contentColor,
        )
    }
}

@Preview
@Composable
private fun TagItemPreview() {
    NFQSnapshotTestThemeForPreview {
        TagItem(
            tag = "\uD83D\uDCBC Summit",
            modifier = Modifier.padding(16.dp)
        )
    }
}