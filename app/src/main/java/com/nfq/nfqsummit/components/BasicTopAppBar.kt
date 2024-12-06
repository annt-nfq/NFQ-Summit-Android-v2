@file:OptIn(ExperimentalMaterial3Api::class)

package com.nfq.nfqsummit.components

import androidx.compose.foundation.Image
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.nfq.nfqsummit.R
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview

@Composable
fun BasicTopAppBar(
    title: String = "",
    navigationUp: () -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
        },
        navigationIcon = {
            IconButton(
                modifier = Modifier.bounceClick(),
                onClick = navigationUp
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = "Back",
                )
            }
        },
    )
}

@Preview
@Composable
private fun BasicTopAppBarPreview() {
    NFQSnapshotTestThemeForPreview {
        BasicTopAppBar(
            title = "Saved Event"
        )
    }
}