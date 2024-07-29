package com.nfq.data.remote.repository

import com.nfq.data.domain.model.Attraction
import com.nfq.data.domain.model.Response
import com.nfq.data.domain.model.toAttraction
import com.nfq.data.domain.repository.AttractionRepository
import com.nfq.data.local.AttractionLocal
import com.nfq.data.remote.AttractionRemote
import javax.inject.Inject

class AttractionRepositoryImpl @Inject constructor(
    private val attractionRemote: AttractionRemote,
    private val attractionLocal: AttractionLocal
) : AttractionRepository {

    override suspend fun getAllAttractions(forceReload: Boolean): Response<List<Attraction>> {
        val cachedAttractions = attractionLocal.getAllAttractions()
        return if (cachedAttractions.isNotEmpty() && !forceReload)
            Response.Success(cachedAttractions.map { it.toAttraction() })
        else
            Response.Success(attractionRemote.getAllAttractions().also {
                attractionLocal.clearAllAttractions()
                attractionLocal.insertAttractions(it)
            }.map { it.toAttraction() })
    }

    override suspend fun getAttractionById(attractionId: Int): Response<Attraction> {
        return attractionLocal.getAttractionById(attractionId)?.let {
            return Response.Success(it.toAttraction())
        } ?: Response.Success(attractionRemote.getAttractionById(attractionId).toAttraction())
    }
}