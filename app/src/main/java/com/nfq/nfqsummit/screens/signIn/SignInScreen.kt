package com.nfq.nfqsummit.screens.signIn

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.nfq.nfqsummit.R
import com.nfq.nfqsummit.components.Loading
import com.nfq.nfqsummit.components.bounceClick
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview
import com.nfq.nfqsummit.ui.theme.boxShadow
import com.nfq.nfqsummit.utils.handleQRCodeUri

@Composable
fun SignInScreen(
    continueAsGuest: () -> Unit,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val loading by viewModel.loading.collectAsState()
    if (loading) Loading()

    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            handleQRCodeUri(
                context = context,
                uri = it,
                onSuccess = viewModel::signIn
            )
        }
    }
    val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .enableAutoZoom()
        .build()
    val scanner = GmsBarcodeScanning.getClient(LocalContext.current, options)


    SignInUI(
        onEvent = { event ->
            when (event) {
                is SignInEvent.SignIn -> {
                    viewModel.signIn(event.attendeeCode)
                }

                is SignInEvent.UploadMyQRCode -> {
                    imagePickerLauncher.launch("image/*")
                }

                is SignInEvent.ScanQRCode -> {
                    scanner.startScan()
                        .addOnSuccessListener {
                            viewModel.signIn(it.rawValue ?: "")
                        }
                }

                SignInEvent.ContinueAsGuest -> {
                    continueAsGuest()
                }
            }
        }
    )
}

@Composable
private fun SignInUI(
    onEvent: (SignInEvent) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var text by remember { mutableStateOf("") }


    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.statusBarsPadding()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_nfq_text_white),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .padding(top = 73.dp)
                    .width(162.dp)
                    .height(48.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp)
                    .padding(top = 112.dp),
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
                        textStyle = MaterialTheme.typography.bodyMedium,
                        shape = RoundedCornerShape(16.dp),
                        placeholder = {
                            Text(
                                text = "Attendee Code",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF747688)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color(0xFFE4DFDF)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    SignInButton(text, focusManager, onEvent)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Please check your email for your booking number or upload your booking QR code here to sign in.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    ScanQRButton(onEvent = onEvent)
                    Text(
                        text = "Or",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    UploadQRCodeButton(onEvent = onEvent)
                    Spacer(modifier = Modifier.weight(1f))
                    ContinueAsGuestButton(onEvent)
                    Spacer(modifier = Modifier.height(56.dp))
                }
            }
        }
    }
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
            .bounceClick()
            .height(58.dp)
            .boxShadow(
                color = Color(0xFF1E1C2E).copy(alpha = 0.08f),
                blurRadius = 48.dp,
                spreadRadius = 0.dp,
                offset = DpOffset(0.dp, 24.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                if (text.isBlank()) return@clickable
                focusManager.clearFocus()
                onEvent(SignInEvent.SignIn(text))
            }
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text = "Sign In",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
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
private fun UploadQRCodeButton(
    onEvent: (SignInEvent) -> Unit = {}
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
            .clickable { onEvent(SignInEvent.UploadMyQRCode("")) }
    ) {
        Text(
            text = "Upload My QR Code",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            )
        )

        Image(
            painter = painterResource(id = R.drawable.ic_upload),
            contentDescription = "ic_upload",
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun ScanQRButton(
    onEvent: (SignInEvent) -> Unit = {}
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick()
            .height(58.dp)
            .clip(shape)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = shape
            )
            .clickable { onEvent(SignInEvent.ScanQRCode) }
    ) {
        Text(
            text = "Scan to register",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun ContinueAsGuestButton(onEvent: (SignInEvent) -> Unit) {
    TextButton(
        onClick = { onEvent(SignInEvent.ContinueAsGuest) },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .bounceClick()
            .fillMaxWidth()
    ) {
        Text(
            text = "Continue As\nUnregistered guest",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            letterSpacing = 1.sp,
        )
    }
}


@Preview
@Composable
private fun SignInUIPreview() {
    NFQSnapshotTestThemeForPreview {
        SignInUI(onEvent = {})
    }
}