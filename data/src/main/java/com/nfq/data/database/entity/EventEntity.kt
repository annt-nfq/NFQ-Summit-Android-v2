package com.nfq.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nfq.data.remote.model.response.CategoryResponse
import com.nfq.data.remote.model.response.EventDayResponse
import com.nfq.data.remote.model.response.EventLocationResponse
import com.nfq.data.remote.model.response.GenreResponse
import com.nfq.data.remote.model.response.SpeakerResponse
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "event_entity")
data class EventEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo("code") val code: String,
    @ColumnInfo("title") val title: String,
    @ColumnInfo("quantity") val quantity: Int,
    @ColumnInfo("isMain") val isMain: Boolean,
    @ColumnInfo("timeStart") val timeStart: Long,
    @ColumnInfo("timeEnd") val timeEnd: Long,
    @ColumnInfo("isTimeLate") val isTimeLate: Boolean,
    @ColumnInfo("description") val description: String,
    @ColumnInfo("location") val location: String,
    @ColumnInfo("latitude") val latitude: Double,
    @ColumnInfo("longitude") val longitude: Double,
    @ColumnInfo("locations") val locations: List<EventLocationResponse>,
    @ColumnInfo("gatherTime") val gatherTime: String,
    @ColumnInfo("gatherLocation") val gatherLocation: String,
    @ColumnInfo("leavingTime") val leavingTime: String,
    @ColumnInfo("leavingLocation") val leavingLocation: String,
    @ColumnInfo("adminNotes") val adminNotes: String,
    @ColumnInfo("images") val images: List<String>,
    @ColumnInfo("order") val order: Int,
    @ColumnInfo("isTechRock") val isTechRock: Boolean,
    @ColumnInfo("category") val category: CategoryResponse?,
    @ColumnInfo("genre") val genre: GenreResponse?,
    @ColumnInfo("eventDay") val eventDay: EventDayResponse?,
    @ColumnInfo("qrCodeUrl") val qrCodeUrl: String,
    @ColumnInfo("isFavorite") val isFavorite: Boolean,
    @ColumnInfo("speaker") val speakers: List<SpeakerResponse>?,
    @ColumnInfo("updatedAt") val updatedAt: Long
)

