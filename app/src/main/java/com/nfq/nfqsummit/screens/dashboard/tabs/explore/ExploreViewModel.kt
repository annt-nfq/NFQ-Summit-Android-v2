package com.nfq.nfqsummit.screens.dashboard.tabs.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.model.CountryEnum
import com.nfq.data.domain.repository.ExploreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val exploreRepository: ExploreRepository
) : ViewModel() {

    fun configCountry(countryEnum: CountryEnum) {
        viewModelScope.launch(Dispatchers.IO) {
            exploreRepository.configCountry(countryEnum)
        }
    }
}