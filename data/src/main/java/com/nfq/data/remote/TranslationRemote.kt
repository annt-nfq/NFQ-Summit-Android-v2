package com.nfq.data.remote

import com.nfq.data.remote.model.TranslationRemoteModel

interface TranslationRemote {
    suspend fun getAllTranslations(): List<TranslationRemoteModel>
}