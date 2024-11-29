package com.nfq.nfqsummit.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview

@Composable
fun Loading() {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(
            decorFitsSystemWindows = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        BasicCard(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(90.dp),
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(35.dp)
            )
        }
    }
}

@Preview
@Composable
private fun LoadingPreview() {
    NFQSnapshotTestThemeForPreview {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Loading()
        }

    }
}