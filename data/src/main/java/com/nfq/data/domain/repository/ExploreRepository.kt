package com.nfq.data.domain.repository

import arrow.core.Either
import com.nfq.data.domain.model.Attraction
import com.nfq.data.domain.model.Blog
import com.nfq.data.domain.model.CountryEnum
import com.nfq.data.domain.model.Response
import com.nfq.data.network.exception.DataException
import kotlinx.coroutines.flow.Flow

interface ExploreRepository {
    val attractions: Flow<List<Attraction>>
    val favouriteBlogs : Flow<List<Blog>>
    fun getBlogsByAttractionId(attractionId: String): Flow<List<Blog>>
    fun getBlogDetails(blogId: String): Flow<Blog>
    fun getTransportationBlogs(parentBlogId: String): Flow<List<Blog>>
    fun exploreBlogs(countryEnum: CountryEnum): Flow<List<Blog>>

    suspend fun updateFavouriteBlog(blogId: String, isFavorite: Boolean)
    suspend fun configCountry(countryEnum: CountryEnum)
    suspend fun fetchAttractions(): Either<DataException, Unit>
    suspend fun fetchBlogs(): Either<DataException, Unit>
    suspend fun getAllAttractions(forceReload: Boolean = false): Response<List<Attraction>>

    suspend fun getAttractionById(attractionId: Int): Response<Attraction>
}