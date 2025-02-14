package com.nfq.nfqsummit.screens.attractions.attractionBlogs

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.model.Blog
import com.nfq.data.domain.repository.ExploreRepository
import com.nfq.data.domain.repository.NFQSummitRepository
import com.nfq.nfqsummit.analytics.helper.AnalyticsHelper
import com.nfq.nfqsummit.analytics.logSaveAttraction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttractionBlogsViewModel @Inject constructor(
    private val exploreRepository: ExploreRepository,
    private val analyticsHelper: AnalyticsHelper,
    nfqSummitRepository: NFQSummitRepository,
    saveStateHandle: SavedStateHandle
) : ViewModel() {
    private val attractionId = saveStateHandle.get<String>("attractionId").orEmpty()
    private val attendeeCode = nfqSummitRepository
        .user.map { it?.attendeeCode.orEmpty() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    val blogs = exploreRepository.let {
        if (attractionId != "-1") {
            it.getBlogsByAttractionId(attractionId = attractionId)
        } else {
            it.favouriteBlogs
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )

    fun markBlogAsFavorite(favorite: Boolean, blog: Blog) {
        viewModelScope.launch {
            if (favorite) {
                analyticsHelper.logSaveAttraction(
                    attendeeCode = attendeeCode.value,
                    attractionId = blog.id,
                    attractionTitle = blog.title
                )
            }
            exploreRepository.updateFavouriteBlog(blogId = blog.id, isFavorite = favorite)
        }
    }
}