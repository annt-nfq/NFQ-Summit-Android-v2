package com.nfq.nfqsummit.di

import arrow.core.Either
import com.nfq.data.database.entity.EventEntity
import com.nfq.data.database.entity.UserEntity
import com.nfq.data.database.entity.VoucherEntity
import com.nfq.data.domain.model.EventDetailsModel
import com.nfq.data.domain.repository.NFQSummitRepository
import com.nfq.data.mapper.toEventDetailsModel
import com.nfq.data.network.exception.DataException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update

class FakeNFQSummitRepository : NFQSummitRepository {
    // Mutable state flows to simulate repository state
    private val _events = MutableStateFlow<List<EventEntity>>(emptyList())
    private val _savedEvents = MutableStateFlow<List<EventEntity>>(emptyList())
    private val _user = MutableStateFlow<UserEntity?>(null)
    private val _isCompletedOnboarding = MutableStateFlow(false)
    private val _isShownNotificationPermissionDialog = MutableStateFlow(false)

    // Simulation control variables
    private var shouldFailAuthentication = false
    private var shouldFailLogout = false
    private var shouldFailFetchEvents = false
    private var shouldFailGetEventDetails = false
    private var shouldFailUpdateFavorite = false
    override val events: Flow<List<EventEntity>>
        get() = _events.asStateFlow()
    override val vouchers: Flow<List<VoucherEntity>>
        get() = flow { emit(emptyList()) }

    // Exposed flows
    override val upcomingEvents: Flow<List<EventEntity>> = _events.asStateFlow()
    override val savedEvents: Flow<List<EventEntity>> = _savedEvents.asStateFlow()
    override val user: Flow<UserEntity?> = _user.asStateFlow()
    override val isCompletedOnboarding: Flow<Boolean> = _isCompletedOnboarding.asStateFlow()
    override val isShownNotificationPermissionDialog: Flow<Boolean>
        get() = _isShownNotificationPermissionDialog.asStateFlow()
    override val isEnabledNotification: Flow<Boolean>
        get() = flow { emit(false) }

    // Simulation methods to control repository state
    fun setEvents(events: List<EventEntity>) {
        _events.value = events
    }

    fun setSavedEvents(savedEvents: List<EventEntity>) {
        _savedEvents.value = savedEvents
    }

    fun setUser(user: UserEntity?) {
        _user.value = user
    }

    fun setOnboardingStatus(isCompleted: Boolean) {
        _isCompletedOnboarding.value = isCompleted
    }

    fun setAuthenticationFailure(shouldFail: Boolean) {
        shouldFailAuthentication = shouldFail
    }

    fun setLogoutFailure(shouldFail: Boolean) {
        shouldFailLogout = shouldFail
    }

    fun setFetchEventsFailure(shouldFail: Boolean) {
        shouldFailFetchEvents = shouldFail
    }

    fun setGetEventDetailsFailure(shouldFail: Boolean) {
        shouldFailGetEventDetails = shouldFail
    }

    fun setUpdateFavoriteFailure(shouldFail: Boolean) {
        shouldFailUpdateFavorite = shouldFail
    }

    // Repository method implementations
    override suspend fun authenticateWithAttendeeCode(attendeeCode: String): Either<DataException, Unit> {
        return if (shouldFailAuthentication) {
            Either.Left(DataException.Api("Authentication failed"))
        } else {
            // Simulate successful authentication by creating a user
            val user = UserEntity(
                id = "test_user_${attendeeCode}",
                firstName = "Test",
                lastName = "User",
                email = "test@example.com",
                attendeeCode = attendeeCode,
                tk = "test_token",
                qrCodeUrl = "https://example.com/qr_code.png"
            )
            setUser(user)
            Either.Right(Unit)
        }
    }

    override suspend fun logout(): Either<DataException, Unit> {
        return if (shouldFailLogout) {
            Either.Left(DataException.Api("Logout failed"))
        } else {
            setUser(null)
            Either.Right(Unit)
        }
    }

    override suspend fun fetchEventActivities(forceUpdate: Boolean): Either<DataException, Unit> {
        return if (shouldFailFetchEvents) {
            Either.Left(DataException.Api("Fetch events failed"))
        } else {
            Either.Right(Unit)
        }
    }

    override suspend fun getEventActivityByID(id: String): Either<DataException, EventDetailsModel> {
        return if (shouldFailGetEventDetails) {
            Either.Left(DataException.Api("Get event details failed"))
        } else {
            // Find the event in the current events list
            val event = _events.value.find { it.id == id }

            event?.let {
                Either.Right(it.toEventDetailsModel())
            } ?: Either.Left(DataException.Api("Event not found"))
        }
    }

    override suspend fun updateFavorite(
        eventId: String,
        isFavorite: Boolean
    ): Either<DataException, Unit> {
        return if (shouldFailUpdateFavorite) {
            Either.Left(DataException.Api("Update favorite failed"))
        } else {
            // Update favorite status in the events list
            _events.update { events ->
                events.map { event ->
                    if (event.id == eventId) {
                        event.copy(isFavorite = isFavorite)
                    } else {
                        event
                    }
                }
            }
            Either.Right(Unit)
        }
    }

    override suspend fun updateOnboardingStatus(isCompletedOnboarding: Boolean) {
        _isCompletedOnboarding.value = isCompletedOnboarding
    }

    override suspend fun updateNotificationSetting(
        isShownNotificationPermissionDialog: Boolean,
        isEnabledNotification: Boolean
    ) {
        _isShownNotificationPermissionDialog.value = isShownNotificationPermissionDialog
    }

    override suspend fun getMealVouchers(): Either<DataException,Unit> {
        return Either.Right(Unit)
    }
}