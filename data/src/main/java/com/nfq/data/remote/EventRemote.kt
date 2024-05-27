package com.nfq.data.remote

import com.nfq.data.remote.model.SummitEventRemoteModel

interface EventRemote {
    suspend fun getAllEvents(): List<SummitEventRemoteModel>

    suspend fun getTechRocksEvents(): List<SummitEventRemoteModel>

    suspend fun getEventById(eventId: String): SummitEventRemoteModel
}