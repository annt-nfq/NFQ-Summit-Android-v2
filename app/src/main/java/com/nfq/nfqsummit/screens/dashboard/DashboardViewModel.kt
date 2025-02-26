package com.nfq.nfqsummit.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.repository.NFQSummitRepository
import com.nfq.data.filterOutTechRock
import com.nfq.nfqsummit.analytics.helper.AnalyticsHelper
import com.nfq.nfqsummit.analytics.logSignOut
import com.nfq.nfqsummit.mapper.toUpcomingEventUIModels
import com.nfq.nfqsummit.model.UpcomingEventUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: NFQSummitRepository,
    private val analyticsHelper: AnalyticsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUIState())
    val uiState = combine(
        _uiState,
        repository.user,
        repository.isEnabledNotification,
        repository.upcomingEvents
    ) { state, user, isEnabledNotification, upcomingEvents ->
        val isLoggedIn = user != null
        state.copy(
            isLoggedIn = isLoggedIn,
            attendeeCode = user?.attendeeCode.orEmpty(),
            isEnabledNotification = isEnabledNotification && isLoggedIn,
            upcomingEventsWithoutTechRocks = upcomingEvents
                .filter { it.category?.code.filterOutTechRock() }
                .toUpcomingEventUIModels()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUIState()
    )

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(loading = true) }
            analyticsHelper.logSignOut(uiState.value.attendeeCode)
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

    fun updateNotificationSetting(
        isShownNotificationPermissionDialog: Boolean,
        isEnabledNotification: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateNotificationSetting(
                isShownNotificationPermissionDialog = isShownNotificationPermissionDialog,
                isEnabledNotification = isEnabledNotification
            )
        }
    }
}

data class DashboardUIState(
    val loading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val attendeeCode: String = "",
    val isEnabledNotification: Boolean = false,
    val upcomingEventsWithoutTechRocks: List<UpcomingEventUIModel> = emptyList()
)