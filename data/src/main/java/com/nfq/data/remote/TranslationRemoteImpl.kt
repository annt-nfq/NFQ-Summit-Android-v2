package com.nfq.data.remote

import com.nfq.data.remote.model.TranslationRemoteModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class TranslationRemoteImpl @Inject constructor(
    private val supabaseClient: SupabaseClient
) : TranslationRemote {
    override suspend fun getAllTranslations(): List<TranslationRemoteModel> {
        val translationColumns =
            Columns.raw("""id,title,audios(id,title,translation_id,audio_url)""".trimIndent())
        return supabaseClient.from("translations").select(columns = translationColumns)
            .decodeList<TranslationRemoteModel>()
    }
}