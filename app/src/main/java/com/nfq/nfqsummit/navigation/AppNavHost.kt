package com.nfq.nfqsummit.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nfq.nfqsummit.screens.bookingNumber.BookingNumberScreen
import com.nfq.nfqsummit.screens.dashboard.DashboardScreen
import com.nfq.nfqsummit.screens.dashboard.tabs.explore.ExploreTab
import com.nfq.nfqsummit.screens.dashboard.tabs.home.HomeTab
import com.nfq.nfqsummit.screens.dashboard.tabs.schedule.ScheduleTab
import com.nfq.nfqsummit.screens.dashboard.tabs.techRocks.TechRocksTab
import com.nfq.nfqsummit.screens.eventDetails.EventDetailsScreen
import com.nfq.nfqsummit.screens.onboarding.OnboardingScreen
import com.nfq.nfqsummit.screens.splash.SplashScreen

@SuppressLint("NewApi")
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = AppDestination.Dashboard.route) {
        composable(AppDestination.Splash.route) {
            SplashScreen(
                navigateToHome = {
                    navController.navigate(AppDestination.Dashboard.route) {
                        popUpTo(AppDestination.Splash.route) { inclusive = true }
                    }
                },
                navigateToOnboarding = {
                    navController.navigate(AppDestination.Onboarding.route) {
                        popUpTo(AppDestination.Splash.route) { inclusive = true }
                    }
                },
            )
        }
        composable(AppDestination.Onboarding.route) {
            OnboardingScreen(
                navigateToHome = {
                    navController.navigate(AppDestination.Dashboard.route) {
                        popUpTo(AppDestination.Onboarding.route) { inclusive = true }
                    }
                },
                navigateToBooking = {
                    navController.navigate(AppDestination.BookingNumber.route) {
                        popUpTo(AppDestination.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(AppDestination.BookingNumber.route) {
            BookingNumberScreen(
                navigateToHome = {
                    navController.navigate(AppDestination.Dashboard.route) {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        composable(AppDestination.Dashboard.route) {
            DashboardScreen(
                goToEventDetails = {
                    navController.navigate(
                        "${AppDestination.EventDetails.route}/$it"
                    )
                }
            )
        }
        composable(
            route = AppDestination.EventDetails.routeWithArgs,
            arguments = AppDestination.EventDetails.arguments,
            deepLinks = AppDestination.EventDetails.deeplinks
        ) {
            val eventId =
                it.arguments?.getString(AppDestination.EventDetails.eventIdArg)
            EventDetailsScreen(eventId = eventId, goBack = { navController.popBackStack() })
        }
    }
}

@Composable
fun DashboardNavHost(
    goToEventDetails: (eventId: String) -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = AppDestination.Home.route) {
        composable(AppDestination.Home.route) {
            HomeTab(
                goToEventDetails = goToEventDetails
            )
        }
        composable(AppDestination.Schedule.route) {
            ScheduleTab(
                goToEventDetails = goToEventDetails
            )
        }
        composable(AppDestination.TechRocks.route) {
            TechRocksTab(
                goToEventDetails = goToEventDetails
            )
        }
        composable(AppDestination.Explore.route) {
            ExploreTab()
        }
    }
}