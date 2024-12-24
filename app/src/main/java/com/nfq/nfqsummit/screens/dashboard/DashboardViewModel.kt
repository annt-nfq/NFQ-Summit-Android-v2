package com.nfq.nfqsummit.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.repository.NFQSummitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: NFQSummitRepository
) : ViewModel() {

    val loading = MutableStateFlow(false)
    private val _uiState = MutableStateFlow(DashboardUIState())
    val uiState: StateFlow<DashboardUIState> get() = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository
                .user
                .collectLatest { user ->
                    _uiState.update { it.copy(isLoggedIn = user != null) }
                }
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(loading = true) }
            delay(300)
            repository
                .logout()
                .onLeft {
                    _uiState.update { it.copy(loading = false) }
                }
                .onRight {
                    _uiState.update { it.copy(loading = false) }
                }
        }
    }
}

data class DashboardUIState(
    val loading: Boolean = false,
    val isLoggedIn: Boolean = false
)