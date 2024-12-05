package com.nfq.data.remote.repository

import arrow.core.Either
import com.nfq.data.database.dao.EventDao
import com.nfq.data.database.dao.UserDao
import com.nfq.data.database.entity.EventEntity
import com.nfq.data.database.entity.UserEntity
import com.nfq.data.domain.model.EventDetailsModel
import com.nfq.data.domain.repository.NFQSummitRepository
import com.nfq.data.mapper.toEventEntities
import com.nfq.data.mapper.toUserEntity
import com.nfq.data.network.exception.DataException
import com.nfq.data.remote.datasource.NFQSummitDataSource
import com.nfq.data.remote.model.response.EventActivityResponse
import com.nfq.data.toFormattedDateTimeString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class NFQSummitRepositoryImpl @Inject constructor(
    private val dataSource: NFQSummitDataSource,
    private val eventDao: EventDao,
    private val userDao: UserDao
) : NFQSummitRepository {
    override val events: Flow<List<EventEntity>>
        get() = eventDao.getAllEvents()
    override val savedEvents: Flow<List<EventEntity>>
        get() = eventDao.getFavoriteEvents()
    override val user: Flow<UserEntity?>
        get() = userDao.getUser()

    override suspend fun authenticateWithAttendeeCode(attendeeCode: String): Either<DataException, Unit> {
        return dataSource
            .authenticateWithAttendeeCode(attendeeCode)
            .map { userDao.insertUser(it.toUserEntity()) }
    }

    override suspend fun logout(): Either<DataException, Unit> {
        return dataSource
            .logout()
            .map { userDao.deleteUser() }
    }

    override suspend fun fetchEventActivities(): Either<DataException, Unit> {
        return dataSource
            .getEventActivities()
            .map { latestEvents ->
                val favoriteEvents = eventDao.getFavoriteEvents().firstOrNull().orEmpty()
                val updatedEvents = updateEventsWithFavorites(latestEvents, favoriteEvents)
                eventDao.insertEvents(updatedEvents.toEventEntities())
            }
    }

    private fun updateEventsWithFavorites(
        latestEvents: List<EventActivityResponse>,
        favoriteEvents: List<EventEntity>
    ): List<EventActivityResponse> {
        return latestEvents.map { event ->
            event.copy(isFavorite = favoriteEvents.any { favoriteEvent -> favoriteEvent.id == event.id.toString() })
        }
    }

    override suspend fun getEventActivityByID(id: String): Either<DataException, EventDetailsModel> {
        return try {
            val details = eventDao
                .getEventById(id)
                .let {
                    val startTime = it
                        .timeStart
                        .toFormattedDateTimeString(targetPattern = "EEE, MMM d • HH:mm")
                    EventDetailsModel(
                        id = it.id,
                        startTime = startTime,
                        name = it.name,
                        description = it.description,
                        locationName = it.location,
                        latitude = it.latitude,
                        longitude = it.longitude,
                        isFavorite = it.isFavorite,
                        coverPhotoUrl = it.images.find { image -> image.isNotBlank() }.orEmpty()
                    )
                }
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
            eventDao.updateFavorite(eventId, isFavorite)
            Either.Right(Unit)
        } catch (e: Exception) {
            Either.Left(DataException.Api(e.message ?: e.localizedMessage ?: "Unknown error"))
        }
    }
}