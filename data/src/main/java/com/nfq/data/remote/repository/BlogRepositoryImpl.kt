package com.nfq.data.remote.repository

import com.nfq.data.domain.model.Blog
import com.nfq.data.domain.model.Response
import com.nfq.data.domain.model.toBlog
import com.nfq.data.domain.repository.BlogRepository
import com.nfq.data.local.BlogLocal
import com.nfq.data.remote.BlogRemote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class BlogRepositoryImpl @Inject constructor(
    private val blogRemote: BlogRemote,
    private val blogLocal: BlogLocal
) : BlogRepository {

    override suspend fun getAllBlogs(forceReload: Boolean): Response<List<Blog>> {
        val cachedBlogs = blogLocal.getAllBlogs()
        return if (cachedBlogs.isNotEmpty() && !forceReload)
            Response.Success(cachedBlogs.map { it.toBlog() })
        else {
            Response.Success(blogRemote.getAllBlogs().also {
                blogLocal.insertBlogs(it)
            }.map { it.toBlog() })
        }
    }

    override suspend fun getBlogById(blogId: Int): Response<Blog> {
        return blogLocal.getBlogById(blogId)?.let {
            return Response.Success(it.toBlog())
        } ?: Response.Success(blogRemote.getBlogById(blogId).toBlog())
    }

    override suspend fun getTransportationBlogs(): Response<List<Blog>> {
        val cachedBlogs = blogLocal.getBlogsByCategory("transportation")
        return if (cachedBlogs.isNotEmpty())
            Response.Success(cachedBlogs.map { it.toBlog() })
        else {
            Response.Success(blogRemote.getTransportationBlogs()
                .also {
                    blogLocal.insertBlogs(it)
                }
                .map { it.toBlog() })
        }
    }

    override suspend fun getPaymentBlog(): Response<Blog> {
        val cachedBlog = blogLocal.getBlogsByCategory("payment").firstOrNull()
        return if (cachedBlog != null)
            Response.Success(cachedBlog.toBlog())
        else {
            Response.Success(
                blogRemote.getPaymentBlog()
                    .also {
                        blogLocal.insertBlog(it)
                    }.toBlog()
            )
        }
    }

    override suspend fun getBlogsByAttractionId(attractionId: Int): Flow<Response<List<Blog>>> =
        flow {
            emit(Response.Loading)
            blogLocal.getBlogsByAttraction(attractionId)
                .catch { emit(Response.Failure(it)) }
                .collect { localBlogs ->
                    if (localBlogs.isNotEmpty()) {
                        emit(Response.Success(localBlogs.map { it.toBlog() }))
                    } else {
                        try {
                            val remoteBlogs = blogRemote.getAllBlogs()
                            blogLocal.insertBlogs(remoteBlogs)
                            emit(Response.Success(remoteBlogs
                                .filter { it.attractionId == attractionId }
                                .map { it.toBlog() })
                            )
                        } catch (e: Exception) {
                            emit(Response.Failure(e))
                        }
                    }
                }
        }.flowOn(Dispatchers.IO)

    override fun getFavoriteBlogs(): Flow<Response<List<Blog>>> =
        flow {
            emit(Response.Loading)
            blogLocal.getFavoriteBlogs()
                .catch { emit(Response.Failure(it)) }
                .collect { localBlogs ->
                    emit(Response.Success(localBlogs.map { it.toBlog() }))
                }
        }.flowOn(Dispatchers.IO)

    override fun getRecommendedBlogs(): Flow<Response<List<Blog>>> {
        return flow {
            emit(Response.Loading)
            blogLocal.getRecommendedBlogs()
                .catch { emit(Response.Failure(it)) }
                .collect { localBlogs ->
                    if (localBlogs.isNotEmpty()) {
                        emit(Response.Success(localBlogs.map { it.toBlog() }))
                    } else {
                        try {
                            val remoteBlogs = blogRemote.getAllBlogs()
                            blogLocal.insertBlogs(remoteBlogs)
                            emit(Response.Success(remoteBlogs.filter { it.isRecommended }
                                .map { it.toBlog() }))
                        } catch (e: Exception) {
                            emit(Response.Failure(e))
                        }
                    }
                }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun markBlogAsFavorite(blog: Blog, favorite: Boolean) {
        blogLocal.markBlogAsFavorite(blog.id.toInt(), favorite)
    }
}