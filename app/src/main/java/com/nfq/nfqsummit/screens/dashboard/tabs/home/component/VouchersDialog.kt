package com.nfq.nfqsummit.screens.dashboard.tabs.home.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nfq.data.domain.model.VoucherModel
import com.nfq.nfqsummit.R
import com.nfq.nfqsummit.components.BasicCard
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview

@Composable
fun VouchersDialog(
    attendeeName: String,
    vouchers: Map<String, List<VoucherModel>> = emptyMap(),
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        VouchersContent(
            attendeeName = attendeeName,
            vouchers = vouchers,
            onDismissRequest = onDismissRequest
        )
    }
}

@Composable
private fun VouchersContent(
    attendeeName: String,
    vouchers: Map<String, List<VoucherModel>>,
    onDismissRequest: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier

            .fillMaxSize()
    ) {
        IconButton(
            onClick = onDismissRequest,
            modifier = Modifier
                .padding(vertical = 17.dp)
                .padding(end = 18.dp)
                .size(24.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
                .padding(2.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = "ic_close",
                modifier = Modifier.size(24.dp)
            )
        }

        val scrollState = rememberLazyListState()

        LazyColumn(
            state = scrollState,
            contentPadding = PaddingValues(bottom = 50.dp, start = 18.dp, end = 18.dp),
            modifier = Modifier
                .navigationBarsPadding()
                .simpleVerticalScrollbar(scrollState)
        ) {
            vouchers.forEach { (date, list) ->
                item {
                    BasicCard(
                        shape = RoundedCornerShape(9.dp),
                        modifier = Modifier.padding(bottom = 6.dp, top = 8.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                        ) {
                            Text(
                                text = date,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                items(list) {
                    VoucherCard(
                        attendeeName = attendeeName,
                        model = it
                    )
                }
            }
        }
    }
}

@Composable
fun Modifier.simpleVerticalScrollbar(
    state: LazyListState,
    width: Dp = 4.dp,
    color: Color = MaterialTheme.colorScheme.primary
): Modifier {
    val targetAlpha = if (state.isScrollInProgress) 1f else 0f
    val duration = if (state.isScrollInProgress) 150 else 500

    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = duration), label = ""
    )

    return drawWithContent {
        drawContent()

        val firstVisibleElementIndex = state.layoutInfo.visibleItemsInfo.firstOrNull()?.index
        val needDrawScrollbar = state.isScrollInProgress || alpha > 0.0f

        // Draw scrollbar if scrolling or if the animation is still running and lazy column has content
        if (needDrawScrollbar && firstVisibleElementIndex != null) {
            val elementHeight = this.size.height / state.layoutInfo.totalItemsCount
            val scrollbarOffsetY = firstVisibleElementIndex * elementHeight
            val scrollbarHeight = state.layoutInfo.visibleItemsInfo.size * elementHeight

            drawRoundRect(
                color = color,
                topLeft = Offset(this.size.width - width.toPx(), scrollbarOffsetY),
                size = Size(width.toPx(), scrollbarHeight),
                cornerRadius = CornerRadius(50f, 50f),
                alpha = alpha
            )
        }
    }
}


val previewVouchers = listOf(
    VoucherModel(
        type = "Lunch",
        date = "26th Feb 2025",
        location = "The Sentry",
        price = "350.000",
        imageUrl = "https://picsum.photos/200",
        sponsorLogoUrls = listOf("https://picsum.photos/200")
    ),
    VoucherModel(
        type = "Snack",
        date = "27th Feb 2025",
        location = "The Sentry",
        price = "20.000",
        imageUrl = "https://picsum.photos/200",
        sponsorLogoUrls = listOf("https://picsum.photos/200")
    )
).groupBy { it.date }

@Preview
@Composable
private fun VouchersContentPreview() {
    NFQSnapshotTestThemeForPreview {

        VouchersContent(
            attendeeName = "Pattarawit Gochgornkamud",
            vouchers = previewVouchers
        )
    }
}
