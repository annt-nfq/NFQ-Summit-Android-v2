package com.nfq.nfqsummit.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink

sealed class AppDestination(var route: String) {

    data object Onboarding : AppDestination(route = "onboarding")

    data object BookingNumber : AppDestination(route = "booking")

    data object Dashboard : AppDestination(route = "dashboard")

    data object Home : AppDestination(route = "home")

    data object Schedule : AppDestination(route = "schedule")

    data object TechRocks : AppDestination(route = "techRocks")

    data object Explore : AppDestination(route = "explore")

    data object SavedEvents : AppDestination(route = "savedEvents")

    data object Splash : AppDestination(route = "splash")

    data object Payment : AppDestination(route = "payment")

    data object Survival : AppDestination(route = "survival")

    data object SignIn : AppDestination(route = "signIn")

    data object QRCodeScanner : AppDestination(route = "qrCodeScanner")

    data object Blogs : AppDestination(route = "blogs") {
        const val blogIdArg = "blogId"
        val routeWithArgs = "$route/{$blogIdArg}"
        val arguments = listOf(
            navArgument(blogIdArg) { type = NavType.StringType },
        )
        val deeplinks = listOf(
            navDeepLink {
                uriPattern = "nfqsummit://$route/{$blogIdArg}"
            }
        )
    }

    data object Transportations : AppDestination(route = "transportations"){
        const val parentBlogId = "parentBlogId"
        val routeWithArgs = "$route/{$parentBlogId}"
        val arguments = listOf(
            navArgument(parentBlogId) { type = NavType.StringType }
        )
        val deeplinks = listOf(
            navDeepLink {
                uriPattern = "nfqsummit://$route/{$parentBlogId}"
            }
        )
    }

    data object Attractions : AppDestination(route = "attractions") {
        const val attractionIdArg = "attractionId"
        const val attractionTitleArg = "attractionTitle"
        val routeWithArgs = "$route/{$attractionIdArg}/{$attractionTitleArg}"
        val arguments = listOf(
            navArgument(attractionIdArg) { type = NavType.StringType },
            navArgument(attractionTitleArg) { type = NavType.StringType }
        )
        val deeplinks = listOf(
            navDeepLink {
                uriPattern = "nfqsummit://$route/{$attractionIdArg}/{$attractionTitleArg}"
            }
        )
    }

    data object EventDetails : AppDestination(route = "eventDetails") {
        const val eventIdArg = "eventId"
        val routeWithArgs = "$route/{$eventIdArg}"
        val arguments = listOf(
            navArgument(eventIdArg) { type = NavType.StringType }
        )
        val deeplinks = listOf(
            navDeepLink {
                uriPattern = "nfqsummit://$route/{$eventIdArg}"
            }
        )
    }
}