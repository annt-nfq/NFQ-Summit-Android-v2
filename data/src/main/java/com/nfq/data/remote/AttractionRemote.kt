package com.nfq.data.remote

import arrow.core.Either
import com.nfq.data.network.exception.DataException
import com.nfq.data.remote.model.AttractionRemoteModel
import com.nfq.data.remote.model.response.AttractionResponse
import com.nfq.data.remote.model.response.BlogResponse

interface AttractionRemote {
    suspend fun fetchAttractions(): Either<DataException, List<AttractionResponse>>

    suspend fun fetchBlogs(): Either<DataException, List<BlogResponse>>

    suspend fun getAttractionDetailById(attractionId: String): Either<DataException, AttractionResponse>

    suspend fun getAllAttractions(): List<AttractionRemoteModel>

    suspend fun getAttractionById(attractionId: Int): AttractionRemoteModel
}