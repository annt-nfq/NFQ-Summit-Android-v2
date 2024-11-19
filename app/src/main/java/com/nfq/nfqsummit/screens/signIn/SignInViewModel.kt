package com.nfq.nfqsummit.screens.signIn

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

enum class SignInStatus {
    initial, loading, success, failed,
}

@HiltViewModel
class SignInViewModel @Inject constructor(
) : ViewModel() {

    private val _signInStatus = MutableSharedFlow<SignInStatus>()
    val signInStatus = _signInStatus.asSharedFlow()

    fun signIn(id: String) {
        viewModelScope.launch {
            _signInStatus.emit(SignInStatus.loading)
            delay(2000)

            if (id.isNotEmpty()) {
                _signInStatus.emit(SignInStatus.success)
            } else {
                _signInStatus.emit(SignInStatus.failed)
            }
        }
    }

    fun resetState() {
        viewModelScope.launch {
            _signInStatus.emit(SignInStatus.initial)
        }
    }
}