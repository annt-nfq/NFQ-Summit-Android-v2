package com.nfq.nfqsummit.screens.signIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.repository.NFQSummitRepository
import com.nfq.nfqsummit.utils.UserMessageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val repository: NFQSummitRepository
) : ViewModel() {

    val loading = MutableStateFlow(false)


    fun signIn(attendeeCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loading.value = true
            repository
                .authenticateWithAttendeeCode(attendeeCode)
                .onLeft {
                    loading.value = false
                    UserMessageManager.showMessage(it)
                }.onRight {
                    loading.value = false
                }

        }
    }
}