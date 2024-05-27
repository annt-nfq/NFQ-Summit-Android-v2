package com.nfq.nfqsummit.screens.dashboard.tabs.home

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
class HomeViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    var upcomingEvents by mutableStateOf<List<SummitEvent>?>(null)
        private set

    init {
        getUpcomingEvents()
    }

    fun getUpcomingEvents() = viewModelScope.launch {
        val events = when (val response = eventRepository.getAllEvents()) {
            is Response.Success -> response.data ?: listOf()
            is Response.Failure -> listOf()
            is Response.Loading -> listOf()
        }

        upcomingEvents = events.sortedBy { it.start }.take(3)
    }
}