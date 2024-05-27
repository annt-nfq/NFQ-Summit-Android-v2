package com.nfq.nfqsummit.screens.eventDetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nfq.data.domain.model.Response
import com.nfq.data.domain.model.SummitEvent
import com.nfq.data.domain.repository.EventRepository
import com.nfq.nfqsummit.UnconfinedCoroutineRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class EventDetailsViewModelTest {

    @get:Rule
    var coroutineRule = UnconfinedCoroutineRule()

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var repository: EventRepository
    private lateinit var viewModel: EventDetailsViewModel

    @Before
    fun setUp() {
        repository = mockk()
        coEvery { repository.getEventById(any()) } returns Response.Success(
            SummitEvent(
                "1",
                "Event",
                LocalDateTime.of(2022, 1, 1, 0, 0),
                LocalDateTime.of(2022, 1, 1, 1, 0),
                "Description",
                0.0, 0.0,
                "",
                "Type"
            )
        )

        coEvery {
            repository.isEventFavorite(any())
        } returns true

        viewModel = EventDetailsViewModel(repository)
    }

    @Test
    fun eventIsNotNullAfterFetch() = runTest(UnconfinedTestDispatcher()) {
        launch {
            viewModel.getEvent("1")
        }
        assert(viewModel.event != null)
    }
}
