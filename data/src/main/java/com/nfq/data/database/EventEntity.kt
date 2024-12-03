package com.nfq.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nfq.data.remote.model.response.EventDayResponse
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "event_entity")
data class EventEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo("code") val code: String,
    @ColumnInfo("title") val title: String,
    @ColumnInfo("quantity") val quantity: Int,
    @ColumnInfo("isMain") val isMain: Boolean,
    @ColumnInfo("timeStart") val timeStart: String,
    @ColumnInfo("timeEnd") val timeEnd: String,
    @ColumnInfo("isTimeLate") val isTimeLate: Boolean,
    @ColumnInfo("description") val description: String,
    @ColumnInfo("location") val location: String,
    @ColumnInfo("latitude") val latitude: String,
    @ColumnInfo("longitude") val longitude: String,
    @ColumnInfo("gatherTime") val gatherTime: String,
    @ColumnInfo("gatherLocation") val gatherLocation: String,
    @ColumnInfo("leavingTime") val leavingTime: String,
    @ColumnInfo("leavingLocation") val leavingLocation: String,
    @ColumnInfo("adminNotes") val adminNotes: String,
    @ColumnInfo("images") val images: List<String>,
    @ColumnInfo("order") val order: String,
    @ColumnInfo("category") val category: String,
    @ColumnInfo("eventDay") val eventDay: EventDayResponse?,
    @ColumnInfo("qrCodeUrl") val qrCodeUrl: String,
    @ColumnInfo("isFavorite") val isFavorite: Boolean
)
