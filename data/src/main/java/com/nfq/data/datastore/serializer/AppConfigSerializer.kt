package com.nfq.data.datastore.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.nfq.data.datastore.model.AppConfigResponse
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json


class AppConfigSerializer @Inject constructor() : Serializer<AppConfigResponse> {

    override val defaultValue: AppConfigResponse
        get() = AppConfigResponse(
            isCompletedOnboarding = false,
            isShownNotificationPermissionDialog = false,
            isEnabledNotification = false
        )

    override suspend fun readFrom(input: InputStream): AppConfigResponse {
        return try {
            Json.decodeFromString(
                AppConfigResponse.serializer(), input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read App config", serialization)
        }
    }

    override suspend fun writeTo(t: AppConfigResponse, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                Json.encodeToString(AppConfigResponse.serializer(), t).encodeToByteArray()
            )
        }
    }
}
