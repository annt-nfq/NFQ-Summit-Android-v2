package com.nfq.data.local

import com.nfq.data.cache.SummitDatabase
import com.nfq.data.remote.model.AttractionRemoteModel
import com.nfq.data.remote.model.toRemoteModel
import javax.inject.Inject

class AttractionLocalImpl @Inject constructor(
    private val database: SummitDatabase
) : AttractionLocal {
    override suspend fun getAllAttractions(): List<AttractionRemoteModel> {
        return database.summitDatabaseQueries.selectAllAttractions().executeAsList().map {
            it.toRemoteModel()
        }
    }

    override suspend fun getAttractionById(id: Int): AttractionRemoteModel? {
        return database.summitDatabaseQueries
            .selectAttraction(id.toLong())
            .executeAsOneOrNull()
            ?.toRemoteModel()
    }

    override suspend fun insertAttractions(attractions: List<AttractionRemoteModel>) {
        database.summitDatabaseQueries.transaction {
            attractions.forEach {
                database.summitDatabaseQueries.insertAttraction(
                    it.id.toLong(),
                    it.title,
                    it.icon
                )
            }
        }
    }

    override suspend fun clearAllAttractions() {
        database.summitDatabaseQueries.clearAttractions()
    }
}