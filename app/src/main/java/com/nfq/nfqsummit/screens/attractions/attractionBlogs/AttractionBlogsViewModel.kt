@file:OptIn(ExperimentalCoroutinesApi::class)

package com.nfq.nfqsummit.screens.attractions.attractionBlogs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.model.Attraction
import com.nfq.data.domain.model.Blog
import com.nfq.data.domain.model.Response
import com.nfq.data.domain.repository.AttractionRepository
import com.nfq.data.domain.repository.BlogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttractionBlogsViewModel @Inject constructor(
    private val attractRepository: AttractionRepository,
    private val blogRepository: BlogRepository
) : ViewModel() {

    var attraction = MutableStateFlow<Attraction?>(null)

    fun getAttraction(attractionId: Int) {
        viewModelScope.launch {
            when (val response = attractRepository.getAttractionById(attractionId)) {
                is Response.Success -> {
                    attraction.value = response.data
                }
                is Response.Loading -> {
                }
                is Response.Failure -> {
                }
            }
        }
    }

    val blogs = attraction
        .filterNotNull()
        .flatMapLatest { attraction ->
            blogRepository.getBlogsByAttractionId(attraction.id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = Response.Loading
        )

    fun markBlogAsFavorite(favorite: Boolean, blog: Blog) {
        viewModelScope.launch {
            blogRepository.markBlogAsFavorite(blog, favorite)
        }
    }
}