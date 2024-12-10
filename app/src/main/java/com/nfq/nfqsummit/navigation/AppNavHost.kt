package com.nfq.nfqsummit.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nfq.nfqsummit.screens.attractions.AttractionsScreen
import com.nfq.nfqsummit.screens.attractions.attractionBlogs.AttractionBlogsScreen
import com.nfq.nfqsummit.screens.blog.BlogScreen
import com.nfq.nfqsummit.screens.bookingNumber.BookingNumberScreen
import com.nfq.nfqsummit.screens.dashboard.DashboardScreen
import com.nfq.nfqsummit.screens.dashboard.tabs.explore.ExploreTab
import com.nfq.nfqsummit.screens.dashboard.tabs.home.HomeTab
import com.nfq.nfqsummit.screens.dashboard.tabs.schedule.ScheduleTab
import com.nfq.nfqsummit.screens.dashboard.tabs.techRocks.TechRocksTab
import com.nfq.nfqsummit.screens.eventDetails.EventDetailsScreen
import com.nfq.nfqsummit.screens.onboarding.OnboardingScreen
import com.nfq.nfqsummit.screens.payment.PaymentScreen
import com.nfq.nfqsummit.screens.signIn.SignInScreen
import com.nfq.nfqsummit.screens.splash.SplashScreen
import com.nfq.nfqsummit.screens.survival.SurvivalScreen
import com.nfq.nfqsummit.screens.transportation.TransportationScreen

@SuppressLint("NewApi")
@Composable
fun AppNavHost(
    startDestination: String,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
        enterTransition = SlideTransition.enterTransition,
        exitTransition = SlideTransition.exitTransition,
        popEnterTransition = SlideTransition.popEnterTransition,
        popExitTransition = SlideTransition.popExitTransition
    ) {
        composable(AppDestination.Splash.route) {
            SplashScreen()
        }
        composable(
            route = AppDestination.Onboarding.route,
            enterTransition = MainTransition.enterTransition,
            exitTransition = MainTransition.exitTransition,
            popEnterTransition = MainTransition.enterTransition,
            popExitTransition = MainTransition.exitTransition
        ) {
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
        composable(AppDestination.SignIn.route) {
            SignInScreen(
                continueAsGuest = {
                    navController.navigate(AppDestination.Dashboard.route) {
                        popUpTo(AppDestination.SignIn.route) { inclusive = true }
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
        composable(
            route = AppDestination.Dashboard.route,
            enterTransition = MainTransition.enterTransition,
            exitTransition = MainTransition.exitTransition,
            popEnterTransition = MainTransition.enterTransition,
            popExitTransition = MainTransition.exitTransition
        ) {
            DashboardScreen(
                goToEventDetails = {
                    navController.navigate(
                        "${AppDestination.EventDetails.route}/$it"
                    )
                },
                goToDestination = {
                    navController.navigate(it.route)
                },
                goToDetails = {
                    navController.navigate("${AppDestination.Blogs.route}/$it")
                },
                goToAttractions = {
                    navController.navigate(AppDestination.Attractions.route)
                }
            )
        }

        composable(AppDestination.Survival.route) {
            SurvivalScreen(
                goBack = { navController.navigateUp() }
            )
        }

        composable(AppDestination.Transportations.route) {
            TransportationScreen(
                goBack = { navController.navigateUp() },
                goToBlog = {
                    navController.navigate("${AppDestination.Blogs.route}/$it")
                }
            )
        }

        composable(AppDestination.Payment.route) {
            PaymentScreen(
                goBack = { navController.navigateUp() }
            )
        }

        composable(AppDestination.Attractions.route) {
            AttractionsScreen(
                goBack = { navController.navigateUp() },
                goToAttraction = { attractionId, attractionTitle ->
                    navController.navigate("${AppDestination.Attractions.route}/${attractionId}/${attractionTitle}")
                }
            )
        }

        composable(
            route = AppDestination.Attractions.routeWithArgs,
            arguments = AppDestination.Attractions.arguments,
            deepLinks = AppDestination.Attractions.deeplinks
        ) {
            val attractionId = it.arguments?.getString(AppDestination.Attractions.attractionIdArg)
            val attractionTitle =
                it.arguments?.getString(AppDestination.Attractions.attractionTitleArg)
            AttractionBlogsScreen(
                attractionId = attractionId!!,
                attractionTitle = attractionTitle.orEmpty(),
                goBack = { navController.navigateUp() },
                goToBlog = { blogId ->
                    navController.navigate("${AppDestination.Blogs.route}/$blogId")
                }
            )
        }

        composable(
            route = AppDestination.Blogs.routeWithArgs,
            arguments = AppDestination.Blogs.arguments,
            deepLinks = AppDestination.Blogs.deeplinks
        ) {
            val blogId = it.arguments?.getString(AppDestination.Blogs.blogIdArg)
            BlogScreen(
                blogId = blogId!!,
                goBack = { navController.navigateUp() }
            )
        }

        composable(
            route = AppDestination.EventDetails.routeWithArgs,
            arguments = AppDestination.EventDetails.arguments,
            deepLinks = AppDestination.EventDetails.deeplinks
        ) {
            val eventId =
                it.arguments?.getString(AppDestination.EventDetails.eventIdArg)
            EventDetailsScreen(eventId = eventId, goBack = { navController.navigateUp() })
        }
    }
}

@Composable
fun DashboardNavHost(
    goToEventDetails: (eventId: String) -> Unit,
    goToDestination: (destination: AppDestination) -> Unit,
    goToBlog: (blogId: Int) -> Unit,
    goToAttractions: () -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = AppDestination.Home.route) {
        composable(AppDestination.Home.route) {
            HomeTab(
                goToAttractions = goToAttractions
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
            ExploreTab(
                goToDestination = goToDestination
            )
        }
    }
}

const val durationMillis = 100
const val slideDurationMillis = 400

object MainTransition {

    val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        { fadeIn(tween(durationMillis)) }
    val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
        { fadeOut(tween(durationMillis)) }
}

object SlideTransition {
    val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        {
            slideInHorizontally(
                initialOffsetX = { 1500 },
                animationSpec = tween(slideDurationMillis)
            )
        }
    val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
        {
            slideOutHorizontally(
                targetOffsetX = { -300 },
                animationSpec = tween(slideDurationMillis)
            )
        }


    val popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        {
            slideInHorizontally(
                initialOffsetX = { -300 },
                animationSpec = tween(slideDurationMillis)
            )

        }
    val popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
        {
            slideOutHorizontally(
                targetOffsetX = { 1500 },
                animationSpec = tween(slideDurationMillis)
            )
        }
}