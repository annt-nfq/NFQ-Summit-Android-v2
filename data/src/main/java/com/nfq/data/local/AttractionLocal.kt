package com.nfq.data.local

import com.nfq.data.remote.model.AttractionRemoteModel

interface AttractionLocal {
    suspend fun getAllAttractions(): List<AttractionRemoteModel>

    suspend fun getAttractionById(id: Int): AttractionRemoteModel?

    suspend fun insertAttractions(attractions: List<AttractionRemoteModel>)

    suspend fun clearAllAttractions()
}