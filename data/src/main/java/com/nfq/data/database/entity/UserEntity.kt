package com.nfq.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "user_entity")
data class UserEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "firstName") val firstName: String,
    @ColumnInfo("lastName") val lastName: String,
    @ColumnInfo("email") val email: String,
    @ColumnInfo("qrCodeUrl") val qrCodeUrl: String,
    @ColumnInfo("attendeeCode") val attendeeCode: String,
    @ColumnInfo("tk") val tk: String
)
