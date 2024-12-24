package com.nfq.nfqsummit.screens.dashboard.tabs.schedule

import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.model.SummitEvent
import com.nfq.data.domain.repository.NFQSummitRepository
import com.nfq.nfqsummit.isSame
import com.nfq.nfqsummit.mapper.toSubmitEvents
import com.nfq.nfqsummit.utils.UserMessageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
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

        val today = LocalDate.now()
        val firstEventDate = events.firstOrNull()?.start?.toLocalDate() ?: today
        val lastEventDate = events.lastOrNull()?.end?.toLocalDate() ?: today

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
                event.start.toLocalDate() == date && event.start.hour < event.end.hour
            }.sortedBy { it.start }.toPersistentList()
        }.toPersistentList()

        // Extract events for the selected date
        val dailyEvents = dayEventPairs
            .filter { (date, _) -> date.isSame(selectedDate) }
            .flatMap { (_, events) -> events }
            .toPersistentList()

        ScheduleUIState(
            selectedDate = selectedDate!!,
            dailyEvents = dailyEvents,
            dayEventPairs = dayEventPairs
        )

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

    private fun fetchEventActivities() = viewModelScope.launch(Dispatchers.IO) {
        repository
            .fetchEventActivities()
            .onLeft { UserMessageManager.showMessage(it) }
    }
}

data class ScheduleUIState(
    val isLoading: Boolean = false,
    val selectedDate: LocalDate = LocalDate.now(),
    val currentTime: LocalTime = LocalTime.now(),
    val dailyEvents: PersistentList<SummitEvent> = persistentListOf(),
    val dayEventPairs: PersistentList<Pair<LocalDate, PersistentList<SummitEvent>>> = persistentListOf(),
)