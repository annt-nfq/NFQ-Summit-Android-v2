package com.nfq.nfqsummit.di

import com.nfq.data.domain.model.Attraction
import com.nfq.data.domain.model.Response
import com.nfq.data.domain.repository.AttractionRepository
import com.nfq.nfqsummit.mocks.mockAttraction
import javax.inject.Inject

class FakeAttractionRepository @Inject constructor(): AttractionRepository {
    override suspend fun getAllAttractions(forceReload: Boolean): Response<List<Attraction>> {
        return Response.Success(listOf())
    }

    override suspend fun getAttractionById(attractionId: Int): Response<Attraction> {
        return Response.Success(mockAttraction)
    }
}