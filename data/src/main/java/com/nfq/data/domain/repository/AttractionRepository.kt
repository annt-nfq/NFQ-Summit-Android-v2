package com.nfq.data.domain.repository

import com.nfq.data.domain.model.Attraction
import com.nfq.data.domain.model.Response

interface AttractionRepository {
    suspend fun getAllAttractions(forceReload: Boolean = false): Response<List<Attraction>>

    suspend fun getAttractionById(attractionId: Int): Response<Attraction>
}