package com.nfq.data.domain.repository

import arrow.core.Either
import com.nfq.data.domain.model.Attraction
import com.nfq.data.domain.model.CountryEnum
import com.nfq.data.domain.model.Response
import com.nfq.data.network.exception.DataException
import kotlinx.coroutines.flow.Flow

interface AttractionRepository {
    val attractions: Flow<List<Attraction>>
    suspend fun configCountry(countryEnum: CountryEnum)
    suspend fun getAttractions(): Either<DataException, Unit>
    suspend fun getAllAttractions(forceReload: Boolean = false): Response<List<Attraction>>

    suspend fun getAttractionById(attractionId: Int): Response<Attraction>
}