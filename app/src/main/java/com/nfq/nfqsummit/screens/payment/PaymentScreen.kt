package com.nfq.nfqsummit.screens.payment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.nfq.nfqsummit.screens.blog.BlogUI

@Composable
fun PaymentScreen(
    goBack: () -> Unit,
    viewModel: PaymentViewModel = hiltViewModel()
) {

    val networkStatus by viewModel.networkStatus.collectAsState()
    LaunchedEffect(viewModel) {
        viewModel.getPaymentBlog()
    }

    BlogUI(
        blog = viewModel.blog,
        status = networkStatus,
        goBack = goBack
    )
}