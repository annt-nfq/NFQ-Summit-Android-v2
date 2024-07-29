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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttractionBlogsViewModel @Inject constructor(
    private val attractRepository: AttractionRepository,
    private val blogRepository: BlogRepository
) : ViewModel() {

    var blogs by mutableStateOf<List<Blog>?>(emptyList())

    var attraction by mutableStateOf<Attraction?>(null)
        private set

    fun getAttraction(attractionId: Int) {
        viewModelScope.launch {
            when (val response = attractRepository.getAttractionById(attractionId)) {
                is Response.Success -> {
                    attraction = response.data
                    getBlogs(attractionId)
                }
                is Response.Loading -> {
                }
                is Response.Failure -> {
                }
            }
        }
    }

    fun getBlogs(attractionId: Int) {
        viewModelScope.launch {
            val response = blogRepository.getBlogsByAttractionId(attractionId)
            when (response) {
                is Response.Success<List<Blog>> -> {
                    blogs = response.data
                }

                is Response.Loading -> {
                }

                is Response.Failure -> {
                }
            }
        }
    }

    fun markBlogAsFavorite(favorite: Boolean, blog: Blog) {
        viewModelScope.launch {
            blogRepository.markBlogAsFavorite(blog, favorite)
        }
    }
}