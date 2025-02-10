package com.nfq.nfqsummit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview
import com.nfq.nfqsummit.ui.theme.boxShadow

@Composable
fun BasicCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    blurRadius: Dp = 30.dp,
    contentAlignment: Alignment = Alignment.TopStart,
    shadowColor: Color = Color(0xFF969696),
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .bounceClick()
            .boxShadow(
                color = shadowColor.copy(alpha = 0.15f),
                blurRadius = blurRadius,
                spreadRadius = 0.dp,
                offset = DpOffset(6.dp, 8.dp)
            )
            .clip(shape = shape)
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = contentAlignment,
        content = content
    )
}

@Composable
fun BasicCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    blurRadius: Dp = 30.dp,
    contentAlignment: Alignment = Alignment.TopStart,
    shadowColor: Color = Color(0xFF969696),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .boxShadow(
                color = shadowColor.copy(alpha = 0.08f),
                blurRadius = blurRadius,
                spreadRadius = 0.dp,
                offset = DpOffset(0.dp, 8.dp)
            )
            .clip(shape = shape)
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = contentAlignment,
        content = content
    )
}


@Preview
@Composable
private fun BasicCardPreview() {
    NFQSnapshotTestThemeForPreview {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            BasicCard(
                modifier = Modifier.size(200.dp)
            ) {}
        }
    }

}

