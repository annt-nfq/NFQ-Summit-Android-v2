package com.nfq.data.remote.repository

import arrow.core.Either
import com.nfq.data.domain.model.EventDetailsModel
import com.nfq.data.domain.repository.NFQSummitRepository
import com.nfq.data.network.exception.DataException
import com.nfq.data.remote.datasource.NFQSummitDataSource
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class NFQSummitRepositoryImpl @Inject constructor(
    private val dataSource: NFQSummitDataSource
) : NFQSummitRepository {
    override suspend fun authenticateWithAttendeeCode(attendeeCode: String): Either<DataException, Unit> {
        return dataSource
            .authenticateWithAttendeeCode(attendeeCode)
            .map {  }
    }

    override suspend fun fetchProfile(): Either<DataException, Unit> {
        return dataSource
            .getProfile()
            .map {  }
    }

    override suspend fun fetchEventActivities(): Either<DataException, Unit> {
        return dataSource
            .getEventActivities()
            .map {  }
    }

    override suspend fun getEventActivityByID(id: String): Either<DataException, EventDetailsModel> {
        return dataSource.getEventActivityByID(id)
            .map {
                EventDetailsModel(
                    id = it.id,
                    startTime = it.timeStart.format(DateTimeFormatter.ofPattern("EEE, MMM d • HH:mm")),
                    name = it.name,
                    description = it.description,
                    locationName = it.location,
                    latitude = it.latitude,
                    longitude = it.longitude,
                    isFavorite = false
                )
            }
    }

}