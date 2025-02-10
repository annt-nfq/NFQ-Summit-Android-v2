package com.nfq.nfqsummit.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview
import timber.log.Timber

@Composable
fun SegmentedControl(
    items: List<String>,
    selectedIndex: Int,
    containerColor: Color = Color(0xFFEEEEEE),
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    onItemSelection: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var itemWidths by remember { mutableStateOf(List(items.size) { 0.dp }) }
    val itemHeight = 40.dp
    fun calculateIndicatorOffset(selectedIndex: Int): Dp {
        return itemWidths.take(selectedIndex).fold(0.dp) { acc, dp -> acc + dp }
    }

    val indicatorOffset by animateDpAsState(
        targetValue = calculateIndicatorOffset(selectedIndex),
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f), label = ""
    )

    val indicatorWidth by animateDpAsState(
        targetValue = itemWidths.getOrNull(selectedIndex) ?: 0.dp,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f), label = ""
    )
    val shape = RoundedCornerShape(62.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(containerColor)
            .height(itemHeight)
    ) {
        // Animated Indicator
        Box(
            Modifier
                .offset { IntOffset(indicatorOffset.roundToPx(), 0) }
                .width(indicatorWidth)
                .height(itemHeight)
                .clip(shape)
                .background(selectedColor)
        )

        // Items
        Row(modifier = Modifier.fillMaxWidth()) {
            items.forEachIndexed { index, item ->
                Box(
                    modifier = Modifier
                        .weight(if (index == 0) 1f else 1.3f)
                        .height(itemHeight)
                        .clip(shape)
                        .clickable { onItemSelection(index) }
                        .onGloballyPositioned { coordinates ->
                            itemWidths = itemWidths
                                .toMutableList()
                                .also { list ->
                                    list[index] = with(density) { coordinates.size.width.toDp() }
                                }
                            Timber.i("Item Widths: $itemWidths")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        color = if (selectedIndex == index) Color.White else MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun SegmentedControlPreview() {
    NFQSnapshotTestThemeForPreview {
        SegmentedControl(
            items = listOf(
                "Thailand \uD83C\uDDF9\uD83C\uDDED",
                "Vietnam \uD83C\uDDFB\uD83C\uDDF3 "
            ),
            selectedIndex = 0,
            onItemSelection = { }
        )
    }
}