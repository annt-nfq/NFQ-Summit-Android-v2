package com.nfq.data.database

import androidx.room.TypeConverter
import com.nfq.data.remote.model.response.CategoryResponse
import com.nfq.data.remote.model.response.EventDayResponse
import com.nfq.data.remote.model.response.EventLocationResponse
import com.nfq.data.remote.model.response.GenreResponse
import com.nfq.data.remote.model.response.LocationResponse
import com.nfq.data.remote.model.response.SpeakerResponse
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

    @TypeConverter
    fun fromCategoryResponse(value: CategoryResponse?): String? {
        return value?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toCategoryResponse(value: String?): CategoryResponse? {
        return value?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromGenreResponse(value: GenreResponse?): String? {
        return value?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toGenreResponse(value: String?): GenreResponse? {
        return value?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromSpeakerResponseList(value: List<SpeakerResponse>?): String? {
        return value?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toSpeakerResponseList(value: String?): List<SpeakerResponse>? {
        return value?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromLocationResponseList(value: List<LocationResponse>?): String? {
        return value?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toLocationResponseList(value: String?): List<LocationResponse>? {
        return value?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromEventLocationResponseList(value: List<EventLocationResponse>?): String? {
        return value?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toEventLocationResponseList(value: String?): List<EventLocationResponse>? {
        return value?.let { Json.decodeFromString(it) }
    }
}