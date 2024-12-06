package com.nfq.nfqsummit.screens.blog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.nfq.data.domain.model.Blog
import com.nfq.nfqsummit.components.BasicTopAppBar
import com.nfq.nfqsummit.components.MarkdownDisplay
import com.nfq.nfqsummit.mocks.mockBlog
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview
import java.net.URL

@Composable
fun BlogScreen(
    blogId: Int,
    goBack: () -> Unit,
    viewModel: BlogViewModel = hiltViewModel()
) {

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
            BasicTopAppBar(
                title = blog?.title.orEmpty(),
                navigationUp = goBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
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
            blog = mockBlog,
            goBack = {}
        )
    }
}