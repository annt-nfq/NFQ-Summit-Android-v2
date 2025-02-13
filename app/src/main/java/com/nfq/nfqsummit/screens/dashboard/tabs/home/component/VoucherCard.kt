package com.nfq.nfqsummit.screens.dashboard.tabs.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nfq.nfqsummit.R
import com.nfq.nfqsummit.components.BasicCard
import com.nfq.nfqsummit.components.networkImagePainter
import com.nfq.nfqsummit.model.LocationUIModel
import com.nfq.nfqsummit.model.VoucherUIModel
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview

@Composable
fun VoucherCard(
    attendeeName: String,
    model: VoucherUIModel
) {
    BasicCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.primary)
                .padding(horizontal = 12.dp, vertical = 18.dp)
                .fillMaxWidth()
        ) {
            Row {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        painter = painterResource(R.drawable.voucher_logo),
                        contentDescription = "voucher_logo"
                    )
                    Text(
                        text = model.type.replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.ROOT) else it.toString() },
                        style = MaterialTheme.typography.displaySmall,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Image(
                    painter = painterResource(R.drawable.ic_union),
                    contentDescription = "ic_union",
                    modifier = Modifier.weight(1f)
                )

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = model.price,
                        style = MaterialTheme.typography.displaySmall,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        letterSpacing = (-0.5).sp,
                    )
                    Image(
                        painter = painterResource(R.drawable.ic_currency),
                        contentDescription = "ic_currency",
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }

            }

            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(bottom = 24.dp)
                    .clip(CircleShape)
                    .size(150.dp)
            ) {
                model.imageBitmap?.asImageBitmap()?.let {
                    Image(
                        bitmap = it,
                        contentDescription = "voucher_image",
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Text(
                text = model.date,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp,
            )

            Text(
                text = attendeeName,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp,
                modifier = Modifier.padding(top = 6.dp)
            )

            Column(
                modifier = Modifier.padding(top = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                model.locations.map { model ->
                    LocationItem(
                        model = model
                    )
                }
            }

            LazyRow(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                modifier = Modifier
                    .padding(top = 35.dp)
                    .padding(bottom = 2.dp)
            ) {
                items(model.sponsorLogoUrls) { url ->
                    Image(
                        painter = networkImagePainter(url),
                        contentDescription = "sponsor_logo",
                        modifier = Modifier.height(35.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationItem(model: LocationUIModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.ic_location_16_filled),
                contentDescription = "ic_location_16_filled"
            )

            Text(
                text = model.name,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        Text(
            text = model.address,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 3.dp)
        )
    }

}

@Preview
@Composable
private fun VoucherCardPreview() {
    NFQSnapshotTestThemeForPreview {
        VoucherCard(
            attendeeName = "Pattarawit Gochgornkamud",
            model = VoucherUIModel(
                type = "Lunch",
                date = "26th Feb 2025",
                locations = listOf(
                    LocationUIModel(
                        id = 1,
                        name = "NFQ Technologies",
                        address = "Bangkok, Thailand"
                    )
                ),
                price = "350.000",
                imageUrl = "https://picsum.photos/200",
                imageBitmap = null,
                sponsorLogoUrls = listOf("https://picsum.photos/200")
            )
        )
    }
}