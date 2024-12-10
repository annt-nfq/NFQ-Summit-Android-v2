package com.nfq.nfqsummit.screens.blog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.repository.AttractionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BlogViewModel @Inject constructor(
    attractionRepository: AttractionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val blogId = savedStateHandle.get<String>("blogId")!!
    var blog = attractionRepository
        .getBlogDeatils(blogId = blogId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

}