package com.nfq.nfqsummit.screens.dashboard.tabs.explore

import android.annotation.SuppressLint
import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nfq.nfqsummit.R
import com.nfq.nfqsummit.navigation.AppDestination
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ExploreTab(
    goToDestination: (destination: AppDestination) -> Unit = {}
) {
    val window = (LocalView.current.context as Activity).window
    window.statusBarColor = Color.Transparent.toArgb()

    val exploreItems = listOf(
        ExploreItem(R.drawable.explore_attractions, AppDestination.Attractions),
//        ExploreItem(R.drawable.explore_activities, AppDestination.Attractions),
//        ExploreItem(R.drawable.explore_gifts, AppDestination.Attractions),
        ExploreItem(R.drawable.explore_payment, AppDestination.Payment),
        ExploreItem(R.drawable.explore_survival, AppDestination.Survival),
        ExploreItem(R.drawable.explore_transport, AppDestination.Transportations)
    )
    Scaffold {
        Column(modifier = Modifier.statusBarsPadding()) {
            Text(
                text = "Explore BKK",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(16.dp)
            )
            LazyVerticalGrid(
                columns = GridCells.Adaptive(150.dp),
            ) {
                items(exploreItems) { item ->
                    ExploreGridItem(
                        exploreItem = item,
                        goToDestination = goToDestination
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(120.dp))
                }
            }
        }
    }
}

data class ExploreItem(
    @DrawableRes val drawable: Int,
    val destination: AppDestination
)

@Composable
fun ExploreGridItem(
    exploreItem: ExploreItem,
    goToDestination: (destination: AppDestination) -> Unit
) {
    Image(
        painter = painterResource(id = exploreItem.drawable),
        contentDescription = "Event Image",
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable {
                goToDestination(exploreItem.destination)
            },
        contentScale = ContentScale.FillWidth
    )
}

@Preview(showBackground = true)
@Composable
fun ExploreTabPreview() {
    NFQSnapshotTestThemeForPreview {
        ExploreTab()
    }
}