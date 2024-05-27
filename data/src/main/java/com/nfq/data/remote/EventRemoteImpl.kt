package com.nfq.data.remote

import com.nfq.data.remote.model.SummitEventRemoteModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import javax.inject.Inject

class EventRemoteImpl @Inject constructor(
    private val supabaseClient: SupabaseClient
) : EventRemote {
    override suspend fun getAllEvents(): List<SummitEventRemoteModel> {
        return supabaseClient.from("events")
            .select()
            .decodeList<SummitEventRemoteModel>()
    }

    override suspend fun getTechRocksEvents(): List<SummitEventRemoteModel> {
        return supabaseClient.from("events").select {
            filter {
                filter(column = "is_conference", operator = FilterOperator.EQ, value = true)
            }
        }.decodeList<SummitEventRemoteModel>()
    }

    override suspend fun getEventById(eventId: String): SummitEventRemoteModel {
        return supabaseClient.from("events").select {
            filter {
                filter(column = "id", operator = FilterOperator.EQ, value = eventId)
            }
        }.decodeSingle()
    }
}