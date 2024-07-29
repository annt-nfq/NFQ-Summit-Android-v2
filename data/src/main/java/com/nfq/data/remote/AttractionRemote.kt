package com.nfq.data.remote

import com.nfq.data.remote.model.AttractionRemoteModel

interface AttractionRemote {
    suspend fun getAllAttractions(): List<AttractionRemoteModel>

    suspend fun getAttractionById(attractionId: Int): AttractionRemoteModel
}