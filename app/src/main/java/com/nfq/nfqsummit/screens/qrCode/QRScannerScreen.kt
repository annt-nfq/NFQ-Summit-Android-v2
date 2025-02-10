package com.nfq.nfqsummit.screens.qrCode

import QRCodeScanner
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.nfq.nfqsummit.components.Loading
import com.nfq.nfqsummit.screens.signIn.SignInViewModel
import com.nfq.nfqsummit.utils.UserMessageManager

@OptIn(ExperimentalGetImage::class)
@Composable
fun QRScannerScreen(
    navigateUp: () -> Unit
) {
    val viewModel: SignInViewModel = hiltViewModel()
    val loading by viewModel.loading.collectAsState()
    if (loading) Loading()
    QRCodeScanner(
        onQrCodeScanned = { qrContent ->
            viewModel.signIn(qrContent)
        },
        onError = { exception ->
            UserMessageManager.showMessage(exception)
        },
        navigateUp = navigateUp
    )
}