package com.nfq.nfqsummit.screens.dashboard.tabs.techRocks

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
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class TechRocksViewModel @Inject constructor(
    private val eventRepository: EventRepository
): ViewModel() {

    var events by mutableStateOf(listOf<SummitEvent>())
        private set

    val currentTime: LocalTime by mutableStateOf(LocalTime.now())

    init {
        getEvents()
    }

    private fun getEvents() = viewModelScope.launch {
        val response = eventRepository.getTechRocksEvents()
        events = when (response) {
            is Response.Success -> response.data?.sortedBy { it.start }
                ?: listOf()

            is Response.Failure -> listOf()
            is Response.Loading -> listOf()
        }
    }
}