package com.nfq.nfqsummit.screens.dashboard.tabs.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
            .padding(horizontal = 18.dp)
            .fillMaxSize()
    ) {
        IconButton(
            onClick = onDismissRequest,
            modifier = Modifier
                .padding(vertical = 17.dp)
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

        LazyColumn {
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


val previewVouchers = listOf(
    VoucherModel(
        name = "Lunch",
        date = "26th Feb 2025",
        location = "The Sentry",
        price = "350.000",
        image = "https://picsum.photos/200"
    ),
    VoucherModel(
        name = "Snack",
        date = "27th Feb 2025",
        location = "The Sentry",
        price = "20.000",
        image = "https://picsum.photos/200"
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
