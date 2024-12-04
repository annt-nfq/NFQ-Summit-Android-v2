package com.nfq.nfqsummit.screens.signIn

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nfq.nfqsummit.R
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview
import com.nfq.nfqsummit.ui.theme.boxShadow

@Composable
fun SignInScreen(
    continueAsGuest: () -> Unit,
    onSignInSuccess: () -> Unit,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val signInStatus by viewModel.signInStatus.collectAsState(SignInStatus.initial)

    SignInUI(
        signInStatus = signInStatus,
        onEvent = { event ->
            when (event) {
                is SignInEvent.SignIn -> viewModel.signIn(event.attendeeCode)
                SignInEvent.SignInSuccess -> onSignInSuccess()
                SignInEvent.ContinueAsGuest -> continueAsGuest()
                SignInEvent.ResetState -> viewModel.resetState()
            }
        }
    )
}

@Composable
private fun SignInUI(
    signInStatus: SignInStatus,
    onEvent: (SignInEvent) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var text by remember { mutableStateOf("") }

    if (signInStatus == SignInStatus.success) {
        onEvent(SignInEvent.SignInSuccess)
    }

    if (signInStatus == SignInStatus.failed) {
        SignInErrorDialog(onEvent)
    }

    Scaffold { paddingValues ->
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(75.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_nfq_text_white),
                    contentDescription = null,
                    modifier = Modifier.width(162.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.height(100.dp))
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 40.dp),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            leadingIcon = {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_mail),
                                    contentDescription = null,
                                    modifier = Modifier.width(22.dp),
                                )
                            },
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            placeholder = {
                                Text(
                                    text = "Attendee Code",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                            alpha = 0.7f
                                        ),
                                    ),
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        SignInButton(text, focusManager, onEvent)
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Please check your email for your booking number or upload your booking QR code here to sign in.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                                fontWeight = FontWeight.W400,
                                textAlign = TextAlign.Center
                            ),
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        UploadQRCodeButton()
                        Spacer(modifier = Modifier.weight(1f))
                        ContinueAsGuestButton(onEvent)
                        Spacer(modifier = Modifier.height(56.dp))
                    }
                }
            }
        }
        if (signInStatus == SignInStatus.loading) {
            LoadingIndicator()
        }
    }
}

@Composable
private fun SignInErrorDialog(onEvent: (SignInEvent) -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            Button(onClick = { onEvent(SignInEvent.ResetState) }) {
                Text(text = "OK", style = MaterialTheme.typography.bodyMedium)
            }
        },
        title = {
            Text(text = "Error", style = MaterialTheme.typography.bodyLarge)
        },
        text = {
            Text(text = "Booking ID invalid!", style = MaterialTheme.typography.bodyMedium)
        },
    )
}

@Composable
private fun SignInButton(
    text: String,
    focusManager: FocusManager,
    onEvent: (SignInEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .boxShadow(
                color = Color(0xFF1E1C2E).copy(alpha = 0.08f),
                blurRadius = 48.dp,
                spreadRadius = 0.dp,
                offset = DpOffset(0.dp, 24.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                focusManager.clearFocus()
                onEvent(SignInEvent.SignIn(text))
            }
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text = "Sign In",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.background,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier.align(Alignment.Center)
        )
        Row(modifier = Modifier.align(Alignment.CenterEnd)) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.3f))
                    .padding(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_big_arrow_right),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.background),
                    modifier = Modifier.size(13.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}

@Composable
private fun UploadQRCodeButton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { }
    ) {
        Text(
            text = "Upload My QR Code",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun ContinueAsGuestButton(onEvent: (SignInEvent) -> Unit) {
    TextButton(
        onClick = { onEvent(SignInEvent.ContinueAsGuest) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = "Continue As\nUnregistered guest",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .width(64.dp)
                .align(Alignment.Center),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}


@Preview
@Composable
private fun SignInUIPreview() {
    NFQSnapshotTestThemeForPreview {
        SignInUI(signInStatus = SignInStatus.initial, onEvent = {})
    }
}