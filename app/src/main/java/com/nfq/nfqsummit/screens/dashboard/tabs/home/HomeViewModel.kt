package com.nfq.nfqsummit.screens.dashboard.tabs.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.repository.NFQSummitRepository
import com.nfq.nfqsummit.mapper.toSavedEventUIModels
import com.nfq.nfqsummit.mapper.toUpcomingEventUIModels
import com.nfq.nfqsummit.model.SavedEventUIModel
import com.nfq.nfqsummit.model.UpcomingEventUIModel
import com.nfq.nfqsummit.utils.UserMessageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val nfqSummitRepository: NFQSummitRepository
) : ViewModel() {
    private val loadingFlow = MutableStateFlow(false)

    val uiState = combine(
        nfqSummitRepository.events,
        nfqSummitRepository.savedEvents,
        loadingFlow
    ) { events, savedEvents, isLoading ->
        HomeUIState(
            isLoading = isLoading,
            upcomingEvents = events.sortedBy { it.timeStart }.take(3).toUpcomingEventUIModels(),
            savedEvents = savedEvents.toSavedEventUIModels()
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
            updateLoadingStateIfNeeded()
            nfqSummitRepository
                .fetchEventActivities()
                .onLeft { e ->
                    loadingFlow.value = false
                    UserMessageManager.showMessage(e)
                }
                .onRight{
                    loadingFlow.value = false
                }
        }
    }

    private suspend fun updateLoadingStateIfNeeded() {
        val events = nfqSummitRepository.events.firstOrNull()
        loadingFlow.value = events.isNullOrEmpty()
    }

    fun markAsFavorite(favorite: Boolean, eventId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            nfqSummitRepository.updateFavorite(eventId, favorite)
        }
    }
}


data class HomeUIState(
    val upcomingEvents: List<UpcomingEventUIModel> = emptyList(),
    val savedEvents: List<SavedEventUIModel> = emptyList(),
    val isLoading: Boolean = false
)