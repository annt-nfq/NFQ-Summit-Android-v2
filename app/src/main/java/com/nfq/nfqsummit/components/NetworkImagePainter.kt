package com.nfq.nfqsummit.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.nfq.nfqsummit.R

@Composable
fun networkImagePainter(
    url: String?,
): Painter = rememberAsyncImagePainter(
    model = ImageRequest.Builder(LocalContext.current)
        .data(url)
        .crossfade(true)
        .error(R.drawable.ic_placeholder_default)
        .size(coil.size.Size.ORIGINAL)
        .build(),
)