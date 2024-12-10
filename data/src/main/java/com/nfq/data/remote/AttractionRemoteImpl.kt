package com.nfq.data.remote

import arrow.core.Either
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.nfq.data.network.exception.DataException
import com.nfq.data.remote.model.AttractionRemoteModel
import com.nfq.data.remote.model.response.AttractionResponse
import com.nfq.data.remote.model.response.AttractionBlogResponse
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AttractionRemoteImpl @Inject constructor(
    private val supabaseClient: SupabaseClient
) : AttractionRemote {
    val db = Firebase.firestore
    override suspend fun fetchAttractions(): Either<DataException, List<AttractionResponse>> {
        return try {
            val querySnapshot = db.collection("attractions")
                .get()
                .await()
            val attractions = querySnapshot
                .documents
                .mapNotNull { document ->
                    AttractionResponse(
                        id = document.id,
                        blogs = (document.get("blogs") as? List<*>)?.mapNotNull { blog ->
                            (blog as? Map<*, *>)?.let { blogMap ->
                                AttractionBlogResponse(
                                    id = blogMap["id"]?.toString().orEmpty(),
                                    attractionId = document.id,
                                    contentUrl = blogMap["content_url"]?.toString().orEmpty(),
                                    title = blogMap["title"]?.toString().orEmpty(),
                                    description = blogMap["description"]?.toString().orEmpty(),
                                    iconUrl = blogMap["icon_url"]?.toString().orEmpty()
                                )
                            }
                        }.orEmpty(),
                        country = document.getString("country").orEmpty(),
                        iconUrl = document.getString("icon_url").orEmpty(),
                        parentBlog = document.getString("parent_blog").orEmpty(),
                        title = document.getString("title").orEmpty()
                    )
                }

            Either.Right(attractions)
        } catch (e: Exception) {
            Either.Left(DataException.Api(e.message ?: "An error occurred"))
        }
    }

    override suspend fun getAttractionDetailById(attractionId: String): Either<DataException, AttractionResponse> {
        return try {
            val querySnapshot = db.collection("attractions")
                .document(attractionId)
                .get()
                .await()

            val attraction = querySnapshot
                .toObject<AttractionResponse>()
                ?.copy(id = querySnapshot.id)

            Either.Right(attraction ?: throw Exception("Attraction not found"))
        } catch (e: Exception) {
            Either.Left(DataException.Api(e.message ?: "An error occurred"))
        }
    }

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