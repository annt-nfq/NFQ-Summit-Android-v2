package com.nfq.nfqsummit.di

import com.nfq.data.domain.model.Response
import com.nfq.data.domain.model.Translation
import com.nfq.data.domain.repository.TranslationRepository
import javax.inject.Inject

class FakeTranslationRepository @Inject constructor(): TranslationRepository {
    override suspend fun getAllTranslations(forceReload: Boolean): Response<List<Translation>> {
        return Response.Success(listOf())
    }
}