@file:OptIn(ExperimentalMaterial3Api::class)

package com.nfq.nfqsummit.screens.blog

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.nfq.data.domain.model.Blog
import com.nfq.nfqsummit.components.MarkdownDisplay
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview
import java.net.URL

@Composable
fun BlogScreen(
    blogId: Int,
    goBack: () -> Unit,
    viewModel: BlogViewModel = hiltViewModel()
) {
    val window = (LocalView.current.context as Activity).window
    window.statusBarColor = Color.Transparent.toArgb()

    LaunchedEffect(viewModel) {
        viewModel.getBlogById(blogId)
    }

    BlogUI(blog = viewModel.blog, goBack = goBack)
}

@Composable
fun BlogUI(
    blog: Blog?,
    goBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    blog?.let {
                        Text(
                            text = it.title,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())) {
            if (blog != null)
                MarkdownDisplay(
                    URL(blog.contentUrl)
                )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BlogUIPreview() {
    NFQSnapshotTestThemeForPreview {
        BlogUI(
            blog = Blog(
                id = 1,
                title = "Blog title",
                contentUrl = "",
                description = "",
                iconUrl = "",
                attractionId = 2,
                isFavorite = true
            ),
            goBack = {}
        )
    }
}