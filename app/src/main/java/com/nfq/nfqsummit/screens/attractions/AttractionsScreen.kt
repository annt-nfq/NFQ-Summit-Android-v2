@file:OptIn(ExperimentalMaterial3Api::class)

package com.nfq.nfqsummit.screens.attractions

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.nfq.data.domain.model.Attraction
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
                        style = MaterialTheme.typography.headlineLarge,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = goBack
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
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
        modifier = Modifier
            .fillMaxWidth()
            .background(NFQOrange, shape = RoundedCornerShape(16.dp))
            .clickable {
                goToAttraction(attraction.id)
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = attraction.title,
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White,
                fontWeight = FontWeight(600)
            ),
        )
        Spacer(modifier = Modifier.width(24.dp))
        AsyncImage(
            model = attraction.icon,
            contentDescription = null,
            modifier = Modifier.height(100.dp),
            contentScale = ContentScale.FillHeight
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AttractionsUIPreview() {
    NFQSnapshotTestThemeForPreview {
        AttractionsUI(
            attractions = listOf(
                Attraction(
                    id = 1,
                    title = "Attraction 1",
                    icon = ""
                ),
                Attraction(
                    id = 2,
                    title = "Attraction 2",
                    icon = ""
                )
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
            Attraction(
                id = 1,
                title = "Attraction Title",
                icon = ""
            )
        )
    }
}