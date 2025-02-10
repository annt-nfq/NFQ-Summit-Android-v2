package com.nfq.nfqsummit.entry

import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.repository.NFQSummitRepository
import com.nfq.nfqsummit.base.BaseViewModel
import com.nfq.nfqsummit.utils.UserMessageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    repository: NFQSummitRepository
) : BaseViewModel<MainEvent>() {
    private val userMessageManager by lazy { UserMessageManager }

    init {
        handelUserMessage()
    }

    val screenState: StateFlow<ScreenState> = combine(
        repository.user,
        repository.isCompletedOnboarding
    ) { user, isCompletedOnboarding ->
        when {
            !isCompletedOnboarding -> ScreenState.OnBoardingScreen
            user != null -> ScreenState.DashboardScreen
            else -> ScreenState.SignInScreen
        }
    }.stateIn(
        scope = viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ScreenState.SplashScreen
    )

    private fun handelUserMessage() {
        viewModelScope.launch(Dispatchers.IO) {
            userMessageManager
                .messages
                .collectLatest { message ->
                    if (message != null) {
                        emitEvent(MainEvent.UserMessage(message))
                    } else {
                        emitEvent(MainEvent.Idle)
                    }
                }
        }
    }

    fun userMessageShown() {
        userMessageManager.userMessageShown()
    }
}

sealed interface ScreenState {
    data object SplashScreen : ScreenState
    data object SignInScreen : ScreenState
    data object OnBoardingScreen : ScreenState
    data object DashboardScreen : ScreenState
}