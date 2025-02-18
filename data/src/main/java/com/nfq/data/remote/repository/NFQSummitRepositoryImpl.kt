package com.nfq.data.remote.repository

import arrow.core.Either
import com.nfq.data.database.dao.EventDao
import com.nfq.data.database.dao.UserDao
import com.nfq.data.database.dao.VouchersDao
import com.nfq.data.database.entity.EventEntity
import com.nfq.data.database.entity.UserEntity
import com.nfq.data.database.entity.VoucherEntity
import com.nfq.data.datastore.PreferencesDataSource
import com.nfq.data.domain.model.EventDetailsModel
import com.nfq.data.domain.repository.NFQSummitRepository
import com.nfq.data.mapper.toEventDetailsModel
import com.nfq.data.mapper.toEventEntity
import com.nfq.data.mapper.toUserEntity
import com.nfq.data.network.exception.DataException
import com.nfq.data.remote.datasource.NFQSummitDataSource
import com.nfq.data.remote.model.response.EventActivityResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import javax.inject.Inject

class NFQSummitRepositoryImpl @Inject constructor(
    private val dataSource: NFQSummitDataSource,
    private val preferencesDataSource: PreferencesDataSource,
    private val eventDao: EventDao,
    private val userDao: UserDao,
    private val vouchersDao: VouchersDao
) : NFQSummitRepository {
    override val events: Flow<List<EventEntity>>
        get() = eventDao.getAllEvents()
    override val vouchers: Flow<List<VoucherEntity>>
        get() = vouchersDao.getVouchers()
    override val upcomingEvents: Flow<List<EventEntity>>
        get() = eventDao.getUpcomingEvents(currentTime = System.currentTimeMillis())
    override val savedEvents: Flow<List<EventEntity>>
        get() = eventDao.getFavoriteEvents()
    override val user: Flow<UserEntity?>
        get() = userDao.getUser()
    override val isCompletedOnboarding: Flow<Boolean>
        get() = preferencesDataSource
            .appConfigDataFlow
            .map { it.isCompletedOnboarding }

    override val isShownNotificationPermissionDialog: Flow<Boolean>
        get() = preferencesDataSource
            .appConfigDataFlow
            .map { it.isShownNotificationPermissionDialog }
    override val isEnabledNotification: Flow<Boolean>
        get() = preferencesDataSource
            .appConfigDataFlow
            .map { it.isEnabledNotification }

    override suspend fun authenticateWithAttendeeCode(attendeeCode: String): Either<DataException, Unit> {
        return dataSource
            .authenticateWithAttendeeCode(attendeeCode)
            .map { userDao.insertUser(it.toUserEntity()) }
    }

    override suspend fun logout(): Either<DataException, Unit> {
        return dataSource
            .logout()
            .onLeft { clearUserData() }
            .map { clearUserData() }
    }

    private suspend fun clearUserData() {
        userDao.deleteUser()
        eventDao.deleteAllEvents()
        preferencesDataSource.updateNotificationSetting(
            isShownNotificationPermissionDialog = false,
            isEnabledNotification = false
        )
    }

    override suspend fun fetchEventActivities(forceUpdate: Boolean): Either<DataException, Unit> {
        return dataSource
            .getEventActivities(userDao.getRegistrantId().orEmpty())
            .onLeft {
                if (it is DataException.Api && it.errorCode == 401) {
                    userDao.deleteUser()
                }
            }
            .map { latestEvents ->
                val favoriteEvents = eventDao.getFavoriteEvents().firstOrNull().orEmpty()
                val updatedEvents = updateEventsWithFavorites(latestEvents, favoriteEvents)
                // if (forceUpdate) eventDao.deleteAllEvents()
                eventDao.insertEvents(updatedEvents)
            }
    }

    private fun updateEventsWithFavorites(
        latestEvents: List<EventActivityResponse>,
        favoriteEvents: List<EventEntity>
    ): List<EventEntity> {
        return latestEvents.map { event ->
            event
                .copy(isFavorite = favoriteEvents.any { favoriteEvent -> favoriteEvent.id == event.id.toString() })
                .toEventEntity()
        }
    }

    override suspend fun getEventActivityByID(id: String): Either<DataException, EventDetailsModel> {
        return try {
            val details = eventDao
                .getEventById(id)
                .toEventDetailsModel()

            Either.Right(details)
        } catch (e: Exception) {
            Either.Left(DataException.Api(e.message ?: e.localizedMessage ?: "Unknown error"))
        }
    }

    override suspend fun updateFavorite(
        eventId: String,
        isFavorite: Boolean
    ): Either<DataException, Unit> {
        return try {
            eventDao.updateFavorite(eventId, isFavorite, updatedAt = System.currentTimeMillis())
            Either.Right(Unit)
        } catch (e: Exception) {
            Either.Left(DataException.Api(e.message ?: e.localizedMessage ?: "Unknown error"))
        }
    }

    override suspend fun updateOnboardingStatus(isCompletedOnboarding: Boolean) {
        preferencesDataSource.updateOnboardingStatus(isCompletedOnboarding)
    }

    override suspend fun updateNotificationSetting(
        isShownNotificationPermissionDialog: Boolean,
        isEnabledNotification: Boolean
    ) {
        preferencesDataSource.updateNotificationSetting(
            isShownNotificationPermissionDialog,
            isEnabledNotification
        )
    }

    override suspend fun getMealVouchers(): Either<DataException, Unit> {
        return dataSource
            .getMealVouchers()
            .map { response ->
                val voucherEntities = response.map {
                    val vietnamPriceFormatter = DecimalFormat("#,###").apply {
                        decimalFormatSymbols = DecimalFormatSymbols().apply {
                            groupingSeparator = '.'
                            decimalSeparator = ','
                        }
                    }

                    VoucherEntity(
                        id = it.id.toString(),
                        type = it.type,
                        date = it.date,
                        locations = it.locations,
                        price = vietnamPriceFormatter.format(it.price),
                        imageUrl = it.imageUrl,
                        sponsorLogoUrls = it.sponsorLogoUrls
                    )
                }
                vouchersDao.deleteAllVouchers()
                vouchersDao.insertVouchers(voucherEntities)
            }
    }
}