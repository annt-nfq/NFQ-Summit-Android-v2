package com.nfq.data.local

import com.nfq.data.cache.SummitDatabase
import com.nfq.data.remote.model.SummitEventRemoteModel
import com.nfq.data.remote.model.toSummitEventRemoteModel
import javax.inject.Inject

class EventLocalImpl @Inject constructor(
    private val database: SummitDatabase
) : EventLocal {
    override suspend fun clearAllEvents() {
        database.summitDatabaseQueries.removeAllEvents()
    }

    override suspend fun getAllEvents(): List<SummitEventRemoteModel> {
        return database.summitDatabaseQueries.selectAllEvents().executeAsList().map {
            it.toSummitEventRemoteModel()
        }
    }

    override suspend fun getTechRocksEvents(): List<SummitEventRemoteModel> {
        return database.summitDatabaseQueries.selectTechRocksEvents().executeAsList().map {
            it.toSummitEventRemoteModel()
        }
    }

    override suspend fun insertEvents(events: List<SummitEventRemoteModel>) {
        database.summitDatabaseQueries.transaction {
            events.forEach {
                database.summitDatabaseQueries.insertEvent(
                    it.id,
                    it.createdAt,
                    it.title,
                    it.description,
                    it.eventStartTime,
                    it.eventEndTime,
                    it.eventType,
                    it.coverPhotoUrl,
                    it.locationName,
                    it.isConference,
                    it.ordering?.toLong(),
                    it.speakerName,
                    it.speakerPosition,
                    null,
                    it.iconUrl,
                )
            }
        }
    }

    override suspend fun getEventById(id: String): SummitEventRemoteModel? {
        return database.summitDatabaseQueries
            .selectEvent(id)
            .executeAsOneOrNull()
            ?.toSummitEventRemoteModel()
    }

    override suspend fun saveFavoriteEvent(eventId: String) {
        database.summitDatabaseQueries.insertFavoriteEvent(eventId)
    }

    override suspend fun getFavoriteEvent(eventId: String): String? {
        return database.summitDatabaseQueries.getFavoriteEvent(eventId).executeAsOneOrNull()
    }

    override suspend fun removeFavoriteEvent(eventId: String) {
        database.summitDatabaseQueries.removeFavoriteEvent(eventId)
    }
}