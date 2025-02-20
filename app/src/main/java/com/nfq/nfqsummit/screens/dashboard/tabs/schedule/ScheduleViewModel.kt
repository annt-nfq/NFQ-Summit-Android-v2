package com.nfq.nfqsummit.screens.dashboard.tabs.schedule

import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.model.CategoryType.Companion.filterSummitOrK5
import com.nfq.data.domain.model.CategoryType.Companion.filterTechRock
import com.nfq.data.domain.model.SummitEvent
import com.nfq.data.domain.repository.NFQSummitRepository
import com.nfq.nfqsummit.components.ScheduleSize
import com.nfq.nfqsummit.isSame
import com.nfq.nfqsummit.mapper.toSubmitEvents
import com.nfq.nfqsummit.utils.UserMessageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
    private val _currentTime = MutableStateFlow(LocalTime.now())
    private val _uiState = combine(
        _selectedDate,
        repository.user.map { it != null },
        repository.events.map { it.toSubmitEvents() }
    ) { oldSelectedDate, isLoggedIn, events ->

        val today = LocalDate.now()
        val firstEventDate = events.firstOrNull()?.start?.toLocalDate() ?: today
        val lastEventDate = events.lastOrNull()?.start?.toLocalDate() ?: today

        val distinctDates = events
            .mapNotNull { it.start.toLocalDate() }
            .distinct()
            .sorted()

        val selectedDate = oldSelectedDate ?: when {
            distinctDates.any { it.isSame(today) } -> today
            today < firstEventDate -> firstEventDate
            today > lastEventDate -> lastEventDate
            else -> distinctDates.firstOrNull { it > today } ?: today
        }

        val dayEventPairs = distinctDates.map { date ->
            date to events.filter { event ->
                event.start.toLocalDate() == date
            }.sortedBy { it.start }.toPersistentList()
        }.toPersistentList()

        val dailyEvents = dayEventPairs
            .firstOrNull { (date, _) -> date.isSame(selectedDate) }
            ?.second
            ?: persistentListOf()

        val summitEvents = dailyEvents.toSummitEvents()
        val techRockEvents = dailyEvents.toTechRockEvents()

        ScheduleUIState(
            isLoggedIn = isLoggedIn,
            selectedDate = selectedDate,
            dailyEvents = dailyEvents,
            dayEventPairs = dayEventPairs,
            techRockEvents = techRockEvents,
            summitEvents = summitEvents,
            hourSize = ScheduleSize.FixedSize(130.dp)
        )
    }

    val uiState = combine(
        _uiState,
        _currentTime
    ) { state, currentTime ->
        state.copy(currentTime = currentTime)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ScheduleUIState()
    )

    init {
        fetchEventActivities()
        updateCurrentTime()
    }

    private fun updateCurrentTime() {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                delay(60000L) // Update every minute
                _currentTime.value = LocalTime.now()
            }
        }
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

fun PersistentList<SummitEvent>.toTechRockEvents(): PersistentList<SummitEvent> {
    return this.filter { filterTechRock(it.category.code) }.toPersistentList()
}

fun PersistentList<SummitEvent>.toSummitEvents(): PersistentList<SummitEvent> {
    return this.filter { filterSummitOrK5(it.category.code) }.toPersistentList()
}

data class ScheduleUIState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val selectedDate: LocalDate = LocalDate.MIN,
    val currentTime: LocalTime = LocalTime.now(),
    val dailyEvents: PersistentList<SummitEvent> = persistentListOf(),
    val summitEvents: PersistentList<SummitEvent> = persistentListOf(),
    val techRockEvents: PersistentList<SummitEvent> = persistentListOf(),
    val hourSize: ScheduleSize = ScheduleSize.FixedSize(130.dp),
    val dayEventPairs: PersistentList<Pair<LocalDate, PersistentList<SummitEvent>>> = persistentListOf(),
)