package com.nfq.nfqsummit.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isUnspecified
import androidx.compose.ui.unit.sp

@Composable
fun AutoResizedText(
    text: String,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    modifier: Modifier = Modifier,
    color: Color = style.color,
    maxLines: Int = 2,
    minFontSize: TextUnit = 11.sp,
    maxFontSize: TextUnit = style.fontSize.takeIf { !it.isUnspecified } ?: 24.sp
) {
    var resizedTextStyle by remember { mutableStateOf(style.copy(fontSize = maxFontSize)) }
    var shouldDraw by remember { mutableStateOf(false) }
    var lineCount by remember { mutableIntStateOf(0) }

    Text(
        text = text,
        color = color,
        modifier = modifier.drawWithContent { if (shouldDraw) drawContent() },
        style = resizedTextStyle,
        onTextLayout = { result ->
            lineCount = result.lineCount
            if (lineCount > maxLines) {
                if (resizedTextStyle.fontSize > minFontSize) {
                    resizedTextStyle = resizedTextStyle.copy(
                        fontSize = (resizedTextStyle.fontSize * 0.95f).value
                            .coerceIn(minFontSize.value, maxFontSize.value).sp
                    )
                } else {
                    shouldDraw = true
                }
            } else {
                shouldDraw = true
            }
        }
    )
}