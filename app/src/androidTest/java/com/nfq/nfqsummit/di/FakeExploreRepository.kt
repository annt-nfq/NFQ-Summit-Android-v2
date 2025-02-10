package com.nfq.nfqsummit.di

import com.nfq.data.domain.model.Attraction
import com.nfq.data.domain.model.Response
import com.nfq.data.domain.repository.ExploreRepository
import com.nfq.nfqsummit.mocks.mockAttraction
import javax.inject.Inject

class FakeExploreRepository @Inject constructor(): ExploreRepository {
    override suspend fun getAllAttractions(forceReload: Boolean): Response<List<Attraction>> {
        return Response.Success(listOf())
    }

    override suspend fun getAttractionById(attractionId: Int): Response<Attraction> {
        return Response.Success(mockAttraction)
    }
}