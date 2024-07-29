@file:OptIn(ExperimentalMaterial3Api::class)

package com.nfq.nfqsummit.screens.transportation

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.nfq.data.domain.model.Blog
import com.nfq.nfqsummit.ui.theme.NFQOrange
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview

@Composable
fun TransportationScreen(
    goBack: () -> Unit,
    goToBlog: (blogId: Int) -> Unit,
    viewModel: TransportationViewModel = hiltViewModel()
) {
    val window = (LocalView.current.context as Activity).window
    window.statusBarColor = Color.Transparent.toArgb()

    LaunchedEffect(viewModel) {
        viewModel.getTransportationBlogs()
    }

    TransportationScreenUI(
        blogs = viewModel.blogs,
        goToBlog = goToBlog,
        goBack = goBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportationScreenUI(
    goBack: () -> Unit,
    goToBlog: (blogId: Int) -> Unit,
    blogs: List<Blog>?
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Transportation",
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = goBack
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid (
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            items(blogs ?: emptyList()) { blog ->
                BlogGridItem(
                    blog = blog,
                    goToBlog = goToBlog
                )
            }
        }
    }
}

@Composable
fun BlogGridItem(
    blog: Blog,
    goToBlog: (blogId: Int) -> Unit
) {
    Column(
        modifier = Modifier
            .background(color = NFQOrange, shape = RoundedCornerShape(16.dp))
            .clickable {
                goToBlog(blog.id)
            }
            .clip(
                RoundedCornerShape(16.dp)
            ),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = blog.title,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                fontWeight = FontWeight(600)
            ),
            maxLines = 2,
            minLines = 2,
            textAlign = TextAlign.Center
        )
        AsyncImage(
            model = blog.iconUrl,
            contentDescription = blog.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.FillWidth,
            alignment = Alignment.BottomCenter
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TransportationScreenUIPreview() {
    NFQSnapshotTestThemeForPreview {
        TransportationScreenUI(
            goBack = {},
            goToBlog = {},
            blogs = listOf(
                Blog(
                    id = 1,
                    title = "Blog title",
                    contentUrl = "",
                    description = "",
                    iconUrl = "",
                    attractionId = 2,
                    isFavorite = true
                ),
                Blog(
                    id = 2,
                    title = "Blog title",
                    contentUrl = "",
                    description = "",
                    iconUrl = "",
                    attractionId = 2,
                    isFavorite = true
                ),
                Blog(
                    id = 3,
                    title = "Blog title",
                    contentUrl = "",
                    description = "",
                    iconUrl = "",
                    attractionId = 2,
                    isFavorite = false
                )
            )
        )
    }
}