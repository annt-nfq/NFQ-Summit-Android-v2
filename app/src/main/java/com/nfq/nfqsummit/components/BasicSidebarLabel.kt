package com.nfq.nfqsummit.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nfq.nfqsummit.convertTimeFormat
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val HourFormatter = DateTimeFormatter.ofPattern("h a")
private val AMPMFormatter = DateTimeFormatter.ofPattern("a")

@Composable
fun BasicSidebarLabel(
    time: LocalTime,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = time.format(HourFormatter).convertTimeFormat(),
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 15.sp,
            modifier = modifier.padding(end = 10.dp),
            textAlign = TextAlign.Left
        )
    }
}


@Preview(showBackground = true)
@Composable
fun BasicSidebarLabelPreview() {
    NFQSnapshotTestThemeForPreview {
        BasicSidebarLabel(time = LocalTime.NOON, Modifier.sizeIn(maxHeight = 64.dp))
    }
}