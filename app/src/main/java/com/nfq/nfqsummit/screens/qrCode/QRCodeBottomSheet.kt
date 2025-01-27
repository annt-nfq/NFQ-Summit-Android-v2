@file:OptIn(ExperimentalMaterial3Api::class)

package com.nfq.nfqsummit.screens.qrCode

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nfq.nfqsummit.R
import com.nfq.nfqsummit.components.BasicModalBottomSheet
import com.nfq.nfqsummit.components.CachedNetworkImage
import com.nfq.nfqsummit.components.ImageCache
import com.nfq.nfqsummit.components.bounceClick
import com.nfq.nfqsummit.components.networkImagePainter
import com.nfq.nfqsummit.model.UserUIModel
import com.nfq.nfqsummit.openWhatsapp
import com.nfq.nfqsummit.ui.theme.MainGreen
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview

@Composable
fun QRCodeBottomSheet(
    user: UserUIModel,
    onDismissRequest: () -> Unit
) {
    val skipPartiallyExpanded by remember { mutableStateOf(true) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded)

    BasicModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
        content = {
            QRCodeContent(userUIModel = user)
        }
    )
}

@Composable
private fun QRCodeContent(
    modifier: Modifier = Modifier,
    userUIModel: UserUIModel
) {
    val context = LocalContext.current
    val phoneNumber = "+76 333 2145"
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxHeight(0.85f)
            .padding(horizontal = 55.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(bottom = 48.dp)
                .clip(RoundedCornerShape(5.0.dp))
                .background(Color(0xFFFFFFFF))
                .width(70.dp)
                .height(5.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = userUIModel.name,
            style = MaterialTheme.typography.headlineMedium,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = userUIModel.attendeeCode,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Spacer(modifier = Modifier.height(26.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.onPrimary)
                .padding(6.dp)
        ) {
            CachedNetworkImage(
                imageUrl = userUIModel.qrCodeUrl,
                contentDescription = null,
                modifier = Modifier.size(220.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Please show this QR code to our staff\n" + "for check-in.",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "Please ensure you’ve reserved a seat for each event. For assistance, contact Tris on WhatsApp.",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(32.dp))
        Box(
            modifier = Modifier
                .bounceClick()
                .clip(RoundedCornerShape(100.dp))
                .background(MainGreen)
                .clickable { context.openWhatsapp(phoneNumber) }
                .padding(horizontal = 16.dp, vertical = 8.dp)

        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_whatsapp),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = phoneNumber,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.background
                )
            }
        }
    }
}

@Preview
@Composable
private fun QRCodeContentPreview() {
    NFQSnapshotTestThemeForPreview {
        QRCodeContent(
            modifier = Modifier.fillMaxSize(),
            userUIModel = UserUIModel(
                id = "1",
                name = "Hermann Hauser",
                attendeeCode = "255373826",
                qrCodeUrl = "",
                email = "wine@nfq.com"
            )
        )
    }
}