package com.nfq.nfqsummit.screens.attractions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.model.CountryEnum
import com.nfq.data.domain.repository.AttractionRepository
import com.nfq.nfqsummit.utils.UserMessageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttractionsViewModel @Inject constructor(
    private val attractionRepository: AttractionRepository
) : ViewModel() {

    var attractions = attractionRepository
        .attractions
        .stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(5000)
        )

    init {

        getAttractions()
    }

    private fun getAttractions() {
        viewModelScope.launch(Dispatchers.IO) {
            attractionRepository.getAttractions()
                .onLeft {
                    UserMessageManager.showMessage(it)
                }
        }
    }
}