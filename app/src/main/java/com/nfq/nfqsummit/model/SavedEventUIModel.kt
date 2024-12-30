package com.nfq.nfqsummit.model

import com.nfq.data.domain.model.CategoryType

data class SavedEventUIModel(
    val id: String,
    val name: String,
    val date: String,
    val imageUrl: String,
    val category: CategoryType = CategoryType.Summit()
)
