package com.nfq.nfqsummit.screens.dashboard.tabs.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.nfq.data.domain.model.Blog
import com.nfq.data.domain.model.Response
import com.nfq.data.domain.model.SummitEvent
import com.nfq.data.domain.repository.BlogRepository
import com.nfq.data.domain.repository.EventRepository
import com.nfq.data.domain.repository.NFQSummitRepository
import com.nfq.nfqsummit.BaseComposeTest
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class HomeTabKtTest : BaseComposeTest() {

    private lateinit var viewModel: HomeViewModel

    @RelaxedMockK
    private lateinit var eventRepository: EventRepository

    @RelaxedMockK
    private lateinit var blogRepository: BlogRepository

    @RelaxedMockK
    private lateinit var nfqSummitRepository: NFQSummitRepository


    @Before
    @Throws(Exception::class)
    override fun setup() {
        eventRepository = mockk()
        blogRepository = mockk()
        coEvery {
            blogRepository.getRecommendedBlogs()
        } returns MutableStateFlow(Response.Success(listOf()))
        coEvery {
            blogRepository.getFavoriteBlogs()
        } returns MutableStateFlow<Response<List<Blog>>>(Response.Success(listOf()))
        coEvery {
            eventRepository.fetchAllEvents(any())
        } returns Result.success(
            listOf(
                SummitEvent(
                    id = "1",
                    name = "Event 1",
                    start = LocalDateTime.of(2024, 1, 6, 10, 0),
                    end = LocalDateTime.of(2024, 1, 6, 11, 0),
                    tag = "\uD83D\uDCBC Summit"
                ),
                SummitEvent(
                    id = "2",
                    name = "Event 2",
                    start = LocalDateTime.of(2024, 1, 6, 11, 0),
                    end = LocalDateTime.of(2024, 1, 6, 12, 0),
                    tag = "\uD83D\uDCBC Summit"
                ),
                SummitEvent(
                    id = "3",
                    name = "Event 3",
                    start = LocalDateTime.of(2024, 1, 6, 12, 0),
                    end = LocalDateTime.of(2024, 1, 6, 13, 0),
                    tag = "\uD83D\uDCBC Summit"
                )
            )
        )
        /*viewModel = HomeViewModel(eventRepository, blogRepository)
        composeTestRule.setContent {
            HomeTab(
                viewModel = viewModel,
                goToAttractions = {}
            )
        }*/
    }


    @Test
    fun `shows only first event when collapsed`() = runTest {
        composeTestRule.onNodeWithText("Event 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Event 2").assertIsNotDisplayed()
        composeTestRule.onNodeWithText("Event 3").assertIsNotDisplayed()
    }

    @Test
    fun `shows three events when expanded`() = runTest {
        composeTestRule.onNodeWithContentDescription("Expand/Collapse", ignoreCase = true)
            .performClick()
        composeTestRule.onNodeWithText("Event 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Event 2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Event 3").assertIsDisplayed()
    }
}