package com.nfq.nfqsummit.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.core.text.HtmlCompat

@Composable
fun HtmlText(
    html: String,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    Text(
        text = AnnotatedString(
            HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY).toString().trim()
        ),
        style = style,
        modifier = modifier
    )
}