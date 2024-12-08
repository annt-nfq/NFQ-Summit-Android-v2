package com.nfq.nfqsummit.screens.dashboard.tabs.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.nfq.data.network.di.RetrofitServiceModule
import com.nfq.nfqsummit.di.DataSourceModule
import com.nfq.nfqsummit.di.FakeNFQSummitRepository
import com.nfq.nfqsummit.di.NetworkModule
import com.nfq.nfqsummit.di.RepositoryModule
import com.nfq.nfqsummit.mocks.mockSavedEvents
import com.nfq.nfqsummit.mocks.mockUpcomingEvents
import com.nfq.nfqsummit.model.UserUIModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.every
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(
    RepositoryModule::class, DataSourceModule::class, NetworkModule::class,
    RetrofitServiceModule::class
)
class HomeTabTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: HomeViewModel

    @Before
    fun setup() {
        hiltRule.inject()

        mockViewModel = HomeViewModel(FakeNFQSummitRepository())

        composeTestRule.runOnUiThread {
            composeTestRule.setContent {
                HomeTab(
                    viewModel = mockViewModel,
                    goToAttractions = {}
                )
            }
        }
    }

    @Test
    fun homeTab_displaysUpcomingEvents() {
        // Arrange
        val uiState = MutableStateFlow(
            HomeUIState(
                upcomingEvents = mockUpcomingEvents,
                savedEvents = mockSavedEvents,
                isLoading = false
            )
        )
        every { mockViewModel.uiState } returns uiState

        // Act
        composeTestRule.setContent {
            HomeTab(
                viewModel = mockViewModel,
                goToAttractions = {},
                seeAllEvents = {},
                seeAllSavedEvents = {}
            )
        }

        // Assert
        mockUpcomingEvents.forEach { event ->
            composeTestRule.onNodeWithText(event.name).assertIsDisplayed()
        }
    }

    @Test
    fun homeTab_displaysSavedEvents() {
        // Arrange
        val uiState = MutableStateFlow(
            HomeUIState(
                upcomingEvents = mockUpcomingEvents,
                savedEvents = mockSavedEvents,
                isLoading = false
            )
        )
        every { mockViewModel.uiState } returns uiState

        // Act
        composeTestRule.setContent {
            HomeTab(
                viewModel = mockViewModel,
                goToAttractions = {},
                seeAllEvents = {},
                seeAllSavedEvents = {}
            )
        }

        // Assert
        mockSavedEvents.forEach { event ->
            composeTestRule.onNodeWithText(event.name).assertIsDisplayed()
        }
    }

    @Test
    fun homeTab_showsQRCodeSection_whenUserLoggedIn() {
        // Arrange
        val mockUser = UserUIModel(
            id = "1",
            name = "Test User",
            email = "test@example.com",
            qrCodeUrl = "https://example.com/qr",
            attendeeCode = "1234"
        )
        val uiState = MutableStateFlow(
            HomeUIState(
                user = mockUser,
                upcomingEvents = mockUpcomingEvents,
                savedEvents = mockSavedEvents,
                isLoading = false
            )
        )
        every { mockViewModel.uiState } returns uiState

        // Act
        composeTestRule.setContent {
            HomeTab(
                viewModel = mockViewModel,
                goToAttractions = {},
                seeAllEvents = {},
                seeAllSavedEvents = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Tap to show my QR Code").assertIsDisplayed()
    }


    @Test
    fun homeTab_noSavedEvents_showsEmptyState() {
        // Arrange
        val uiState = MutableStateFlow(
            HomeUIState(
                upcomingEvents = mockUpcomingEvents,
                savedEvents = emptyList(),
                isLoading = false
            )
        )
        every { mockViewModel.uiState } returns uiState

        // Act
        composeTestRule.setContent {
            HomeTab(
                viewModel = mockViewModel,
                goToAttractions = {},
                seeAllEvents = {},
                seeAllSavedEvents = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithText("No Favorites Yet!").assertIsDisplayed()
        composeTestRule.onNodeWithText("View Attractions").assertIsDisplayed()
    }

    @Test
    fun homeTab_loading_displaysLoadingIndicator() {
        // Arrange
        val uiState = MutableStateFlow(
            HomeUIState(
                isLoading = true
            )
        )
        every { mockViewModel.uiState } returns uiState

        // Act
        composeTestRule.setContent {
            HomeTab(
                viewModel = mockViewModel,
                goToAttractions = {},
                seeAllEvents = {},
                seeAllSavedEvents = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Loading").assertIsDisplayed()
    }
}