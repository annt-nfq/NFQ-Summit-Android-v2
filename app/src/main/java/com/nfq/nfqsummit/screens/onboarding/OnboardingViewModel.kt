package com.nfq.nfqsummit.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.repository.NFQSummitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: NFQSummitRepository
) : ViewModel() {

    fun updateOnboardingStatus(isCompletedOnboarding: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateOnboardingStatus(isCompletedOnboarding)
        }
    }
}