package com.nfq.data.remote

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.nfq.data.remote.model.AudioRemoteModel
import com.nfq.data.remote.model.TranslationRemoteModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TranslationRemoteImpl @Inject constructor() : TranslationRemote {
    val db = Firebase.firestore
    override suspend fun getAllTranslations(): List<TranslationRemoteModel> {
        return try {
            val querySnapshot = db.collection("survivals")
                .get()
                .await()

            querySnapshot
                .documents
                .mapNotNull { document ->
                    TranslationRemoteModel(
                        id = document.id,
                        title = document.getString("title").orEmpty(),
                        audios = (document.get("audios") as? List<*>)?.mapIndexedNotNull { index, audio ->
                            (audio as? Map<*, *>)?.let { audioMap ->
                                AudioRemoteModel(
                                    id = index,
                                    title = audioMap["title"] as String,
                                    audioUrl = audioMap["audio_url"] as String,
                                    translationId = 0
                                )
                            }
                        }.orEmpty()
                    )
                }

        } catch (e: Exception) {
            emptyList()
        }
    }
}