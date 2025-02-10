package com.nfq.nfqsummit.screens.blog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.repository.ExploreRepository
import com.nfq.data.network.utils.networkConnectivity.ConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BlogViewModel @Inject constructor(
    exploreRepository: ExploreRepository,
    savedStateHandle: SavedStateHandle,
    connectivityObserver: ConnectivityObserver
) : ViewModel() {
    private val blogId = savedStateHandle.get<String>("blogId")!!
    var blog = exploreRepository
        .getBlogDetails(blogId = blogId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    val networkStatus  = connectivityObserver.observe().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ConnectivityObserver.Status.Unavailable
    )
}