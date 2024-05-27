package com.nfq.nfqsummit.screens.eventDetails

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
import javax.inject.Inject

@HiltViewModel
class EventDetailsViewModel @Inject constructor(
    private val eventRepository: EventRepository
) :
    ViewModel() {

    var isBookmarked by mutableStateOf(false)

    var event by mutableStateOf<SummitEvent?>(null)
        private set

    fun getEvent(eventId: String) {
        viewModelScope.launch {
            when (val response = eventRepository.getEventById(eventId)) {
                is Response.Success -> {
                    event = response.data
                    getSavedEvent(event!!.id)
                }
                is Response.Loading -> {
                }
                is Response.Failure -> {
                }
            }
        }
    }

    fun insertSavedEvent(eventId: String) {
        viewModelScope.launch {
            eventRepository.saveFavoriteEvent(eventId)
        }
    }

    fun deleteSavedEvent(eventId: String) {
        viewModelScope.launch {
            eventRepository.removeFavoriteEvent(eventId)
        }
    }

    fun getSavedEvent(eventId: String) {
        viewModelScope.launch {
            isBookmarked = eventRepository.isEventFavorite(eventId)
        }
    }
}
