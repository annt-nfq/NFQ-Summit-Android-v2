package com.nfq.data.remote

import com.nfq.data.remote.model.AttractionRemoteModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import javax.inject.Inject

class AttractionRemoteImpl @Inject constructor(
    private val supabaseClient: SupabaseClient
) : AttractionRemote {

    override suspend fun getAllAttractions(): List<AttractionRemoteModel> {
        return supabaseClient.from("attractions")
            .select()
            .decodeList<AttractionRemoteModel>()
    }

    override suspend fun getAttractionById(attractionId: Int): AttractionRemoteModel {
        return supabaseClient.from("attractions").select {
            filter {
                filter(column = "id", operator = FilterOperator.EQ, value = attractionId)
            }
        }.decodeSingle()
    }
}