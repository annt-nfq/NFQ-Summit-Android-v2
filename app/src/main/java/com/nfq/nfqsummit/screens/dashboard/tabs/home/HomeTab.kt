@file:OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)

package com.nfq.nfqsummit.screens.dashboard.tabs.home

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.times
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.nfq.data.domain.model.Blog
import com.nfq.data.domain.model.Response
import com.nfq.data.domain.model.SummitEvent
import com.nfq.nfqsummit.R
import com.nfq.nfqsummit.mocks.mockBlog
import com.nfq.nfqsummit.mocks.mockEventDay1
import com.nfq.nfqsummit.mocks.mockFavoriteAndRecommendedBlog
import com.nfq.nfqsummit.mocks.mockRecommendedBlog
import com.nfq.nfqsummit.ui.theme.MainGreen
import com.nfq.nfqsummit.ui.theme.MainNeutral
import com.nfq.nfqsummit.ui.theme.NFQOrange
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview
import com.nfq.nfqsummit.ui.theme.boxShadow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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

    var showQRCodeBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Scaffold { _ ->
        HomeTabUI(
            upcomingEvents = viewModel.upcomingEvents,
            goToEventDetails = goToEventDetails,
            favoriteBlogs = favoriteBlogsState,
            recommendedBlogs = recommendedBlogsState,
            goToBlog = goToBlog,
            goToAttractions = goToAttractions,
            onShowQRCode = {
                scope.launch {
                    showQRCodeBottomSheet = true
                }
            },
            markAsFavorite = viewModel::markAsFavorite
        )
        if (showQRCodeBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showQRCodeBottomSheet = false
                },
                sheetState = sheetState,
                modifier = Modifier.height(LocalConfiguration.current.screenHeightDp.dp * 0.7f),
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                QRCodeContent()
            }
        }
    }
}

@Composable
fun QRCodeContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 55.dp)
    ) {
        Text(
            text = "Hermann Hauser", style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.background
            )
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "UUID: 255373826", style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.W400, color = MaterialTheme.colorScheme.background
            )
        )
        Spacer(modifier = Modifier.height(26.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(6.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.sample_qrcode),
                contentDescription = null,
                modifier = Modifier.size(220.dp)
            )
        }
        Spacer(modifier = Modifier.height(26.dp))
        Text(
            text = "Please show this QR code to our staff\n" + "for check-in.",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.background,
                textAlign = TextAlign.Center,
            )
        )
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "Please ensure you’ve reserved a seat for each event. For assistance, contact Tris on WhatsApp.",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.W400, color = MaterialTheme.colorScheme.background,
                textAlign = TextAlign.Center,
            )
        )
        Spacer(modifier = Modifier.height(32.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .background(MainGreen)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_whatsapp),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "+76 333 2145", style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.background
                    )
                )
            }
        }

    }
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
    onShowQRCode: () -> Unit,
    markAsFavorite: (favorite: Boolean, blog: Blog) -> Unit
) {
    Scaffold {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            ShowQRCodeSection {
                onShowQRCode()
            }
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

@Composable
fun ShowQRCodeSection(
    onShowQRCode: () -> Unit,
) {
    Box(
        modifier = Modifier.padding(24.dp)
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(height = 117.dp)
            .boxShadow(
                color = Color(0xFF1E1C2E).copy(alpha = 0.08f),
                blurRadius = 48.dp,
                spreadRadius = 0.dp,
                offset = DpOffset(0.dp, 24.dp)
            )
            .clip(RoundedCornerShape(32.dp))
            .clickable {
                onShowQRCode()
            }
            .background(MaterialTheme.colorScheme.background)) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo_qrcode),
                    contentDescription = null,
                    modifier = Modifier.size(75.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Tap to show my QR Code",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You'll need to show this at NFQ Summit registration \uD83D\uDCCB",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.secondary
                        )
                    )
                }
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
    val configuration = LocalConfiguration.current

    val screenWidth = configuration.screenWidthDp.dp
    val itemWidth = (237.0 * screenWidth) / 393
    val itemHeight = (226.0 * itemWidth) / 237
    val paddingEnd = 16.dp + itemWidth / 2

    when (recommendedBlogs) {
        is Response.Success -> {
            val pagerState = rememberPagerState { recommendedBlogs.data!!.size }
            Column {
                SectionHeader("Upcoming Events") {

                }
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalPager(
                    state = pagerState, contentPadding = PaddingValues(
                        start = 24.dp, end = paddingEnd
                    ), modifier = Modifier.fillMaxWidth()
                ) {
                    println(recommendedBlogs.data!![it].toString())
                    HomeRecommendation(
                        blog = recommendedBlogs.data!![it],
                        markAsFavorite = markAsFavorite,
                        goToBlog = goToBlog,
                        itemHeight = itemHeight,
                    )
                }
            }
        }

        is Response.Failure -> {
            Text(
                text = "Failed to load recommendations", style = MaterialTheme.typography.bodyMedium
            )
        }

        is Response.Loading -> {
            Text(
                text = "Loading recommendations...", style = MaterialTheme.typography.bodyMedium
            )
        }
    }

}

@Composable
fun SectionHeader(title: String, onSeeAll: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(24.dp))
        Text(
            text = title, style = MaterialTheme.typography.headlineSmall.copy(
                color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.W400
            )
        )
        Spacer(modifier = Modifier.weight(1f))
        Box(modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                onSeeAll()
            }
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            .padding(horizontal = 12.dp, vertical = 8.dp)) {
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(24.dp))
    }

}

@Composable
fun HomeRecommendation(
    blog: Blog, goToBlog: (Int) -> Unit, markAsFavorite: (favorite: Boolean, blog: Blog) -> Unit,
    itemHeight: Dp,
) {
    Box(modifier = Modifier
        .height(itemHeight)
        .padding(end = 16.dp)
        .boxShadow(
            color = Color(0xFF969696).copy(alpha = 0.08f),
            blurRadius = 30.dp,
            spreadRadius = 0.dp,
            offset = DpOffset(0.dp, 8.dp)
        )
        .clip(RoundedCornerShape(16.dp))
        .clickable {

        }
        .background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .aspectRatio(ratio = 221 / 131f)
                    .fillMaxWidth()
            ) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    model = blog.iconUrl,
                    contentDescription = blog.title,
                    contentScale = ContentScale.Crop
                )
                Box(modifier = Modifier
                    .clickable {
                        markAsFavorite(!blog.isFavorite, blog)
                    }
                    .padding(8.dp)
                    .graphicsLayer(
                        alpha = 30f,
                        shadowElevation = 5f,
                        shape = RoundedCornerShape(7.dp),
                    )
                    .background(
                        color = Color.White.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(7.dp),
                    )
                    .align(Alignment.TopEnd)
                    .padding(8.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_bookmark),
                        contentDescription = null,
                        modifier = Modifier.width(14.dp),
                        colorFilter = ColorFilter.tint(if (blog.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary)
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .graphicsLayer(
                            alpha = 30f,
                            shadowElevation = 5f,
                            shape = RoundedCornerShape(7.dp),
                        )
                        .background(
                            color = Color.White.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(7.dp),
                        )
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Text(
                        "10\nJun",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.background,
                            textAlign = TextAlign.Center,
                            lineHeight = 1.em,
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            ),
                        ),
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            Text(
                blog.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp),
                        )
                        .padding(8.dp)
                ) {
                    Text(
                        "10:00 - 12:00",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.background,
                            fontWeight = FontWeight.Bold
                        ),
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        "\uD83D\uDCBC Summit",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold
                        ),
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
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
        SectionHeader("Your Saved Favorites") {

        }
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
                                blog = blog, goToBlog = goToBlog, markAsFavorite = markAsFavorite
                            )
                        }
                        item {
                            Spacer(Modifier.width(0.dp))
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .boxShadow(
                                color = Color(0xFF575C8A).copy(alpha = 0.06f),
                                blurRadius = 35.dp,
                                spreadRadius = 0.dp,
                                offset = DpOffset(0.dp, 10.dp)
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.background)
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
                            Text("View Attractions",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = NFQOrange, textDecoration = TextDecoration.Underline
                                ),
                                modifier = Modifier.clickable {
                                    goToAttractions()
                                })
                        }
                    }
                }
            }

            is Response.Failure -> {
                Text(
                    text = "Failed to load favorites", style = MaterialTheme.typography.bodyMedium
                )
            }

            is Response.Loading -> {
                Text(
                    text = "Loading favorites...", style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun HomeFavorite(
    blog: Blog, goToBlog: (Int) -> Unit, markAsFavorite: (favorite: Boolean, blog: Blog) -> Unit
) {
    Box(modifier = Modifier
        .height(88.dp)
        .clickable {
            goToBlog(blog.id)
        }
        .padding(horizontal = 24.dp)
        .boxShadow(
            color = Color(0xFF575C8A).copy(alpha = 0.06f),
            blurRadius = 35.dp,
            spreadRadius = 0.dp,
            offset = DpOffset(0.dp, 10.dp)
        )
        .clip(RoundedCornerShape(16.dp))

        .background(MaterialTheme.colorScheme.background)) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            AsyncImage(
                modifier = Modifier
                    .width(84.dp)
                    .clip(RoundedCornerShape(12.dp)),
                model = blog.iconUrl,
                contentDescription = blog.title,
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(18.dp))
            Column(
                modifier = Modifier.weight(1f), verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Wed, Jun 28 • 17:00",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                        ),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp),
                            )
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "\uD83D\uDCBC Summit",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            ),
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    blog.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 1.2.em,
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        ),
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
fun UpcomingEvent(
    modifier: Modifier = Modifier, event: SummitEvent, goToEventDetails: (String) -> Unit = {}
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
                text = event.name, style = MaterialTheme.typography.bodyLarge.copy(
                    color = MainNeutral, fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "${event.start.format(dateTimeFormatter)} - ${event.end.format(dateTimeFormatter)}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MainNeutral, fontWeight = FontWeight.Normal
                )
            )
        }
    }
}

@Composable
fun HomeHeader(
    upcomingEvents: List<SummitEvent>?, goToEventDetails: (String) -> Unit
) {

    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val headerDateFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .gradientBackground(
                colors = listOf(
                    Color(0xff1d1008), Color(0xff592602), Color(0xff953F01), NFQOrange
                ), angle = 80f
            )
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = headerDateFormatter.format(LocalDate.of(2024, 1, 6)),
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White, fontWeight = FontWeight.Bold
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
                event = upcomingEvents.first(), goToEventDetails = goToEventDetails
            )
            if (upcomingEvents.size > 1) AnimatedVisibility(visible = isExpanded) {
                Column {
                    upcomingEvents.subList(1, upcomingEvents.size).forEach {
                        UpcomingEvent(
                            event = it, goToEventDetails = goToEventDetails
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
            onShowQRCode = {},
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
            onShowQRCode = {},
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
            goToBlog = {},
            itemHeight = 226.dp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeFavoritePreview() {
    NFQSnapshotTestThemeForPreview {
        HomeFavorite(blog = mockFavoriteAndRecommendedBlog,
            goToBlog = {},
            markAsFavorite = { _, _ -> })
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

fun Modifier.gradientBackground(colors: List<Color>, angle: Float) = this.then(Modifier.drawBehind {
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
        ), size = size
    )
})