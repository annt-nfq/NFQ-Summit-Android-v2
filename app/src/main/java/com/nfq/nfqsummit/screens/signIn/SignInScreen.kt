package com.nfq.nfqsummit.screens.signIn

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import com.nfq.nfqsummit.R
import com.nfq.nfqsummit.ui.theme.boxShadow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    continueAsGuest: () -> Unit,
    onSignInSuccess: () -> Unit,
    viewModel: SignInViewModel = hiltViewModel()
) {

    val window = (LocalView.current.context as Activity).window
    window.statusBarColor = Color.Transparent.toArgb()

    val focusManager = LocalFocusManager.current

    val interactionSource = remember { MutableInteractionSource() }
    var text by remember { mutableStateOf("") }

    val signInStatus = viewModel.signInStatus.collectAsState(initial = SignInStatus.initial).value
    if (signInStatus == SignInStatus.success) {
        onSignInSuccess()
    }
    if (signInStatus == SignInStatus.failed) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                Button(onClick = {
                    viewModel.resetState()
                }) {
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

    Scaffold { paddingValues ->
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = interactionSource, indication = null
                    ) {
                        focusManager.clearFocus()
                    }
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
                                    text = "Booking ID / Booking No.",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                            alpha = 0.7f
                                        ),
                                    ),
                                )
                            },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(modifier = Modifier
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
                                viewModel.signIn(text)
                            }
                            .background(MaterialTheme.colorScheme.primary)) {
                            Text(
                                text = "Sign In", style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.background,
                                    fontWeight = FontWeight.Bold,
                                ), modifier = Modifier.align(Alignment.Center)
                            )
                            Row(
                                modifier = Modifier.align(Alignment.CenterEnd)
                            ) {
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
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {

                            }) {
                            Text(
                                text = "Upload My QR Code",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                ),
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "Continue As\nUnregistered guest",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.clickable {
                                continueAsGuest()
                            })
                        Spacer(modifier = Modifier.height(56.dp))
                    }

                }

            }
        }
        if (signInStatus == SignInStatus.loading) Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                )
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
}