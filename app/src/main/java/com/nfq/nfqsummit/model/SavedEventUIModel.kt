package com.nfq.nfqsummit.model

import com.nfq.data.domain.model.CategoryEnum

data class SavedEventUIModel(
    val id: String,
    val name: String,
    val date: String,
    val imageUrl: String,
    val tag: String,
    val category: CategoryEnum = CategoryEnum.SUMMIT
)
