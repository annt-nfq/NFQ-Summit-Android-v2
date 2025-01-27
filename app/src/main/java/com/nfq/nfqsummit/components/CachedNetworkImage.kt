package com.nfq.nfqsummit.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

class ImageCache(private val context: Context) {
    private val cacheDir = context.cacheDir
    private val imageLoader = ImageLoader(context)

    private fun getFileName(url: String): String {
        return MessageDigest.getInstance("MD5")
            .digest(url.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

    private fun getCacheFile(fileName: String): File {
        return File(cacheDir, fileName)
    }

    suspend fun getImage(url: String): Bitmap? = withContext(Dispatchers.IO) {
        val fileName = getFileName(url)
        val cacheFile = getCacheFile(fileName)

        // Check if image exists in cache
        if (cacheFile.exists()) {
            return@withContext BitmapFactory.decodeFile(cacheFile.absolutePath)
        }

        // Download and cache if not found
        try {
            val request = ImageRequest.Builder(context)
                .data(url)
                .allowHardware(false)
                .build()

            val result = (imageLoader.execute(request) as? SuccessResult)?.drawable?.toBitmap()

            result?.let { bitmap ->
                FileOutputStream(cacheFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
            }

            return@withContext result
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }
}

@Composable
fun CachedNetworkImage(
    imageUrl: String,
    contentDescription: String?,
    contentScale: ContentScale = ContentScale.Fit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageCache = remember { ImageCache(context) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(imageUrl) {
        bitmap = imageCache.getImage(imageUrl)
    }

    bitmap?.let { cachedBitmap ->
        Image(
            bitmap = cachedBitmap.asImageBitmap(),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    }
}