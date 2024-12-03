package com.nfq.nfqsummit.screens.dashboard.tabs.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.model.SummitEvent
import com.nfq.data.domain.repository.NFQSummitRepository
import com.nfq.nfqsummit.isSame
import com.nfq.nfqsummit.mapper.toSubmitEvents
import com.nfq.nfqsummit.utils.UserMessageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: NFQSummitRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow<LocalDate?>(null)

    val uiState = combine(
        _selectedDate,
        repository.events.map { it.toSubmitEvents() }
    ) { oldSelectedDate, events ->
        if (events.isNotEmpty()) {
            val today = LocalDate.now()
            val firstEventDate = events.first().start.toLocalDate()
            val lastEventDate = events.last().end.toLocalDate()

            val selectedDate = when {
                oldSelectedDate != null -> oldSelectedDate
                today < firstEventDate -> firstEventDate
                today > lastEventDate -> lastEventDate
                else -> today
            }
            // Generate a sorted list of unique event dates
            val distinctDates = events
                .mapNotNull { it.start.toLocalDate() }
                .distinct()
                .sorted()

            // Pair each date with its corresponding non-conference events
            val dayEventPairs = distinctDates.map { date ->
                date to events.filter { event ->
                    event.start.toLocalDate() == date && !event.isConference
                }.sortedBy { it.start }
            }

            // Extract events for the selected date
            val dailyEvents = dayEventPairs
                .filter { (date, _) -> date.isSame(selectedDate) }
                .flatMap { (_, events) -> events }

            ScheduleUIState(
                events = events,
                selectedDate = selectedDate!!,
                dayEventPairs = dayEventPairs,
                dailyEvents = dailyEvents
            )
        } else {
            ScheduleUIState()
        }

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ScheduleUIState()
    )

    init {
        fetchEventActivities()
    }


    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }

    private fun fetchEventActivities() = viewModelScope.launch {
        repository
            .fetchEventActivities()
            .onLeft { UserMessageManager.showMessage(it) }
    }
}

data class ScheduleUIState(
    val isLoading: Boolean = false,
    val events: List<SummitEvent> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val currentTime: LocalTime = LocalTime.now(),
    val dayEventPairs: List<Pair<LocalDate, List<SummitEvent>>> = emptyList(),
    val dailyEvents: List<SummitEvent> = emptyList()
)