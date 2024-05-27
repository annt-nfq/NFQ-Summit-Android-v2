package com.nfq.nfqsummit.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink

sealed class AppDestination(var route: String) {

    data object Onboarding : AppDestination(route = "onboarding")

    data object BookingNumber: AppDestination(route = "booking")

    data object Dashboard : AppDestination(route = "dashboard")

    data object Home: AppDestination(route = "home")

    data object Schedule: AppDestination(route = "schedule")

    data object TechRocks: AppDestination(route = "techRocks")

    data object Explore: AppDestination(route = "explore")

    data object Splash : AppDestination(route = "splash")

    data object EventDetails : AppDestination(route = "eventDetails") {
        const val eventIdArg = "eventId"
        val routeWithArgs = "$route/{$eventIdArg}"
        val arguments = listOf(
            navArgument(eventIdArg) { type = NavType.StringType }
        )
        val deeplinks = listOf(
            navDeepLink {
                uriPattern = "snapshottest://$route/{$eventIdArg}"
            }
        )
    }
}