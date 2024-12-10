package com.nfq.data.remote.repository

import arrow.core.Either
import com.nfq.data.database.dao.AttractionDao
import com.nfq.data.database.dao.AttractionBlogDao
import com.nfq.data.database.entity.AttractionBlogEntity
import com.nfq.data.domain.model.Attraction
import com.nfq.data.domain.model.Blog
import com.nfq.data.domain.model.CountryEnum
import com.nfq.data.domain.model.Response
import com.nfq.data.domain.model.toAttraction
import com.nfq.data.domain.repository.ExploreRepository
import com.nfq.data.local.AttractionLocal
import com.nfq.data.mapper.toAttractionEntity
import com.nfq.data.mapper.toAttractions
import com.nfq.data.mapper.toBlog
import com.nfq.data.mapper.toAttractionBlogEntity
import com.nfq.data.mapper.toBlogs
import com.nfq.data.network.exception.DataException
import com.nfq.data.remote.AttractionRemote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExploreRepositoryImpl @Inject constructor(
    private val attractionRemote: AttractionRemote,
    private val attractionLocal: AttractionLocal,
    private val attractionBlogDao: AttractionBlogDao,
    private val attractionDao: AttractionDao
) : ExploreRepository {
    private val _countryEnumFlow = MutableStateFlow(CountryEnum.THAILAND)
    override val attractions: Flow<List<Attraction>>
        get() = combine(
            _countryEnumFlow.map { it.name.lowercase() },
            attractionDao.getAttractions()
        ) { country, attractions ->
            attractions.filter { it.country == country }.toAttractions()
        }

    override fun getBlogsByAttractionId(attractionId: String): Flow<List<Blog>> {
        return attractionBlogDao
            .getBlogsByAttractionId(attractionId)
            .map { it.toBlogs() }
    }

    override fun getBlogDeatils(blogId: String): Flow<Blog> {
        return attractionBlogDao
            .getBlogDeatils(blogId)
            .map { it.toBlog() }
    }

    override suspend fun updateFavouriteBlog(blogId: String, isFavorite: Boolean) {
        attractionBlogDao.updateFavouriteBlog(blogId = blogId, isFavorite = isFavorite)
    }


    override suspend fun configCountry(countryEnum: CountryEnum) {
        _countryEnumFlow.value = countryEnum
    }

    override suspend fun getAttractions(): Either<DataException, Unit> {
        return try {
            attractionRemote.fetchAttractions().map { attractionResponses ->
                val blogs = mutableListOf<AttractionBlogEntity>()
                val attractions = attractionResponses.map { attractionResponse ->
                    val cachedBlogs =
                        attractionBlogDao.getBlogsByAttractionId(attractionResponse.id).firstOrNull()
                    val remoteBlogs = attractionResponse.blogs.map { blogResponse ->
                        blogResponse.toAttractionBlogEntity(
                            attractionId = attractionResponse.id,
                            isFavorite = cachedBlogs?.any { it.id == blogResponse.id && it.isFavorite }
                                ?: false
                        )
                    }
                    blogs.addAll(remoteBlogs)
                    attractionResponse.toAttractionEntity()
                }
                attractionDao.insertAttractions(attractions)
                attractionBlogDao.insertBlogs(blogs)
            }
            Either.Right(Unit)
        } catch (e: Exception) {
            Either.Left(DataException.Api(e.message ?: e.localizedMessage ?: "Unknown error"))
        }
    }


    override suspend fun getAllAttractions(forceReload: Boolean): Response<List<Attraction>> {
        val cachedAttractions = attractionLocal.getAllAttractions()
        return if (cachedAttractions.isNotEmpty() && !forceReload)
            Response.Success(cachedAttractions.map { it.toAttraction() })
        else {
            Response.Success(
                attractionRemote.getAllAttractions().also {
                    attractionLocal.clearAllAttractions()
                    attractionLocal.insertAttractions(it)
                }.map { it.toAttraction() }
            )
        }
    }

    override suspend fun getAttractionById(attractionId: Int): Response<Attraction> {
        return attractionLocal.getAttractionById(attractionId)?.let {
            return Response.Success(it.toAttraction())
        } ?: Response.Success(attractionRemote.getAttractionById(attractionId).toAttraction())
    }
}