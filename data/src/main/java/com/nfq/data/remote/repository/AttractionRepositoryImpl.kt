package com.nfq.data.remote.repository

import com.nfq.data.domain.model.Attraction
import com.nfq.data.domain.model.Response
import com.nfq.data.domain.model.toAttraction
import com.nfq.data.domain.repository.AttractionRepository
import com.nfq.data.remote.AttractionRemote
import javax.inject.Inject

class AttractionRepositoryImpl @Inject constructor(
    private val attractionRemote: AttractionRemote
) : AttractionRepository {

    override suspend fun getAllAttractions(forceReload: Boolean): Response<List<Attraction>> {
        return Response.Success(attractionRemote.getAllAttractions().map { it.toAttraction() })
    }

    override suspend fun getAttractionById(attractionId: Int): Response<Attraction> {
        return Response.Success(attractionRemote.getAttractionById(attractionId).toAttraction())
    }
}