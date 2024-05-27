package com.nfq.nfqsummit.screens.dashboard

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nfq.nfqsummit.R
import com.nfq.nfqsummit.navigation.AppDestination
import com.nfq.nfqsummit.navigation.DashboardNavHost
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview

@Composable
fun DashboardScreen(
    goToEventDetails: (eventId: String) -> Unit
) {
    val bottomNavController = rememberNavController()
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Schedule,
        BottomNavItem.TechRocks,
        BottomNavItem.Explore
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            NavigationBar {
                items.forEach { bottomNavItem ->
                    val selected =
                        currentDestination?.hierarchy?.any { it.route == bottomNavItem.destination.route } == true
                    NavigationBarItem(
                        label = {
                            Text(text = bottomNavItem.title)
                        },
                        selected = selected,
                        onClick = {
                            bottomNavController.navigate(bottomNavItem.destination.route) {
                                bottomNavController.graph.startDestinationRoute?.let { screenRoute ->
                                    popUpTo(screenRoute) {
                                        saveState = true
                                    }
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Image(
                                painter = painterResource(id = bottomNavItem.icon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                colorFilter = ColorFilter.tint(
                                    if (selected) MaterialTheme.colorScheme.primary else
                                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                alpha = 0.5f
                            )
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        DashboardNavHost(
            navController = bottomNavController,
            goToEventDetails = goToEventDetails,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

sealed class BottomNavItem(
    var title: String,
    @DrawableRes var icon: Int,
    var destination: AppDestination
) {
    data object Home : BottomNavItem("Home", R.drawable.ic_home, AppDestination.Home)
    data object Schedule :
        BottomNavItem("Schedule", R.drawable.ic_schedule, AppDestination.Schedule)

    data object TechRocks : BottomNavItem("Tech Rocks", R.drawable.ic_tra, AppDestination.TechRocks)
    data object Explore : BottomNavItem("Explore", R.drawable.ic_explore, AppDestination.Explore)
}

@Preview
@Composable
fun DashboardScreenPreview() {
    NFQSnapshotTestThemeForPreview {
        DashboardScreen(goToEventDetails = {})
    }
}