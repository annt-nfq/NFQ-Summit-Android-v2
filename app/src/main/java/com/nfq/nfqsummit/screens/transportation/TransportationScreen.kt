package com.nfq.nfqsummit.screens.transportation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.nfq.data.domain.model.Blog
import com.nfq.nfqsummit.components.BasicTopAppBar
import com.nfq.nfqsummit.components.bounceClick
import com.nfq.nfqsummit.mocks.mockBlog
import com.nfq.nfqsummit.ui.theme.NFQOrange
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview

@Composable
fun TransportationScreen(
    goBack: () -> Unit,
    goToBlog: (blogId: Int) -> Unit,
    viewModel: TransportationViewModel = hiltViewModel()
) {

    LaunchedEffect(viewModel) {
        viewModel.getTransportationBlogs()
    }

    TransportationScreenUI(
        blogs = viewModel.blogs,
        goToBlog = goToBlog,
        goBack = goBack
    )
}

@Composable
fun TransportationScreenUI(
    goBack: () -> Unit,
    goToBlog: (blogId: Int) -> Unit,
    blogs: List<Blog>?
) {
    Scaffold(
        topBar = {
            BasicTopAppBar(
                title = "Transportation",
                navigationUp = goBack
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
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
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .bounceClick()
            .clip(RoundedCornerShape(16.dp))
            .background(color = NFQOrange)
            .clickable { goToBlog(blog.id) },

    ) {
        Text(
            text = blog.title,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                fontWeight = FontWeight(600)
            ),
            textAlign = TextAlign.Center
        )
        AsyncImage(
            model = blog.iconUrl,
            contentDescription = blog.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Fit,
            alignment = Alignment.BottomCenter
        )
    }
}

@Preview
@Composable
fun TransportationScreenUIPreview() {
    NFQSnapshotTestThemeForPreview {
        TransportationScreenUI(
            goBack = {},
            goToBlog = {},
            blogs = listOf(
                mockBlog,
                mockBlog,
                mockBlog
            )
        )
    }
}

@Preview
@Composable
fun TransportationScreenUIDarkPreview() {
    NFQSnapshotTestThemeForPreview(darkTheme = true) {
        TransportationScreenUI(
            goBack = {},
            goToBlog = {},
            blogs = listOf(
                mockBlog,
                mockBlog,
                mockBlog
            )
        )
    }
}