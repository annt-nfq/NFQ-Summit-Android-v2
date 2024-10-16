@file:OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)

package com.nfq.nfqsummit.screens.dashboard.tabs.home

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.nfq.data.domain.model.Blog
import com.nfq.data.domain.model.Response
import com.nfq.data.domain.model.SummitEvent
import com.nfq.nfqsummit.mocks.mockBlog
import com.nfq.nfqsummit.mocks.mockEventDay1
import com.nfq.nfqsummit.mocks.mockFavoriteAndRecommendedBlog
import com.nfq.nfqsummit.mocks.mockRecommendedBlog
import com.nfq.nfqsummit.ui.theme.MainNeutral
import com.nfq.nfqsummit.ui.theme.NFQOrange
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun HomeTab(
    viewModel: HomeViewModel = hiltViewModel(),
    goToEventDetails: (String) -> Unit,
    goToBlog: (Int) -> Unit,
    goToAttractions: () -> Unit,
) {
    val window = (LocalView.current.context as Activity).window
    window.statusBarColor = Color.Transparent.toArgb()
    WindowCompat.setDecorFitsSystemWindows(window, false)

    val favoriteBlogsState by viewModel.favoriteBlogs.collectAsState()
    val recommendedBlogsState by viewModel.recommendedBlogs.collectAsState()

    HomeTabUI(
        upcomingEvents = viewModel.upcomingEvents,
        goToEventDetails = goToEventDetails,
        favoriteBlogs = favoriteBlogsState,
        recommendedBlogs = recommendedBlogsState,
        goToBlog = goToBlog,
        goToAttractions = goToAttractions,
        markAsFavorite = viewModel::markAsFavorite
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeTabUI(
    upcomingEvents: List<SummitEvent>?,
    goToEventDetails: (String) -> Unit = {},
    recommendedBlogs: Response<List<Blog>>,
    favoriteBlogs: Response<List<Blog>>,
    goToBlog: (Int) -> Unit,
    goToAttractions: () -> Unit,
    markAsFavorite: (favorite: Boolean, blog: Blog) -> Unit
) {
    Scaffold {
        Column {
            HomeHeader(
                upcomingEvents = upcomingEvents,
                goToEventDetails = goToEventDetails
            )
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                HomeRecommendationsSection(
                    recommendedBlogs = recommendedBlogs,
                    markAsFavorite = markAsFavorite,
                    goToBlog = goToBlog
                )
                Spacer(modifier = Modifier.height(24.dp))
                HomeFavoritesSection(
                    favoriteBlogs = favoriteBlogs,
                    goToBlog = goToBlog,
                    markAsFavorite = markAsFavorite,
                    goToAttractions = goToAttractions
                )
                Spacer(modifier = Modifier.height(120.dp))
            }
        }
    }
}

@Composable
fun HomeRecommendationsSection(
    recommendedBlogs: Response<List<Blog>>,
    goToBlog: (Int) -> Unit,
    markAsFavorite: (favorite: Boolean, blog: Blog) -> Unit
) {
    when (recommendedBlogs) {
        is Response.Success -> {
            val pagerState = rememberPagerState { recommendedBlogs.data!!.size }
            Column {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "Recommendations Today",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                HorizontalPager(state = pagerState) {
                    HomeRecommendation(
                        blog = recommendedBlogs.data!![it],
                        markAsFavorite = markAsFavorite,
                        goToBlog = goToBlog
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(pagerState.pageCount) { iteration ->
                        val color =
                            if (pagerState.currentPage == iteration) NFQOrange else Color.LightGray
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(8.dp)
                        )
                    }
                }
            }
        }

        is Response.Failure -> {
            Text(
                text = "Failed to load recommendations",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        is Response.Loading -> {
            Text(
                text = "Loading recommendations...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

}

@Composable
fun HomeRecommendation(
    blog: Blog,
    goToBlog: (Int) -> Unit,
    markAsFavorite: (favorite: Boolean, blog: Blog) -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .clickable { goToBlog(blog.id) }
                .padding(16.dp)) {
            Text(
                text = blog.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
            ) {
                AsyncImage(
                    modifier = Modifier
                        .height(120.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp)),
                    model = blog.iconUrl,
                    contentDescription = blog.title,
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
        }
    }
}

@Composable
fun HomeFavoritesSection(
    favoriteBlogs: Response<List<Blog>>,
    goToBlog: (Int) -> Unit,
    goToAttractions: () -> Unit,
    markAsFavorite: (favorite: Boolean, blog: Blog) -> Unit
) {
    Column {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = "Your Saved Favorites",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        when (favoriteBlogs) {
            is Response.Success -> {
                if (favoriteBlogs.data?.isNotEmpty() == true) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Spacer(Modifier.width(0.dp))
                        }
                        items(favoriteBlogs.data!!) { blog ->
                            HomeFavorite(
                                blog = blog,
                                goToBlog = goToBlog,
                                markAsFavorite = markAsFavorite
                            )
                        }
                        item {
                            Spacer(Modifier.width(0.dp))
                        }
                    }
                } else {
                    OutlinedCard(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Column(
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
                                "You have not marked any favorites",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "View Attractions",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = NFQOrange,
                                    textDecoration = TextDecoration.Underline
                                ),
                                modifier = Modifier.clickable {
                                    goToAttractions()
                                }
                            )
                        }
                    }
                }
            }

            is Response.Failure -> {
                Text(
                    text = "Failed to load favorites",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            is Response.Loading -> {
                Text(
                    text = "Loading favorites...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun HomeFavorite(
    blog: Blog,
    goToBlog: (Int) -> Unit,
    markAsFavorite: (favorite: Boolean, blog: Blog) -> Unit
) {
    OutlinedCard(modifier = Modifier.clickable {
        goToBlog(blog.id)
    }) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = blog.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(220.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    model = blog.iconUrl,
                    contentDescription = blog.title,
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
        }
    }
}

@Composable
fun UpcomingEvent(
    modifier: Modifier = Modifier,
    event: SummitEvent,
    goToEventDetails: (String) -> Unit = {}
) {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    Row(modifier = modifier
        .padding(8.dp)
        .clickable {
            goToEventDetails(event.id)
        }) {
        Box(
            modifier = Modifier
                .padding(4.dp)
                .background(Color(0xFF3AB34A), shape = CircleShape)
                .size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column {
            Text(
                text = event.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MainNeutral,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "${event.start.format(dateTimeFormatter)} - ${event.end.format(dateTimeFormatter)}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MainNeutral,
                    fontWeight = FontWeight.Normal
                )
            )
        }
    }
}

@Composable
fun HomeHeader(
    upcomingEvents: List<SummitEvent>?,
    goToEventDetails: (String) -> Unit
) {

    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val headerDateFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .gradientBackground(
                colors = listOf(
                    Color(0xff1d1008),
                    Color(0xff592602),
                    Color(0xff953F01),
                    NFQOrange
                ),
                angle = 80f
            )
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = headerDateFormatter.format(LocalDate.of(2024, 1, 6)),
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
            Icon(
                if (isExpanded) Icons.Default.KeyboardArrowUp
                else Icons.Default.KeyboardArrowDown,
                contentDescription = "Expand/Collapse",
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        isExpanded = !isExpanded
                    },
                tint = Color.White,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        upcomingEvents?.let {
            UpcomingEvent(
                event = upcomingEvents.first(),
                goToEventDetails = goToEventDetails
            )
            if (upcomingEvents.size > 1)
                AnimatedVisibility(visible = isExpanded) {
                    Column {
                        upcomingEvents.subList(1, upcomingEvents.size).forEach {
                            UpcomingEvent(
                                event = it,
                                goToEventDetails = goToEventDetails
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeTabUIPreview() {
    NFQSnapshotTestThemeForPreview {
        HomeTabUI(
            upcomingEvents = listOf(
                SummitEvent(
                    id = "1",
                    name = "Event name",
                    start = LocalDateTime.of(2024, 1, 6, 10, 0),
                    end = LocalDateTime.of(2024, 1, 6, 11, 0)
                )
            ),
            goToBlog = {},
            goToAttractions = {},
            markAsFavorite = { _, _ -> },
            favoriteBlogs = Response.Success(
                listOf(mockFavoriteAndRecommendedBlog, mockBlog)
            ),
            recommendedBlogs = Response.Success(
                listOf(
                    mockFavoriteAndRecommendedBlog, mockRecommendedBlog
                )
            )
        )
    }
}

@Preview
@Composable
fun HomeTabUIDarkPreview() {
    NFQSnapshotTestThemeForPreview(darkTheme = true) {
        HomeTabUI(
            upcomingEvents = listOf(
                mockEventDay1
            ),
            goToBlog = {},
            goToAttractions = {},
            markAsFavorite = { _, _ -> },
            favoriteBlogs = Response.Success(
                listOf(mockFavoriteAndRecommendedBlog, mockBlog)
            ),
            recommendedBlogs = Response.Success(
                listOf(
                    mockFavoriteAndRecommendedBlog, mockRecommendedBlog
                )
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeRecommendationPreview() {
    NFQSnapshotTestThemeForPreview {
        HomeRecommendation(
            blog = mockFavoriteAndRecommendedBlog,
            markAsFavorite = { _, _ -> },
            goToBlog = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeFavoritePreview() {
    NFQSnapshotTestThemeForPreview {
        HomeFavorite(
            blog = mockFavoriteAndRecommendedBlog,
            goToBlog = {},
            markAsFavorite = { _, _ -> }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF001d36)
@Composable
fun UpcomingEventPreview() {
    NFQSnapshotTestThemeForPreview {
        UpcomingEvent(
            event = mockEventDay1
        )
    }
}

fun Modifier.gradientBackground(colors: List<Color>, angle: Float) = this.then(
    Modifier.drawBehind {
        val angleRad = angle / 180f * PI
        val x = cos(angleRad).toFloat() //Fractional x
        val y = sin(angleRad).toFloat() //Fractional y

        val radius = sqrt(size.width.pow(2) + size.height.pow(2)) / 2f
        val offset = center + Offset(x * radius, y * radius)

        val exactOffset = Offset(
            x = min(offset.x.coerceAtLeast(0f), size.width),
            y = size.height - min(offset.y.coerceAtLeast(0f), size.height)
        )

        drawRect(
            brush = Brush.linearGradient(
                colors = colors,
                start = Offset(size.width, size.height) - exactOffset,
                end = exactOffset
            ),
            size = size
        )
    }
)