package com.nfq.nfqsummit.screens.dashboard.tabs.home

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.nfq.data.domain.repository.EventRepository
import com.nfq.nfqsummit.MainActivity
import com.nfq.nfqsummit.di.FakeEventRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class HomeTabKtTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var viewModel: HomeViewModel
    private lateinit var eventRepository: EventRepository

    @Before
    fun setup() {
        hiltRule.inject()

        eventRepository = FakeEventRepository()

        viewModel = HomeViewModel(eventRepository)

        composeTestRule.activity.runOnUiThread {
            composeTestRule.activity.setContent {
                HomeTab(
                    viewModel = viewModel,
                    goToEventDetails = {}
                )
            }
        }
    }

    @Test
    fun showsOnlyFirstEventWhenCollapsed() = runTest {
        composeTestRule.onNodeWithText("Event 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Event 2").assertIsNotDisplayed()
        composeTestRule.onNodeWithText("Event 3").assertIsNotDisplayed()
    }

    @Test
    fun showsThreeEventsWhenExpanded() = runTest {
        composeTestRule.onNodeWithContentDescription("Expand/Collapse", ignoreCase = true).performClick()
        composeTestRule.onNodeWithText("Event 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Event 2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Event 3").assertIsDisplayed()
    }
}