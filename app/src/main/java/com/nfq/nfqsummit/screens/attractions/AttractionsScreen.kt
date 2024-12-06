@file:OptIn(ExperimentalMaterial3Api::class)

package com.nfq.nfqsummit.screens.attractions

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.Image
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.nfq.data.domain.model.Attraction
import com.nfq.nfqsummit.R
import com.nfq.nfqsummit.mocks.mockAttraction
import com.nfq.nfqsummit.ui.theme.NFQOrange
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AttractionsScreen(
    goBack: () -> Unit,
    goToAttraction: (attractionId: Int) -> Unit,
    viewModel: AttractionsViewModel = hiltViewModel(),
) {
    val window = (LocalView.current.context as Activity).window
    window.statusBarColor = Color.Transparent.toArgb()

    LaunchedEffect(viewModel) {
        viewModel.getAttractions()
    }

    AttractionsUI(
        attractions = viewModel.attractions ?: emptyList(),
        goBack = goBack,
        goToAttraction = goToAttraction
    )
}

@Composable
fun AttractionsUI(
    attractions: List<Attraction>, goBack: () -> Unit,
    goToAttraction: (attractionId: Int) -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Attractions",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = goBack
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Back",
                        )
                    }
                },
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
    goToAttraction: (attractionId: Int) -> Unit = {}
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
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
            contentScale = ContentScale.Inside
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