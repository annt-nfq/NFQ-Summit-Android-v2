package com.nfq.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "attraction_entity")
data class AttractionEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "country") val country: String,
    @ColumnInfo(name = "icon_url") val iconUrl: String,
    @ColumnInfo(name = "parent_blog") val parentBlog: String,
    @ColumnInfo(name = "title") val title: String,
)
