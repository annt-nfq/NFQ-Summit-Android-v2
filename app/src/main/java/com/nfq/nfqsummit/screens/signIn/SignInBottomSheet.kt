@file:OptIn(ExperimentalMaterial3Api::class)

package com.nfq.nfqsummit.screens.signIn

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nfq.nfqsummit.R
import com.nfq.nfqsummit.components.BasicModalBottomSheet
import com.nfq.nfqsummit.components.bounceClick
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview
import kotlinx.coroutines.launch

@Composable
fun SignInBottomSheet(
    onDismissRequest: () -> Unit,
    goToSignIn: () -> Unit
) {
    val skipPartiallyExpanded by remember { mutableStateOf(true) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded)
    val scope = rememberCoroutineScope()

    BasicModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
        content = {
            SignInContent(goToSignIn = goToSignIn, onSkip = {
                scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                    if (!bottomSheetState.isVisible) {
                        onDismissRequest()
                    }
                }
            })
        }
    )
}

@Composable
private fun SignInContent(
    modifier: Modifier = Modifier,
    goToSignIn: () -> Unit,
    onSkip: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary,
        modifier = modifier.fillMaxHeight(0.85f),
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            Column(
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .padding(bottom = 24.dp)
                    .navigationBarsPadding()
            ) {
                SignInButton(
                    focusManager = focusManager,
                    onClickAction = goToSignIn,
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    contentColor = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(4.dp))
                SkipButton(onSkip = onSkip)
            }
        }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(horizontal = 30.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(bottom = 44.dp)
                    .clip(RoundedCornerShape(5.0.dp))
                    .background(Color(0xFFFFFFFF))
                    .width(70.dp)
                    .height(5.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = "Please sign in to view your registered events",
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.width(333.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.ic_social),
                contentDescription = null
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Use your unique QR or attendee code from your Summit events booking to sign in. Without signing in, only Scaling Business Day events on Feb 28 will be visible.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SkipButton(
    onSkip: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick()
            .height(58.dp)
            .clip(shape)
            .clickable { onSkip() }
    ) {
        Text(
            text = "Skip",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            )
        )
    }
}

@Preview
@Composable
private fun QRCodeContentPreview() {
    NFQSnapshotTestThemeForPreview {
        SignInContent(
            modifier = Modifier.fillMaxSize(),
            goToSignIn = {}
        )
    }
}