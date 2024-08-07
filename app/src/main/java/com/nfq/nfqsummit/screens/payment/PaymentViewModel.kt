package com.nfq.nfqsummit.screens.payment

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
class PaymentViewModel @Inject constructor(
    private val blogRepository: BlogRepository
): ViewModel() {

    var blog by mutableStateOf<Blog?>(null)
        private set

    fun getPaymentBlog() {
        viewModelScope.launch {
            when(val response = blogRepository.getPaymentBlog()) {
                is Response.Success<Blog> -> {
                    blog = response.data
                }
                is Response.Loading -> {
                }
                is Response.Failure -> {
                }
            }
        }
    }
}