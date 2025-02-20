package com.nfq.nfqsummit.screens.eventDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.model.EventDetailsModel
import com.nfq.data.domain.repository.NFQSummitRepository
import com.nfq.nfqsummit.analytics.helper.AnalyticsHelper
import com.nfq.nfqsummit.analytics.logSaveEvent
import com.nfq.nfqsummit.utils.UserMessageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventDetailsBottomSheetViewModel @Inject constructor(
    private val repository: NFQSummitRepository,
    private val analyticsHelper: AnalyticsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventDetailUIState())
    val uiState = combine(
        _uiState,
        repository.user.map { it?.attendeeCode }
    ) { state, attendeeCode ->
        state.copy(attendeeCode = attendeeCode.orEmpty())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = EventDetailUIState()
    )

    fun getEvent(eventId: String) {
        viewModelScope.launch {
            repository
                .getEventActivityByID(eventId)
                .onLeft {
                    UserMessageManager.showMessage(it)
                }
                .onRight { event ->
                    _uiState.update {
                        it.copy(event = event)
                    }
                }

        }
    }

    fun markEventAsFavorite(isFavorite: Boolean, eventId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isFavorite) {
                analyticsHelper.logSaveEvent(
                    attendeeCode = uiState.value.attendeeCode,
                    eventId = eventId,
                    eventTitle = uiState.value.event?.name.orEmpty()
                )
            }
            repository.updateFavorite(eventId, isFavorite)
            _uiState.update {
                it.copy(event = it.event?.copy(isFavorite = isFavorite))
            }
        }
    }
}

data class EventDetailUIState(
    val event: EventDetailsModel? = null,
    val attendeeCode: String = ""
)
