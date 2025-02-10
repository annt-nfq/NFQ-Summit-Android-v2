package com.nfq.nfqsummit.screens.transportation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.repository.ExploreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TransportationViewModel @Inject constructor(
    exploreRepository: ExploreRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val parentBlogId = savedStateHandle.get<String>("parentBlogId")!!
    var blogs = exploreRepository
        .getTransportationBlogs(parentBlogId)
        .stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(5000)
        )
}