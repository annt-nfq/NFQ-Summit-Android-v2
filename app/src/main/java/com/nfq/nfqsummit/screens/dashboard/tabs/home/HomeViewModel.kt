package com.nfq.nfqsummit.screens.dashboard.tabs.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.model.VoucherModel
import com.nfq.data.domain.repository.NFQSummitRepository
import com.nfq.data.filterOutTechRock
import com.nfq.nfqsummit.components.ImageCache
import com.nfq.nfqsummit.mapper.toSavedEventUIModels
import com.nfq.nfqsummit.mapper.toUpcomingEventUIModels
import com.nfq.nfqsummit.mapper.toUserUIModel
import com.nfq.nfqsummit.model.SavedEventUIModel
import com.nfq.nfqsummit.model.UpcomingEventUIModel
import com.nfq.nfqsummit.model.UserUIModel
import com.nfq.nfqsummit.utils.UserMessageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NFQSummitRepository,
    application: Application
) : AndroidViewModel(application) {
    private val loadingFlow = MutableStateFlow(false)
    private val vouchersFlow = MutableStateFlow<List<VoucherModel>>(emptyList())

    val showReminderDialog = combine(
        repository.user,
        repository.isShownNotificationPermissionDialog
    ) { user, isShownNotificationPermissionDialog ->
        !isShownNotificationPermissionDialog && user != null
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val uiState = combine(
        repository.user,
        repository.upcomingEvents,
        repository.savedEvents,
        loadingFlow,
        vouchersFlow
    ) { user, events, savedEvents, isLoading,vouchers ->
        val upcomingEvents = events.toUpcomingEventUIModels()
        val qrCodeBitmap = user?.qrCodeUrl?.let {
            ImageCache(context = application.applicationContext).getImage(it)
        }
        HomeUIState(
            isLoading = isLoading,
            user = user?.toUserUIModel(qrCodeBitmap),
            upcomingEvents = upcomingEvents,
            upcomingEventsWithoutTechRocks = upcomingEvents.filter { it.category.code.filterOutTechRock() },
            savedEvents = savedEvents.toSavedEventUIModels(),
            vouchers = vouchers.groupBy { it.date }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUIState()
    )

    init {
        fetchEventActivities()
        fetchVouchers()
    }

    private fun fetchVouchers() {
        viewModelScope.launch(Dispatchers.IO) {
            repository
                .getMealVouchers()
                .onLeft { e -> UserMessageManager.showMessage(e) }
                .onRight { vouchersFlow.value = it }
        }
    }

    private fun fetchEventActivities() {
        viewModelScope.launch(Dispatchers.IO) {
            updateLoadingStateIfNeeded()
            repository
                .fetchEventActivities(forceUpdate = true)
                .onLeft { e ->
                    loadingFlow.value = false
                    UserMessageManager.showMessage(e)
                }
                .onRight {
                    loadingFlow.value = false
                }
        }
    }

    private suspend fun updateLoadingStateIfNeeded() {
        val events = repository.upcomingEvents.firstOrNull()
        loadingFlow.value = events.isNullOrEmpty()
    }

    fun markAsFavorite(favorite: Boolean, eventId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFavorite(eventId, favorite)
        }
    }

    fun updateNotificationSetting(
        isShownNotificationPermissionDialog: Boolean,
        isEnabledNotification: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateNotificationSetting(
                isShownNotificationPermissionDialog,
                isEnabledNotification
            )
        }
    }
}


data class HomeUIState(
    val user: UserUIModel? = null,
    val vouchers: Map<String, List<VoucherModel>> = emptyMap(),
    val upcomingEvents: List<UpcomingEventUIModel> = emptyList(),
    val upcomingEventsWithoutTechRocks: List<UpcomingEventUIModel> = emptyList(),
    val savedEvents: List<SavedEventUIModel> = emptyList(),
    val isLoading: Boolean = false
)