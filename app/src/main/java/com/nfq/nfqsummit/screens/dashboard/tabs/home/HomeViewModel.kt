package com.nfq.nfqsummit.screens.dashboard.tabs.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.repository.NFQSummitRepository
import com.nfq.nfqsummit.mapper.toSavedEventUIModels
import com.nfq.nfqsummit.mapper.toUpcomingEventUIModels
import com.nfq.nfqsummit.mapper.toUserUIModel
import com.nfq.nfqsummit.model.SavedEventUIModel
import com.nfq.nfqsummit.model.UpcomingEventUIModel
import com.nfq.nfqsummit.model.UserUIModel
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
    private val repository: NFQSummitRepository
) : ViewModel() {
    private val loadingFlow = MutableStateFlow(false)

    val uiState = combine(
        repository.user,
        repository.events,
        repository.savedEvents,
        loadingFlow
    ) { user, events, savedEvents, isLoading ->
        HomeUIState(
            isLoading = isLoading,
            user = user?.toUserUIModel(),
            upcomingEvents = events.sortedBy { it.timeStart }.take(3).toUpcomingEventUIModels(),
            savedEvents = savedEvents.toSavedEventUIModels()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUIState()
    )

    init {
        fetchEventActivities()
    }

    private fun fetchEventActivities() {
        viewModelScope.launch(Dispatchers.IO) {
            updateLoadingStateIfNeeded()
            repository
                .fetchEventActivities(forceUpdate = true)
                .onLeft { e ->
                    loadingFlow.value = false
                    UserMessageManager.showMessage(e)
                }
                .onRight {
                    loadingFlow.value = false
                }
        }
    }

    private suspend fun updateLoadingStateIfNeeded() {
        val events = repository.events.firstOrNull()
        loadingFlow.value = events.isNullOrEmpty()
    }

    fun markAsFavorite(favorite: Boolean, eventId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFavorite(eventId, favorite)
        }
    }
}


data class HomeUIState(
    val user: UserUIModel? = null,
    val upcomingEvents: List<UpcomingEventUIModel> = emptyList(),
    val savedEvents: List<SavedEventUIModel> = emptyList(),
    val isLoading: Boolean = false
)