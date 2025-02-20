package com.nfq.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nfq.data.remote.model.response.LocationResponse
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "voucher_entity")
data class VoucherEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo("date") val date: String,
    @ColumnInfo("locations") val locations: List<LocationResponse>,
    @ColumnInfo("price") val price: String,
    @ColumnInfo("imageUrl") val imageUrl: String,
    @ColumnInfo("sponsorLogoUrls") val sponsorLogoUrls: List<String>
)

