package com.nfq.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TranslationRemoteModel(
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    val audios: List<AudioRemoteModel>
)

@Serializable
data class AudioRemoteModel(
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("audio_url")
    val audioUrl: String,
    @SerialName("translation_id")
    val translationId: Int
)