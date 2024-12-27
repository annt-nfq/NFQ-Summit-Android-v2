package com.nfq.nfqsummit.screens.blog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.nfq.data.domain.model.Blog
import com.nfq.data.network.utils.networkConnectivity.ConnectivityObserver
import com.nfq.nfqsummit.components.BasicTopAppBar
import com.nfq.nfqsummit.components.MarkdownDisplay
import com.nfq.nfqsummit.mocks.mockBlog
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview
import java.net.URL

@Composable
fun BlogScreen(
    blogId: String,
    goBack: () -> Unit,
    viewModel: BlogViewModel = hiltViewModel()
) {

    val blog by viewModel.blog.collectAsState()
    val networkStatus by viewModel.networkStatus.collectAsState()

    BlogUI(
        blog = blog,
        status = networkStatus,
        goBack = goBack
    )
}

@Composable
fun BlogUI(
    blog: Blog?,
    status: ConnectivityObserver.Status,
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
            if (blog != null && status == ConnectivityObserver.Status.Available)
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
            status = ConnectivityObserver.Status.Available,
            goBack = {}
        )
    }
}