package com.nfq.nfqsummit.screens.bookingNumber

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.nfq.data.domain.model.Response
import com.nfq.nfqsummit.R
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview

@Composable
fun BookingNumberScreen(
    navigateToHome: () -> Unit
) {
    var bookingNumber by remember { mutableStateOf("") }
    val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .enableAutoZoom()
        .build()
    val scanner = GmsBarcodeScanning.getClient(LocalContext.current, options)

    AddBookingNumberUI(
        navigateToHome = { navigateToHome() },
        saveBookingNumber = {
//            viewModel.saveBookingNumber(it)
        },
        bookingState = null,
        bookingNumber = bookingNumber,
        onBookingNumberChange = { bookingNumber = it },
        startScanner = {
            scanner.startScan()
                .addOnSuccessListener {
                    bookingNumber = it.rawValue ?: ""
                }
        }
    )
}


@Composable
fun AddBookingNumberUI(
    navigateToHome: () -> Unit,
    saveBookingNumber: (String) -> Unit,
    bookingState: Response<Boolean>? = null,
    bookingNumber: String,
    onBookingNumberChange: (String) -> Unit,
    startScanner: () -> Unit,
) {

    val focusManager = LocalFocusManager.current


    Scaffold(
        containerColor = Color(0xff231F20)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(32.dp)
                .fillMaxSize(),
        ) {
            Column(
                modifier = Modifier.weight(1.0f, true),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_scan_qr),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                )
                Text(
                    text = "Get started",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = "Scan or input your booking number for full access",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.size(16.dp))
                TextField(
                    value = bookingNumber,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { onBookingNumberChange(it) },
                    placeholder = {
                        Text(text = "Your code here")
                    },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() })

                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = "Or",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.size(16.dp))
                Button(
                    onClick = {
                        startScanner()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(text = "Scan")
                    Spacer(modifier = Modifier.size(16.dp))
                    Image(
                        painter = painterResource(id = R.drawable.ic_qr_scanner),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.size(16.dp))
                Button(
                    onClick = {
                        saveBookingNumber(bookingNumber)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = bookingState != Response.Loading
                ) {
                    if (bookingState == Response.Loading) {
                        CircularProgressIndicator()
                    } else {
                        Text(text = "Submit")
                    }
                }
                Spacer(modifier = Modifier.size(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Joining as a guest? ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                    TextButton(onClick = {
                        navigateToHome()
                    }) {
                        Text(
                            text = "Skip",
                            style = MaterialTheme.typography.bodyLarge,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AddBookingNumberPagePreview() {
    NFQSnapshotTestThemeForPreview {
        AddBookingNumberUI(
            navigateToHome = {},
            saveBookingNumber = {},
            bookingState = null,
            bookingNumber = "",
            onBookingNumberChange = {},
            startScanner = {}
        )
    }
}