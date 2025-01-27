package com.nfq.data.domain.repository

import arrow.core.Either
import com.nfq.data.database.entity.EventEntity
import com.nfq.data.database.entity.UserEntity
import com.nfq.data.database.entity.VoucherEntity
import com.nfq.data.domain.model.EventDetailsModel
import com.nfq.data.network.exception.DataException
import kotlinx.coroutines.flow.Flow

interface NFQSummitRepository {
    val events: Flow<List<EventEntity>>
    val vouchers: Flow<List<VoucherEntity>>
    val upcomingEvents: Flow<List<EventEntity>>
    val savedEvents: Flow<List<EventEntity>>
    val user: Flow<UserEntity?>
    val isCompletedOnboarding: Flow<Boolean>
    val isShownNotificationPermissionDialog: Flow<Boolean>
    val isEnabledNotification: Flow<Boolean>
    suspend fun authenticateWithAttendeeCode(attendeeCode: String): Either<DataException, Unit>
    suspend fun logout(): Either<DataException, Unit>
    suspend fun fetchEventActivities(forceUpdate: Boolean = false): Either<DataException, Unit>
    suspend fun getEventActivityByID(id: String): Either<DataException, EventDetailsModel>
    suspend fun updateFavorite(eventId: String, isFavorite: Boolean): Either<DataException, Unit>
    suspend fun updateOnboardingStatus(isCompletedOnboarding: Boolean)
    suspend fun updateNotificationSetting(
        isShownNotificationPermissionDialog: Boolean,
        isEnabledNotification: Boolean
    )

    suspend fun getMealVouchers(): Either<DataException, Unit>
}