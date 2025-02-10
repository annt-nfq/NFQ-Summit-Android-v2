package com.nfq.nfqsummit.screens.eventDetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.model.EventDetailsModel
import com.nfq.data.domain.repository.NFQSummitRepository
import com.nfq.nfqsummit.utils.UserMessageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventDetailsBottomSheetViewModel @Inject constructor(
    private val repository: NFQSummitRepository
) :
    ViewModel() {

    var event by mutableStateOf<EventDetailsModel?>(null)
        private set


    fun getEvent(eventId: String) {
        viewModelScope.launch {
            repository
                .getEventActivityByID(eventId)
                .onLeft {
                    UserMessageManager.showMessage(it)
                }
                .onRight {
                    event = it
                }

        }
    }

    fun markEventAsFavorite(isFavorite: Boolean, eventId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFavorite(eventId, isFavorite)
            event = event?.copy(isFavorite = isFavorite)
        }
    }
}
