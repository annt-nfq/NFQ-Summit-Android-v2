package com.nfq.nfqsummit.screens.payment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfq.data.domain.model.Blog
import com.nfq.data.domain.model.Response
import com.nfq.data.domain.repository.BlogRepository
import com.nfq.data.network.utils.networkConnectivity.ConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val blogRepository: BlogRepository,
    connectivityObserver: ConnectivityObserver,
): ViewModel() {

    var blog by mutableStateOf<Blog?>(null)
        private set
    val networkStatus  = connectivityObserver.observe().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ConnectivityObserver.Status.Unavailable
    )
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