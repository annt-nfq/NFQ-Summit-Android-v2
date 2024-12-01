package com.nfq.nfqsummit.screens.savedEvents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.repository.EventRepository
import com.nfq.nfqsummit.mapper.toSavedEventUIModels
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SavedEventViewModel @Inject constructor(
    eventRepository: EventRepository
) : ViewModel() {

    val savedEvents = eventRepository
        .savedEvents
        .map { it.toSavedEventUIModels() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}