package com.nfq.nfqsummit.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nfq.nfqsummit.R
import dev.jeziellago.compose.markdowntext.MarkdownText
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import kotlin.concurrent.thread

@Composable
fun MarkdownDisplay(
    url: URL
) {
    val urlContent = remember { mutableStateOf("") }
    thread {
        val reader = BufferedReader(InputStreamReader(url.openStream()))
        var line: String
        var mdText = ""
        while (reader.readLine().also { line = it ?: "" } != null) {
            mdText += "$line\n"
        }
        reader.close()
        urlContent.value = mdText
    }
    if (urlContent.value.isNotEmpty()) {
        MarkdownText(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            markdown = urlContent.value,
            fontResource = R.font.montserrat_regular
        )
    }
}