package com.nfq.data.domain.repository

import com.nfq.data.domain.model.Response
import com.nfq.data.domain.model.Translation

interface TranslationRepository {
    suspend fun getAllTranslations(forceReload: Boolean = false): Response<List<Translation>>
}