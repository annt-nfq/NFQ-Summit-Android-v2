package com.nfq.nfqsummit.screens.dashboard.tabs.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.nfqsummit.model.VoucherUIModel
import com.nfq.data.domain.repository.NFQSummitRepository
import com.nfq.data.filterOutTechRock
import com.nfq.nfqsummit.components.ImageCache
import com.nfq.nfqsummit.mapper.toSavedEventUIModels
import com.nfq.nfqsummit.mapper.toUpcomingEventUIModels
import com.nfq.nfqsummit.mapper.toUserUIModel
import com.nfq.nfqsummit.mapper.toVoucherUIModels
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NFQSummitRepository,
    application: Application
) : AndroidViewModel(application) {
    private val loadingFlow = MutableStateFlow(false)

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

    private val userFlow = repository.user.map {
        val qrCodeBitmap = it
            ?.qrCodeUrl
            ?.let { url -> ImageCache(context = application.applicationContext).getImage(url) }
        it?.toUserUIModel(qrCodeBitmap)
    }

    private val upcomingEventsFlow = repository
        .upcomingEvents
        .map { events ->
            events
                .toUpcomingEventUIModels()
                .filter { it.category.code.filterOutTechRock() }
        }

    private val savedEventsFlow = repository
        .savedEvents
        .map { it.toSavedEventUIModels() }

    val uiState = combine(
        userFlow,
        upcomingEventsFlow,
        savedEventsFlow,
        loadingFlow,
        repository.vouchers.map { it.toVoucherUIModels(application.applicationContext) }
    ) { user, upcomingEvents, savedEvents, isLoading, vouchers ->
        HomeUIState(
            isLoading = isLoading,
            user = user,
            upcomingEvents = upcomingEvents,
            upcomingEventsWithoutTechRocks = upcomingEvents,
            savedEvents = savedEvents,
            vouchers = vouchers
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
    val vouchers: Map<String, List<VoucherUIModel>> = emptyMap(),
    val upcomingEvents: List<UpcomingEventUIModel> = emptyList(),
    val upcomingEventsWithoutTechRocks: List<UpcomingEventUIModel> = emptyList(),
    val savedEvents: List<SavedEventUIModel> = emptyList(),
    val isLoading: Boolean = false
)