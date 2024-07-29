@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.nfq.nfqsummit.screens.attractions.attractionBlogs

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.nfq.data.domain.model.Attraction
import com.nfq.data.domain.model.Blog
import com.nfq.nfqsummit.ui.theme.NFQOrange
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview

@Composable
fun AttractionBlogsScreen(
    attractionId: Int,
    goBack: () -> Unit,
    goToBlog: (blogId: Int) -> Unit,
    viewModel: AttractionBlogsViewModel = hiltViewModel()
) {
    val window = (LocalView.current.context as Activity).window
    window.statusBarColor = Color.Transparent.toArgb()

    LaunchedEffect(viewModel) {
        viewModel.getAttraction(attractionId)
        viewModel.getBlogs(attractionId)
    }

    AttractionBlogsUI(
        attraction = viewModel.attraction,
        viewModel.blogs ?: emptyList(),
        goBack = goBack,
        goToBlog = goToBlog,
        markAsFavorite = { favorite, blog ->
            viewModel.markBlogAsFavorite(favorite, blog)
        }
    )
}

@Composable
fun AttractionBlogsUI(
    attraction: Attraction?,
    blogs: List<Blog>,
    goBack: () -> Unit,
    goToBlog: (blogId: Int) -> Unit,
    markAsFavorite: (favorite: Boolean, blog: Blog) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = attraction?.title ?: "Attraction",
                        style = MaterialTheme.typography.headlineLarge
                    )
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
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(blogs) { blog ->
                BlogListItem(
                    blog = blog,
                    goToBlog = goToBlog,
                    markAsFavorite = markAsFavorite
                )
            }
        }
    }
}

@Composable
fun BlogListItem(
    blog: Blog,
    goToBlog: (blogId: Int) -> Unit,
    markAsFavorite: (favorite: Boolean, blog: Blog) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                goToBlog(blog.id)
            }
    ) {
        Text(
            text = blog.title,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
        ) {
            AsyncImage(
                model = blog.iconUrl,
                contentDescription = null,
                modifier = Modifier
                    .height(240.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .clickable {
                        markAsFavorite(!blog.isFavorite, blog)
                    }
                    .padding(8.dp)
                    .size(40.dp)
                    .background(
                        color = Color.White,
                        shape = CircleShape
                    )
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                Icon(
                    if (blog.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    tint = NFQOrange,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = blog.description,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Read more",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = NFQOrange,
                textDecoration = TextDecoration.Underline
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AttractionBlogsUIPreview() {
    NFQSnapshotTestThemeForPreview {
        AttractionBlogsUI(
            attraction = Attraction(
                id = 1,
                title = "Attraction Title",
                icon = ""
            ),
            blogs = listOf(
                Blog(
                    id = 1,
                    title = "Blog Title",
                    description = "Blog description",
                    iconUrl = "",
                    contentUrl = "",
                    attractionId = 2,
                    isFavorite = true
                ),
                Blog(
                    id = 2,
                    title = "Blog Title",
                    description = "Blog description",
                    iconUrl = "",
                    contentUrl = "",
                    attractionId = 2,
                    isFavorite = false
                )
            ),
            goBack = {},
            goToBlog = {},
            markAsFavorite = { _, _ -> }
        )
    }

}

@Preview(showBackground = true)
@Composable
fun BlogListItemPreview() {
    NFQSnapshotTestThemeForPreview {
        BlogListItem(
            blog = Blog(
                id = 1,
                title = "Blog Title",
                description = "Blog description",
                iconUrl = "",
                contentUrl = "",
                attractionId = 2,
                isFavorite = false
            ),
            goToBlog = {},
            markAsFavorite = { _, _ -> }
        )
    }
}