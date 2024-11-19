package com.nfq.nfqsummit.screens.splash

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.nfq.nfqsummit.R
import com.nfq.nfqsummit.ui.theme.NFQOrange
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(
    navigateToHome: () -> Unit,
    navigateToOnboarding: () -> Unit,
) {
    val window = (LocalView.current.context as Activity).window
    WindowCompat.setDecorFitsSystemWindows(window, false)
    LaunchedEffect(Unit) {
        delay(1000)
        navigateToHome()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = NFQOrange),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_nfq_text_white),
            contentDescription = null,
            modifier = Modifier
                .width(300.dp)
                .wrapContentHeight(),
            alignment = Alignment.Center
        )
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    SplashScreen(
        navigateToHome = {},
        navigateToOnboarding = {}
    )
}