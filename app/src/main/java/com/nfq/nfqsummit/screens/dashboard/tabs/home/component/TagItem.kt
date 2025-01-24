package com.nfq.nfqsummit.screens.dashboard.tabs.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
    isTechRock: Boolean = false,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    containerColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
) {
    Box(
        modifier = modifier
            .widthIn(min = 80.dp)
            .background(
                color = containerColor,
                shape = RoundedCornerShape(8.dp),
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        if (isTechRock) {
            val (icon, name) = tag.split(" ").let {
                Pair(it.first(), it.drop(1).joinToString(" "))
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = icon,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1,
                    color = contentColor,
                )
                AutoResizedText(
                    text = name,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = contentColor,
                    minFontSize = 9.sp,
                )
            }

        } else {
            AutoResizedText(
                text = tag,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                minFontSize = 10.sp,
                maxLines = 1,
                color = contentColor,
            )
        }
    }
}

@Preview
@Composable
private fun TagItemPreview() {
    NFQSnapshotTestThemeForPreview {
        TagItem(
            tag = "\uD83D\uDCC8 Scaling Business",
            containerColor = Color(0xFFE5EDFF),
            contentColor = Color(0xFF42389D),
            isTechRock = true,
            modifier = Modifier
                .padding(16.dp)
                .widthIn(min = 80.dp)
                .width(60.dp)
        )
    }
}