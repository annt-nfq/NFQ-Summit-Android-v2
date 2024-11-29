package com.nfq.nfqsummit.screens.dashboard

import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.nfq.nfqsummit.entry.MainActivity
import com.nfq.nfqsummit.navigation.AppNavHost
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class DashboardScreenTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    lateinit var navController: TestNavHostController

    @Before
    fun setup() {
        hiltRule.inject()
        composeTestRule.activity.runOnUiThread {
            composeTestRule.activity.setContent {
                navController = TestNavHostController(LocalContext.current)
                navController.navigatorProvider.addNavigator(ComposeNavigator())
                AppNavHost(navController = navController)
            }
        }
    }

    @Test
    fun canNavigateThroughBottomNav() {
        composeTestRule.onNodeWithText("Recommendations Today").assertIsDisplayed()

        composeTestRule.onNodeWithText("Schedule").performClick()
        composeTestRule.onNodeWithText("My Bookings").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sat").performClick()
        Thread.sleep(1500)
        composeTestRule.onNodeWithText("Event 1").performClick()
        Thread.sleep(1500)
        composeTestRule.onNodeWithText("Event 1").assertIsDisplayed()
        val route = navController.currentBackStackEntry?.destination?.route
        Assert.assertEquals(route, "eventDetails/{eventId}")
    }
}