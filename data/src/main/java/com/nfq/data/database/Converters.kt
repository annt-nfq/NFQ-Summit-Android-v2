package com.nfq.data.database

import androidx.room.TypeConverter
import com.nfq.data.remote.model.response.EventDayResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class EventTypeConverters {

    @TypeConverter
    fun fromList(value: List<String>?): String? {
        return value?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toList(value: String?): List<String>? {
        return value?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromEventDayResponse(value: EventDayResponse?): String? {
        return value?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toEventDayResponse(value: String?): EventDayResponse? {
        return value?.let { Json.decodeFromString(it) }
    }
}