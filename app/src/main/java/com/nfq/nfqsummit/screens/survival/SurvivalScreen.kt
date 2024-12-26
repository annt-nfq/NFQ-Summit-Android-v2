@file:OptIn(ExperimentalMaterial3Api::class)

package com.nfq.nfqsummit.screens.survival

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nfq.data.domain.model.Translation
import com.nfq.data.domain.model.TranslationAudio
import com.nfq.nfqsummit.R
import com.nfq.nfqsummit.components.BasicTopAppBar
import com.nfq.nfqsummit.components.bounceClick
import com.nfq.nfqsummit.mocks.mockTranslation
import com.nfq.nfqsummit.mocks.mockTranslationAudio
import com.nfq.nfqsummit.ui.theme.NFQOrange
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview

@Composable
fun SurvivalScreen(
    goBack: () -> Unit,
    viewModel: SurvivalViewModel = hiltViewModel()
) {
    LaunchedEffect(viewModel) {
        viewModel.getTranslations()
    }

    SurvivalScreenUI(
        goBack = goBack,
        translations = viewModel.translations
    )
}

class AudioPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    fun play(url: String) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepare()
            start()
        }
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

@Composable
fun SurvivalScreenUI(
    goBack: () -> Unit,
    translations: List<Translation>?
) {
    val context = LocalContext.current
    val audioPlayer = remember { AudioPlayer(context) }
    var isPlaying by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            BasicTopAppBar(
                title = "Survival Guide",
                navigationUp = goBack
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (translations != null)
                items(translations) { translation ->
                    TranslationListItem(
                        translation,
                        playAudio = {
                            audioPlayer.play(it)
                        }
                    )
                }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            audioPlayer.stop()
        }
    }
}

@Composable
fun TranslationListItem(
    translation: Translation,
    playAudio: (audioUrl: String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            translation.title,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        translation.audios.forEach { audio ->
            AudioListItem(
                audio,
                playAudio = playAudio
            )
        }
    }   // Translation item
}

@Composable
fun AudioListItem(
    audio: TranslationAudio,
    playAudio: (audioUrl: String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            audio.title,
            style = MaterialTheme.typography.bodyLarge
        )
        IconButton(
            modifier = Modifier
                .bounceClick()
                .clip(CircleShape),
            colors = IconButtonDefaults.iconButtonColors(containerColor = NFQOrange),
            onClick = {
                playAudio(audio.audioUrl)
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_outline_volume_up),
                contentDescription = "Play",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }   // Audio item
}

@Preview
@Composable
fun SurvivalScreenUIPreview() {
    NFQSnapshotTestThemeForPreview {
        SurvivalScreenUI(
            goBack = {},
            translations = listOf(
                mockTranslation,
                mockTranslation
            )
        )
    }

}

@Preview
@Composable
fun SurvivalScreenUIDarkPreview() {
    NFQSnapshotTestThemeForPreview(darkTheme = true) {
        SurvivalScreenUI(
            goBack = {},
            translations = listOf(
                mockTranslation,
                mockTranslation
            )
        )
    }

}

@Preview(showBackground = true)
@Composable
fun TranslationListItemPreview() {
    NFQSnapshotTestThemeForPreview {
        TranslationListItem(
            translation = Translation(
                id = "1",
                title = "Title",
                audios = listOf(
                    mockTranslationAudio,
                    mockTranslationAudio
                )
            ),
            playAudio = {}
        )
    }
}