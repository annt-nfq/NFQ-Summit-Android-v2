package com.nfq.nfqsummit.entry

import androidx.lifecycle.viewModelScope
import com.nfq.nfqsummit.base.BaseViewModel
import com.nfq.nfqsummit.utils.UserMessageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : BaseViewModel<MainEvent>() {
    private val userMessageManager by lazy { UserMessageManager }

    init {
        handelUserMessage()
    }

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