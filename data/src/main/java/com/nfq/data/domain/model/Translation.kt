package com.nfq.data.domain.model

import com.nfq.data.remote.model.AudioRemoteModel
import com.nfq.data.remote.model.TranslationRemoteModel

data class Translation(
    val id: Int,
    val title: String,
    val audios: List<TranslationAudio>
)

data class TranslationAudio(
    val id: Int,
    val title: String,
    val audioUrl: String
)


fun TranslationRemoteModel.toDomain(): Translation {
    return Translation(
        id = id,
        title = title,
        audios = audios.map { it.toDomain() }
    )
}

fun AudioRemoteModel.toDomain(): TranslationAudio {
    return TranslationAudio(
        id = id,
        title = title,
        audioUrl = audioUrl
    )
}
