package com.nfq.nfqsummit.screens.dashboard.tabs.explore

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nfq.nfqsummit.analytics.TrackScreenViewEvent
import com.nfq.nfqsummit.components.Loading
import com.nfq.nfqsummit.components.bounceClick
import com.nfq.nfqsummit.components.networkImagePainter
import com.nfq.nfqsummit.navigation.AppDestination
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview

@Composable
fun ExploreTab(
    goToDestination: (destination: String) -> Unit = {}
) {
    val viewModel: ExploreViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()


    if (uiState.isLoading) {
        Loading()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            Text(
                text = "Explore Vietnam \uD83C\uDDFB\uD83C\uDDF3",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = uiState.exploreVietnam,
                key = { it.title },
                contentType = { "ExploreItem" }
            ) { exploreItem ->
                ExploreItem(
                    exploreItem = exploreItem,
                    goToDestination = goToDestination
                )
            }
        }
    }

    TrackScreenViewEvent(screenName = "explore")
}

data class ExploreItem(
    val imageUrl: String,
    val title: String,
    val destination: String
)

@Composable
fun ExploreItem(
    exploreItem: ExploreItem,
    goToDestination: (destination: String) -> Unit
) {
    Box(
        contentAlignment = Alignment.BottomStart,
        modifier = Modifier
            .bounceClick()
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                goToDestination(exploreItem.destination)
            }
    ) {
        Image(
            painter = networkImagePainter(exploreItem.imageUrl),
            contentDescription = "Event Image",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(343f / 116f)
                .background(Color(0xFFE6E6E6)),
            contentScale = ContentScale.FillWidth
        )
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            GuidelineTag()
            Text(
                text = exploreItem.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun GuidelineTag() {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp),
            )
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Text(
            text = "Guideline",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExploreTabPreview() {
    NFQSnapshotTestThemeForPreview {
        ExploreTab()
    }
}

@Preview
@Composable
private fun ExploreItemPreview() {
    NFQSnapshotTestThemeForPreview {
        ExploreItem(
            exploreItem = ExploreItem(
                imageUrl = "",
                title = "Transportation \uD83D\uDE90",
                destination = AppDestination.Attractions.route
            ),
            goToDestination = {}
        )
    }
}