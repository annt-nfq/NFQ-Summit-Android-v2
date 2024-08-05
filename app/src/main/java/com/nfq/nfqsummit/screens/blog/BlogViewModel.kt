package com.nfq.nfqsummit.screens.blog

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.model.Blog
import com.nfq.data.domain.model.Response
import com.nfq.data.domain.repository.BlogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlogViewModel @Inject constructor(
    private val blogRepository: BlogRepository
): ViewModel() {

    var blog by mutableStateOf<Blog?>(null)
        private set

    fun getBlogById(blogId: Int) {
        viewModelScope.launch {
            when(val response = blogRepository.getBlogById(blogId)) {
                is Response.Success -> {
                    blog = response.data
                }
                is Response.Loading -> {
                    // Handle loading
                }
                is Response.Failure -> {
                    // Handle failure
                }
            }
        }
    }
}