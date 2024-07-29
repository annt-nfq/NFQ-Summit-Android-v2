package com.nfq.data.remote.repository

import com.nfq.data.domain.model.Response
import com.nfq.data.domain.model.Translation
import com.nfq.data.domain.model.toDomain
import com.nfq.data.domain.repository.TranslationRepository
import com.nfq.data.remote.TranslationRemote
import javax.inject.Inject

class TranslationRepositoryImpl @Inject constructor(
    private val translationRemote: TranslationRemote
) : TranslationRepository {
    override suspend fun getAllTranslations(forceReload: Boolean): Response<List<Translation>> {
        return Response.Success(translationRemote.getAllTranslations().map { it.toDomain() })
    }
}