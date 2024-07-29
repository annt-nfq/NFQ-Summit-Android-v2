package com.nfq.nfqsummit.screens.transportation

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
class TransportationViewModel @Inject constructor(
    private val blogRepository: BlogRepository
): ViewModel() {

    var blogs by mutableStateOf<List<Blog>?>(emptyList())
        private set

    fun getTransportationBlogs() {
        viewModelScope.launch {
            val response = blogRepository.getTransportationBlogs()
            Log.e("TransportationViewModel", response.toString())

            when(response) {
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
}