package com.nfq.nfqsummit.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun AnnotatedHtmlStringWithLink(
    modifier: Modifier = Modifier,
    style: TextStyle,
    htmlText: String = ""
) {
    Text(
        text =  AnnotatedString.fromHtml(
            htmlText.trimIndent(),
            linkStyles = TextLinkStyles(
                style = SpanStyle(
                    textDecoration = TextDecoration.Underline,
                    fontStyle = style.fontStyle,
                    color = Color.Blue
                )
            )
        ),
        style = style,
        modifier = modifier
    )
}