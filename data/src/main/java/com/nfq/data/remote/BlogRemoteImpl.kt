package com.nfq.data.remote

import com.nfq.data.remote.model.BlogRemoteModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import javax.inject.Inject

class BlogRemoteImpl @Inject constructor(
    private val supabaseClient: SupabaseClient
) : BlogRemote {

    override suspend fun getAllBlogs(): List<BlogRemoteModel> {
        return supabaseClient.from("blogs")
            .select()
            .decodeList<BlogRemoteModel>()
    }

    override suspend fun getBlogsByAttractionId(attractionId: Int): List<BlogRemoteModel> {
        return supabaseClient.from("blogs").select {
            filter {
                filter(column = "attraction", operator = FilterOperator.EQ, value = attractionId)
            }
        }.decodeList()
    }

    override suspend fun getTransportationBlogs(): List<BlogRemoteModel> {
        return supabaseClient.from("blogs").select {
            filter {
                filter(column = "category", operator = FilterOperator.EQ, value = "transportation")
            }
        }.decodeList()
    }

    override suspend fun getPaymentBlog(): BlogRemoteModel {
        return supabaseClient.from("blogs").select {
            filter {
                filter(column = "category", operator = FilterOperator.EQ, value = "payment")
            }
        }.decodeSingle()
    }

    override suspend fun getRecommendedBlogs(): List<BlogRemoteModel> {
        return supabaseClient.from("blogs").select {
            filter {
                filter(column = "is_recommended", operator = FilterOperator.EQ, value = true)
            }
        }.decodeList()
    }

    override suspend fun getBlogById(blogId: Int): BlogRemoteModel {
        return supabaseClient.from("blogs").select {
            filter {
                filter(column = "id", operator = FilterOperator.EQ, value = blogId)
            }
        }.decodeSingle()
    }
}