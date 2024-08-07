package com.nfq.nfqsummit.screens.payment

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.hilt.navigation.compose.hiltViewModel
import com.nfq.nfqsummit.screens.blog.BlogUI

@Composable
fun PaymentScreen(
    goBack: () -> Unit,
    viewModel: PaymentViewModel = hiltViewModel()
) {

    val window = (LocalView.current.context as Activity).window
    window.statusBarColor = Color.Transparent.toArgb()

    LaunchedEffect(viewModel) {
        viewModel.getPaymentBlog()
    }

    BlogUI(
        blog = viewModel.blog,
        goBack = goBack
    )
}