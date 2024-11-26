package com.nfq.nfqsummit.screens.dashboard.tabs.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.repository.EventRepository
import com.nfq.nfqsummit.model.SavedEventUIModel
import com.nfq.nfqsummit.model.UpcomingEventUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {
    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val uiState = combine(
        eventRepository.events,
        eventRepository.savedEvents
    ) { events, savedEvents ->
        HomeUIState(
            upcomingEvents = events.sortedBy { it.start }.take(3).map {
                UpcomingEventUIModel(
                    id = it.id,
                    name = it.name,
                    imageUrl = it.coverPhotoUrl.orEmpty(),
                    date = it.start.format(DateTimeFormatter.ofPattern("dd\nMMM")),
                    startAndEndTime = "${it.start.format(dateTimeFormatter)} - ${
                        it.end.format(
                            dateTimeFormatter
                        )
                    }",
                    isFavorite = it.isFavorite,
                    tag = "\uD83D\uDCBC Summit"
                )
            },
            savedEvents = savedEvents.map { data ->
                SavedEventUIModel(
                    id = data.id,
                    imageUrl = data.coverPhotoUrl.orEmpty(),
                    name = data.name,
                    date = data.start.format(DateTimeFormatter.ofPattern("EEE, MMM d • HH:mm")),
                    tag = "\uD83D\uDCBC Summit"
                )
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUIState()
    )

    init {
        fetchUpcomingEvents()
    }

    private fun fetchUpcomingEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            eventRepository.fetchAllEvents()
        }
    }

    fun markAsFavorite(favorite: Boolean, eventId: String) {
        viewModelScope.launch {
            eventRepository.markEventAsFavorite(favorite, eventId)
        }
    }
}


data class HomeUIState(
    val upcomingEvents: List<UpcomingEventUIModel> = emptyList(),
    val savedEvents: List<SavedEventUIModel> = emptyList(),
    val isLoading: Boolean = false
)