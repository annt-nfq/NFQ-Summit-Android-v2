package com.nfq.nfqsummit.screens.dashboard.tabs.schedule

import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.model.CategoryEnum
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
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

        val selectedDate = oldSelectedDate ?: when {
            today < firstEventDate -> firstEventDate
            today > lastEventDate -> lastEventDate
            else -> today
        }

        val distinctDates = events
            .mapNotNull { it.start.toLocalDate() }
            .distinct()
            .sorted()

        val dayEventPairs = distinctDates.map { date ->
            date to events.filter { event ->
                event.start.toLocalDate() == date && event.start.hour < event.end.hour
            }.sortedBy { it.start }.toPersistentList()
        }.toPersistentList()

        val dailyEvents = dayEventPairs
            .firstOrNull { (date, _) -> date.isSame(selectedDate) }
            ?.second
            ?: persistentListOf()

        val summitEvents = dailyEvents.toSummitEvents()
        val techRockEvents = dailyEvents.toTechRockEvents()

        val hourSize =
            if (techRockEvents.any { ChronoUnit.MINUTES.between(it.start, it.end) < 60 }) {
                ScheduleSize.FixedSize(220.dp)
            } else {
                ScheduleSize.FixedSize(130.dp)
            }

        ScheduleUIState(
            selectedDate = selectedDate,
            dailyEvents = dailyEvents,
            dayEventPairs = dayEventPairs,
            techRockEvents = techRockEvents,
            summitEvents = summitEvents,
            hourSize = hourSize
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

fun PersistentList<SummitEvent>.toTechRockEvents(): PersistentList<SummitEvent> {
    return this.filter { it.category == CategoryEnum.TECH_ROCK }.toPersistentList()
}

fun PersistentList<SummitEvent>.toSummitEvents(): PersistentList<SummitEvent> {
    return this.filter { it.category == CategoryEnum.SUMMIT || it.category == CategoryEnum.K5 }
        .toPersistentList()
}

data class ScheduleUIState(
    val isLoading: Boolean = false,
    val selectedDate: LocalDate = LocalDate.now(),
    val currentTime: LocalTime = LocalTime.now(),
    val dailyEvents: PersistentList<SummitEvent> = persistentListOf(),
    val summitEvents: PersistentList<SummitEvent> = persistentListOf(),
    val techRockEvents: PersistentList<SummitEvent> = persistentListOf(),
    val hourSize: ScheduleSize = ScheduleSize.FixedSize(130.dp),
    val dayEventPairs: PersistentList<Pair<LocalDate, PersistentList<SummitEvent>>> = persistentListOf(),
)