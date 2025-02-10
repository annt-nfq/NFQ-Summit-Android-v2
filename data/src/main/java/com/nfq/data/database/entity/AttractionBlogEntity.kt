package com.nfq.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attraction_blog_entity")
data class AttractionBlogEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "attractionId") val attractionId: String,
    @ColumnInfo(name = "content_url") val contentUrl: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "icon_url") val iconUrl: String,
    @ColumnInfo(name = "is_favorite") val isFavorite: Boolean
)
