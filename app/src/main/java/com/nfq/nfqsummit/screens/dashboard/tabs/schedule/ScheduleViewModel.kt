package com.nfq.nfqsummit.screens.dashboard.tabs.schedule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.model.Response
import com.nfq.data.domain.model.SummitEvent
import com.nfq.data.domain.repository.EventRepository
import com.nfq.nfqsummit.utils.UserMessageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {
    var events by mutableStateOf(listOf<SummitEvent>())
        private set

    var selectedDate: LocalDate by mutableStateOf(LocalDate.now())

    val currentTime: LocalTime by mutableStateOf(LocalTime.now())

    var dayEventPair by mutableStateOf(listOf<Pair<LocalDate, List<SummitEvent>>>())

    init {
        getEvents()
    }

    private fun getEvents() = viewModelScope.launch {
        eventRepository.fetchAllEvents()
            .onFailure {
                // Handle failure by resetting events
                events = emptyList()
                // Optionally log or notify the user of the failure
                UserMessageManager.showMessage(it)
            }
            .onSuccess { result ->
                // Sort events by start time
                events = result.sortedBy { it.start }

                if (events.isNotEmpty()) {
                    // Determine the selected date
                    val today = LocalDate.now()
                    val firstEventDate = events.first().start.toLocalDate()
                    val lastEventDate = events.last().end.toLocalDate()

                    selectedDate = when {
                        today < firstEventDate -> firstEventDate
                        today > lastEventDate -> lastEventDate
                        else -> today
                    }

                    // Generate a list of unique event dates
                    val distinctDates = events.map { it.start.toLocalDate() }.distinct().sorted()

                    // Create pairs of dates and corresponding events
                    dayEventPair = distinctDates.map { date ->
                        date to events.filter { event ->
                            event.start.toLocalDate() == date && !event.isConference
                        }.sortedBy { it.start }
                    }
                }
            }
    }
}