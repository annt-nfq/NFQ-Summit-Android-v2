package com.nfq.nfqsummit.screens.attractions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import coil.compose.AsyncImage
import com.nfq.data.domain.model.Attraction
import com.nfq.nfqsummit.components.BasicTopAppBar
import com.nfq.nfqsummit.components.bounceClick
import com.nfq.nfqsummit.mocks.mockAttraction
import com.nfq.nfqsummit.ui.theme.NFQOrange
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview

@Composable
fun AttractionsScreen(
    goBack: () -> Unit,
    goToAttraction: (attractionId: String) -> Unit,
    viewModel: AttractionsViewModel = hiltViewModel(),
) {
    val attractions by viewModel.attractions.collectAsState()
    AttractionsUI(
        attractions = attractions,
        goBack = goBack,
        goToAttraction = goToAttraction
    )
}

@Composable
fun AttractionsUI(
    attractions: List<Attraction>, goBack: () -> Unit,
    goToAttraction: (attractionId: String) -> Unit = {}
) {
    Scaffold(
        topBar = {
            BasicTopAppBar(
                title = "Attractions",
                navigationUp = goBack
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(paddingValues)
                .navigationBarsPadding()
        ) {
            items(attractions) { attraction ->
                AttractionListItem(
                    attraction = attraction,
                    goToAttraction = goToAttraction
                )
            }
        }
    }
}

@Composable
fun AttractionListItem(
    attraction: Attraction,
    goToAttraction: (attractionId: String) -> Unit = {}
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick()
            .clip(RoundedCornerShape(16.dp))
            .background(NFQOrange)
            .clickable { goToAttraction(attraction.id) }

    ) {
        Spacer(modifier = Modifier.width(24.dp))
        Text(
            text = attraction.title,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight(600),
        )
        Spacer(modifier = Modifier.width(24.dp))
        AsyncImage(
            model = attraction.icon,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.width(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun AttractionsUIPreview() {
    NFQSnapshotTestThemeForPreview {
        AttractionsUI(
            attractions = listOf(
                mockAttraction,
                mockAttraction
            ),
            goBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AttractionListItemPreview() {
    NFQSnapshotTestThemeForPreview {
        AttractionListItem(
            mockAttraction
        )
    }
}