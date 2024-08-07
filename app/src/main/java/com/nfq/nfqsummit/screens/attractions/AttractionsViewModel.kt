package com.nfq.nfqsummit.screens.attractions

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.model.Attraction
import com.nfq.data.domain.model.Response
import com.nfq.data.domain.repository.AttractionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttractionsViewModel @Inject constructor(
    private val attractionRepository: AttractionRepository
): ViewModel() {

    var attractions by mutableStateOf<List<Attraction>?>(emptyList())
        private set

    fun getAttractions() {
        viewModelScope.launch {
            when(val response = attractionRepository.getAllAttractions(forceReload = false)) {
                is Response.Success -> {
                    attractions = response.data
                }
                is Response.Loading -> {
                }
                is Response.Failure -> {
                }
            }
        }
    }
}