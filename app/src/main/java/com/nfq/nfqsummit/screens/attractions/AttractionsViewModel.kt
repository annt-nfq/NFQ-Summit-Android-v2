package com.nfq.nfqsummit.screens.attractions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.repository.ExploreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AttractionsViewModel @Inject constructor(
    exploreRepository: ExploreRepository
) : ViewModel() {

    var attractions = exploreRepository
        .attractions
        .stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(5000)
        )
}