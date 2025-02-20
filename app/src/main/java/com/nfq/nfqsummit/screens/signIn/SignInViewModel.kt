package com.nfq.nfqsummit.screens.signIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.repository.NFQSummitRepository
import com.nfq.nfqsummit.analytics.helper.AnalyticsHelper
import com.nfq.nfqsummit.analytics.logLoginWithQrCodeFail
import com.nfq.nfqsummit.analytics.logLoginWithQrCodeSuccess
import com.nfq.nfqsummit.utils.UserMessageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val repository: NFQSummitRepository,
    private val analyticsHelper: AnalyticsHelper
) : ViewModel() {

    val loading = MutableStateFlow(false)


    fun signIn(attendeeCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (loading.value) return@launch
            loading.value = true
            repository
                .authenticateWithAttendeeCode(attendeeCode)
                .onLeft {
                    analyticsHelper.logLoginWithQrCodeFail(attendeeCode, it.message.orEmpty())
                    loading.value = false
                    UserMessageManager.showMessage(it)
                }.onRight {
                    analyticsHelper.logLoginWithQrCodeSuccess(attendeeCode)
                    loading.value = false
                }

        }
    }
}