package com.nfq.nfqsummit.screens.attractions.attractionBlogs

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.model.Blog
import com.nfq.data.domain.repository.ExploreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttractionBlogsViewModel @Inject constructor(
    private val exploreRepository: ExploreRepository,
    saveStateHandle: SavedStateHandle
) : ViewModel() {
    private val attractionId = saveStateHandle.get<String>("attractionId").orEmpty()

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
            exploreRepository.updateFavouriteBlog(blogId = blog.id, isFavorite = favorite)
        }
    }
}