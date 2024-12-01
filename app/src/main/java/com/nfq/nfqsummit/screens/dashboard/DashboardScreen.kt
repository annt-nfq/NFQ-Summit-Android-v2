package com.nfq.nfqsummit.screens.dashboard

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nfq.nfqsummit.R
import com.nfq.nfqsummit.navigation.AppDestination
import com.nfq.nfqsummit.screens.dashboard.tabs.explore.ExploreTab
import com.nfq.nfqsummit.screens.dashboard.tabs.home.HomeTab
import com.nfq.nfqsummit.screens.dashboard.tabs.schedule.ScheduleTab
import com.nfq.nfqsummit.screens.savedEvents.SavedEventScreen
import com.nfq.nfqsummit.ui.theme.MainGreen
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    goToEventDetails: (eventId: String) -> Unit,
    goToDestination: (destination: AppDestination) -> Unit,
    goToDetails: (eventId: Int) -> Unit,
    goToAttractions: () -> Unit,
) {
    val window = (LocalView.current.context as Activity).window
    window.statusBarColor = Color.Transparent.toArgb()
    WindowCompat.setDecorFitsSystemWindows(window, false)

    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Schedule,
        BottomNavItem.SavedEvents,
        BottomNavItem.Explore
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerShape = RoundedCornerShape(0.dp),
                windowInsets = WindowInsets(0)
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                DrawerContent(
                    menus = items,
                    onMenuClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        navController.navigate(it) {
                            navController.graph.startDestinationRoute?.let { screenRoute ->
                                popUpTo(screenRoute) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onSignOutClick = {

                    },
                    isSelected = { item ->
                        currentDestination?.hierarchy?.any { it.route == item.destination.route } == true
                    }
                )
            }
        },
        modifier = Modifier
            .fillMaxSize()

    ) {
        Column {
            DashBoardHeader(
                onMenuClick = {
                    coroutineScope.launch {
                        drawerState.open()
                    }
                }
            )
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.Home.destination.route,
                modifier = Modifier.weight(1f)
            ) {
                composable(AppDestination.Home.route) {
                    HomeTab(
                        goToAttractions = goToAttractions,
                        seeAllSavedEvents = {
                            navController.navigate(AppDestination.SavedEvents.route)
                        },
                        seeAllEvents = {
                            navController.navigate(AppDestination.Schedule.route)
                        }
                    )
                }
                composable(AppDestination.Schedule.route) {
                    ScheduleTab(
                        goToEventDetails = goToEventDetails
                    )
                }
                composable(AppDestination.Explore.route) {
                    ExploreTab(
                        goToDestination = goToDestination
                    )
                }
                composable(AppDestination.SavedEvents.route) {
                    SavedEventScreen(
                        navigateUp = { navController.navigateUp() }
                    )
                }
            }
        }
    }
}

@Composable
fun DashBoardHeader(
    onMenuClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp)
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_menu),
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .width(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                    onMenuClick()
                },
        )
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(id = R.drawable.ic_nfq_text_white),
            contentDescription = null,
            modifier = Modifier
                .width(80.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
fun DrawerContent(
    menus: List<BottomNavItem>,
    onMenuClick: (String) -> Unit,
    onSignOutClick: () -> Unit,
    isSelected: (BottomNavItem) -> Boolean,
) {
    var switchCheckedState by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_nfq_text_white),
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .padding(start = 34.dp)
                .padding(top = 36.dp)
                .width(80.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            thickness = 0.5.dp
        )
        Spacer(modifier = Modifier.height(28.dp))
        menus.forEach {
            val selected = isSelected(it)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            ) {
                Spacer(modifier = Modifier.width(19.dp))
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.0.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    ),
                    onClick = {
                        onMenuClick(it.destination.route)
                    },
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 16.dp),
                    ) {
                        Text(
                            text = it.title, style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.W400,
                                color = if (selected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground,
                            ),
                            modifier = Modifier.align(Alignment.CenterStart)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(49.dp))
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 19.dp, end = 16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Switch(
                    checked = switchCheckedState,
                    onCheckedChange = { switchCheckedState = it },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = MainGreen
                    ),
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier
                        .weight(1f),
                ) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Push Notification",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onBackground,
                        ),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Turning this on will send a notification to your device as a reminder of the event time.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        ),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
                .navigationBarsPadding(),
        ) {
            TextButton(
                onClick = onSignOutClick,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Text(
                    text = "Sign out",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

sealed class BottomNavItem(
    var title: String,
    @DrawableRes var icon: Int,
    var destination: AppDestination
) {
    data object Home : BottomNavItem("Home", R.drawable.ic_home, AppDestination.Home)
    data object Schedule :
        BottomNavItem("Calendar & Events", R.drawable.ic_schedule, AppDestination.Schedule)

    data object SavedEvents :
        BottomNavItem("Saved Event", R.drawable.ic_tra, AppDestination.SavedEvents)

    data object Explore : BottomNavItem("Explore", R.drawable.ic_explore, AppDestination.Explore)
}

@Preview
@Composable
fun DashboardScreenPreview() {
    NFQSnapshotTestThemeForPreview {
        DashboardScreen(
            goToEventDetails = {},
            goToDestination = {},
            goToDetails = {},
            goToAttractions = {})
    }
}