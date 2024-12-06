package com.nfq.nfqsummit.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import timber.log.Timber

fun Context.decodeQRCode(imageUri: Uri): String? {
    val inputStream = contentResolver.openInputStream(imageUri)
    val bitmap = BitmapFactory.decodeStream(inputStream)

    if (bitmap == null) {
        Timber.e("QRCodeDecoder", "Failed to decode bitmap")
        return null
    }

    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    val source = RGBLuminanceSource(width, height, pixels)
    val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

    return try {
        val result = MultiFormatReader().decode(binaryBitmap)
        result.text
    } catch (e: Exception) {
        Timber.e("QRCodeDecoder", "Decoding failed", e)
        null
    }
}

fun handleQRCodeUri(
    context: Context,
    uri: Uri,
    onSuccess: (String) -> Unit
) {
    try {
        val decodedText = context.decodeQRCode(uri)
        when {
            decodedText != null -> onSuccess(decodedText)
            else -> UserMessageManager.showMessage("No QR code found")
        }
    } catch (e: Exception) {
        UserMessageManager.showMessage("Error decoding QR code: ${e.message}")
    }
}