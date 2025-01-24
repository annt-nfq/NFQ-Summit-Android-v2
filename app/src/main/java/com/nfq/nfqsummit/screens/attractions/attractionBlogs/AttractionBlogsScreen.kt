@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.nfq.nfqsummit.screens.attractions.attractionBlogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.nfq.data.domain.model.Blog
import com.nfq.nfqsummit.components.BasicTopAppBar
import com.nfq.nfqsummit.components.bounceClick
import com.nfq.nfqsummit.mocks.mockBlog
import com.nfq.nfqsummit.mocks.mockFavoriteAndRecommendedBlog
import com.nfq.nfqsummit.ui.theme.NFQOrange
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview

@Composable
fun AttractionBlogsScreen(
    attractionTitle: String,
    attractionId: String,
    goBack: () -> Unit,
    goToBlog: (blogId: String) -> Unit,
    viewModel: AttractionBlogsViewModel = hiltViewModel()
) {
    val blogs by viewModel.blogs.collectAsState()

    AttractionBlogsUI(
        attractionTitle = attractionTitle,
        blogs = blogs,
        goBack = goBack,
        goToBlog = goToBlog,
        markAsFavorite = { favorite, blog ->
            viewModel.markBlogAsFavorite(favorite, blog)
        }
    )
}

@Composable
fun AttractionBlogsUI(
    attractionTitle: String,
    blogs: List<Blog>,
    goBack: () -> Unit,
    goToBlog: (blogId: String) -> Unit,
    markAsFavorite: (favorite: Boolean, blog: Blog) -> Unit
) {
    Scaffold(
        topBar = {
            BasicTopAppBar(
                title = attractionTitle,
                navigationUp = goBack
            )
        }
    ) { paddingValues ->

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (!blogs.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        "No Favorites Yet!",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "You have not marked any favorite attraction",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.5f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    items(
                        items = blogs,
                        key = { it.id },
                        contentType = { "BlogListItem" }
                    ) { blog ->
                        BlogListItem(
                            blog = blog,
                            goToBlog = goToBlog,
                            markAsFavorite = markAsFavorite
                        )
                    }
                }
            }
        }

    }
}

@Composable
fun BlogListItem(
    blog: Blog,
    goToBlog: (blogId: String) -> Unit,
    markAsFavorite: (favorite: Boolean, blog: Blog) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick()
            .clip(RoundedCornerShape(16.dp))
            .clickable { goToBlog(blog.id) }
            .padding(8.dp)
    ) {
        Text(
            text = blog.title,
            style = MaterialTheme.typography.titleLarge
        )
        if (blog.isRecommended) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(4.dp)
            ) {
                Icon(
                    Icons.Outlined.ThumbUp,
                    contentDescription = "Thumbs up",
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Recommended", style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
            }
        }
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
                    .padding(8.dp)
                    .size(40.dp)
                    .bounceClick()
                    .clip(shape = CircleShape)
                    .background(color = Color.White)
                    .clickable { markAsFavorite(!blog.isFavorite, blog) }
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
            attractionTitle = "Attraction Title",
            blogs = listOf(
                mockBlog,
                mockFavoriteAndRecommendedBlog
            ),
            goBack = {},
            goToBlog = {},
            markAsFavorite = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AttractionBlogsUIDarkPreview() {
    NFQSnapshotTestThemeForPreview(darkTheme = true) {
        AttractionBlogsUI(
            attractionTitle = "Attraction Title",
            blogs = listOf(
                mockBlog,
                mockFavoriteAndRecommendedBlog
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
            blog = mockFavoriteAndRecommendedBlog,
            goToBlog = {},
            markAsFavorite = { _, _ -> }
        )
    }
}