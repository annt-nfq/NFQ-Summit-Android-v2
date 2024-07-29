package com.nfq.data.remote.repository

import com.nfq.data.domain.model.Blog
import com.nfq.data.domain.model.Response
import com.nfq.data.domain.model.toBlog
import com.nfq.data.domain.repository.BlogRepository
import com.nfq.data.remote.BlogRemote
import javax.inject.Inject

class BlogRepositoryImpl @Inject constructor(
    private val blogRemote: BlogRemote
) : BlogRepository {

    override suspend fun getAllBlogs(forceReload: Boolean): Response<List<Blog>> {
        return Response.Success(blogRemote.getAllBlogs().map { it.toBlog() })
    }

    override suspend fun getBlogById(blogId: Int): Response<Blog> {
        return Response.Success(blogRemote.getBlogById(blogId).toBlog())
    }

    override suspend fun getTransportationBlogs(): Response<List<Blog>> {
        return Response.Success(blogRemote.getTransportationBlogs().map { it.toBlog() })
    }

    override suspend fun getPaymentBlog(): Response<Blog> {
        return Response.Success(blogRemote.getPaymentBlog().toBlog())
    }

    override suspend fun getBlogsByAttractionId(attractionId: Int): Response<List<Blog>> {
        return Response.Success(blogRemote.getBlogsByAttractionId(attractionId).map { it.toBlog() })
    }
}