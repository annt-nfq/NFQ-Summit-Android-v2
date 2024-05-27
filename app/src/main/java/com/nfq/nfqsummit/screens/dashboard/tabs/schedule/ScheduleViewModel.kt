package com.nfq.nfqsummit.screens.dashboard.tabs.schedule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.model.Response
import com.nfq.data.domain.model.SummitEvent
import com.nfq.data.domain.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val eventRepository: EventRepository
): ViewModel() {
    var events by mutableStateOf(listOf<SummitEvent>())
        private set

    var selectedDate: LocalDate by mutableStateOf(LocalDate.now())

    val currentTime: LocalTime by mutableStateOf(LocalTime.now())

    var dayEventPair by mutableStateOf(listOf<Pair<LocalDate, List<SummitEvent>>>())

    init {
        getEvents()
    }

    private fun getEvents() = viewModelScope.launch {
        val response = eventRepository.getAllEvents()
        events = when (response) {
            is Response.Success -> response.data?.sortedBy { it.start }
                ?: listOf()

            is Response.Failure -> listOf()
            is Response.Loading -> listOf()
        }
        if (events.isNotEmpty()) {
            selectedDate =
                if (LocalDate.now() < events.first().start.toLocalDate()) {
                    events.first().start.toLocalDate()
                } else if (LocalDate.now() > events.last().end.toLocalDate()) {
                    events.last().end.toLocalDate()
                } else {
                    LocalDate.now()
                }
            val dates = events.map { it.start.toLocalDate() }.distinct().sortedBy { it }
            dayEventPair = dates.map { date ->
                Pair(
                    date,
                    events.filter { it.start.toLocalDate() == date && !it.isConference }
                        .sortedBy { it.start }
                )
            }
        }
    }
}