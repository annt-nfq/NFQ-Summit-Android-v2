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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.nfq.data.domain.model.Attraction
import com.nfq.data.domain.model.Blog
import com.nfq.data.domain.model.Response
import com.nfq.nfqsummit.components.BasicTopAppBar
import com.nfq.nfqsummit.mocks.mockAttraction
import com.nfq.nfqsummit.mocks.mockBlog
import com.nfq.nfqsummit.mocks.mockFavoriteAndRecommendedBlog
import com.nfq.nfqsummit.ui.theme.NFQOrange
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview

@Composable
fun AttractionBlogsScreen(
    attractionId: Int,
    goBack: () -> Unit,
    goToBlog: (blogId: Int) -> Unit,
    viewModel: AttractionBlogsViewModel = hiltViewModel()
) {
    val blogsState by viewModel.blogs.collectAsState()
    val attractionState by viewModel.attraction.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.getAttraction(attractionId)
    }

    AttractionBlogsUI(
        attraction = attractionState,
        blogsState = blogsState,
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
    blogsState: Response<List<Blog>>,
    goBack: () -> Unit,
    goToBlog: (blogId: Int) -> Unit,
    markAsFavorite: (favorite: Boolean, blog: Blog) -> Unit
) {
    Scaffold(
        topBar = {
            BasicTopAppBar(
                title = attraction?.title ?: "Attraction",
                navigationUp = goBack
            )
        }
    ) { paddingValues ->
        when (blogsState) {
            is Response.Loading -> {
                Text("Loading")
            }

            is Response.Failure -> {
                Text(blogsState.e.message ?: "Error")
            }

            is Response.Success -> {
                LazyColumn(
                    modifier = Modifier.padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = blogsState.data!!,
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
    goToBlog: (blogId: Int) -> Unit,
    markAsFavorite: (favorite: Boolean, blog: Blog) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
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
            attraction = mockAttraction,
            blogsState = Response.Success(
                listOf(
                    mockBlog,
                    mockFavoriteAndRecommendedBlog
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
fun AttractionBlogsUIDarkPreview() {
    NFQSnapshotTestThemeForPreview(darkTheme = true) {
        AttractionBlogsUI(
            attraction = mockAttraction,
            blogsState = Response.Success(
                listOf(
                    mockBlog,
                    mockFavoriteAndRecommendedBlog
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
            blog = mockFavoriteAndRecommendedBlog,
            goToBlog = {},
            markAsFavorite = { _, _ -> }
        )
    }
}