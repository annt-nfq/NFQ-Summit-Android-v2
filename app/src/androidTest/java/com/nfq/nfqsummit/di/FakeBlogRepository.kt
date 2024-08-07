package com.nfq.nfqsummit.di

import com.nfq.data.domain.model.Blog
import com.nfq.data.domain.model.Response
import com.nfq.data.domain.repository.BlogRepository
import com.nfq.nfqsummit.mocks.mockBlog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class FakeBlogRepository @Inject constructor(): BlogRepository {
    override suspend fun getAllBlogs(forceReload: Boolean): Response<List<Blog>> {
        return Response.Success(listOf())
    }

    override suspend fun getBlogById(blogId: Int): Response<Blog> {
        return Response.Success(mockBlog)
    }

    override suspend fun getPaymentBlog(): Response<Blog> {
        return Response.Success(mockBlog)
    }

    override suspend fun getTransportationBlogs(): Response<List<Blog>> {
        return Response.Success(listOf())
    }

    override suspend fun getBlogsByAttractionId(attractionId: Int): Flow<Response<List<Blog>>> {
        return MutableStateFlow(Response.Success(listOf()))
    }

    override fun getFavoriteBlogs(): Flow<Response<List<Blog>>> {
        return MutableStateFlow(Response.Success(listOf()))
    }

    override fun getRecommendedBlogs(): Flow<Response<List<Blog>>> {
       return MutableStateFlow(Response.Success(listOf()))
    }

    override suspend fun markBlogAsFavorite(blog: Blog, favorite: Boolean) {
    }
}