package com.nfq.nfqsummit.screens.onboarding

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nfq.nfqsummit.R
import com.nfq.nfqsummit.components.bounceClick
import com.nfq.nfqsummit.ui.theme.Grey
import com.nfq.nfqsummit.ui.theme.LightGrey
import com.nfq.nfqsummit.ui.theme.NFQOrange
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview
import com.nfq.nfqsummit.ui.theme.boxShadow
import com.nfq.nfqsummit.ui.theme.ubuntuFamily
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState { 2 }
    OnboardingContent(
        pagerState = pagerState,
        onNext = {
            coroutineScope.launch {
                if (pagerState.currentPage < pagerState.pageCount - 1) {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                } else {
                    viewModel.updateOnboardingStatus(true)
                }
            }
        },
        onSkip = {
            viewModel.updateOnboardingStatus(true)
        }
    )
}


@Composable
fun OnboardingPage1() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NFQOrange)
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 150.dp),
            horizontalAlignment = Alignment.End
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_nfq_text_white),
                contentDescription = null,
                modifier = Modifier
                    .width(300.dp)
                    .wrapContentHeight(),
                alignment = Alignment.CenterEnd
            )
            Text(
                text = "Summit\n2024",
                style = MaterialTheme.typography.displayLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 56.sp
                ),
                textAlign = TextAlign.End
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Embracing innovation,\nigniting possibilities",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White
                ),
                textAlign = TextAlign.End,
                modifier = Modifier.navigationBarsPadding()
            )
        }
    }
}

@Composable
private fun OnboardingContent(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .navigationBarsPadding()
            ) {
                TextButton(
                    onClick = onSkip,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .bounceClick()
                        .padding(start = 20.dp)
                ) {
                    Text(
                        text = "Skip",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .bounceClick()
                        .padding(end = 32.dp)
                        .size(64.dp, 51.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onNext() }
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_arrow_right),
                        contentDescription = null,
                        modifier = Modifier.size(25.6.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(innerPadding)
        ) {
            HorizontalPager(
                state = pagerState,
                verticalAlignment = Alignment.Top,
            ) { page ->
                when (page) {
                    0 -> OnboardingPage(
                        modifier = Modifier.fillMaxHeight(0.78f),
                        imageRes = R.drawable.onboarding1,
                        title = "Explore Events",
                        description = "Discover every event and stay informed! Explore the NFQ Summit 2025 calendar to ensure you never miss any exciting sessions, workshops, or networking opportunities."
                    )

                    1 -> OnboardingPage(
                        modifier = Modifier.fillMaxHeight(0.78f),
                        imageRes = R.drawable.onboarding2,
                        title = "Experience Thailand & Vietnam",
                        description = "Explore these vibrant destinations with expert guides, uncovering landmarks, culture, and local flavors."
                    )
                }
            }

            PagerIndicator(
                pageCount = pagerState.pageCount,
                currentPageIndex = pagerState.currentPage,
                modifier = Modifier.padding(top = 40.dp)
            )
        }
    }
}

@Composable
private fun PagerIndicator(
    modifier: Modifier = Modifier,
    pageCount: Int,
    currentPageIndex: Int
) {
    Row(
        modifier = modifier
            .animateContentSize()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPageIndex
            val width by animateDpAsState(
                targetValue = if (isSelected) 40.dp else 10.dp,
                animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy), label = ""
            )
            Box(
                modifier = Modifier
                    .size(width, 10.dp)
                    .clip(CircleShape)
                    .then(
                        if (isSelected) {
                            Modifier
                                .background(MaterialTheme.colorScheme.primary)
                                .boxShadow(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    blurRadius = 4.dp,
                                    spreadRadius = 1.dp,
                                    offset = DpOffset(2.dp, 2.dp)
                                )
                        } else Modifier.border(
                            width = 1.dp,
                            color = LightGrey,
                            shape = CircleShape
                        )
                    )
            )
        }
    }
}

@Composable
private fun OnboardingPage(
    modifier: Modifier = Modifier,
    @DrawableRes imageRes: Int,
    title: String,
    description: String
) {
    Surface(
        modifier = modifier
    ) {

        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 33.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_nfq_text),
                contentDescription = "ic_nfq_text",
            )

            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "onboarding_image",
                modifier = Modifier.padding(top = 48.dp)
            )

            Text(
                text = title,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontFamily = ubuntuFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    lineHeight = 27.58.sp
                ),
                modifier = Modifier.padding(top = 33.dp)
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Grey,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }

}

@Preview
@Composable
private fun OnboardingContentPreview() {
    NFQSnapshotTestThemeForPreview {
        OnboardingContent(
            pagerState = rememberPagerState { 2 },
            onNext = {},
            onSkip = {}
        )
    }
}

@Preview
@Composable
private fun OnboardingPagePreview() {
    NFQSnapshotTestThemeForPreview {
        OnboardingPage(
            imageRes = R.drawable.onboarding1,
            title = "Explore Events",
            description = "Discover every event and stay informed! Explore the NFQ Summit 2025 calendar to ensure you never miss any exciting sessions, workshops, or networking opportunities.",
            modifier = Modifier
        )
    }
}

@Preview
@Composable
private fun PagerIndicatorPreview() {
    NFQSnapshotTestThemeForPreview {
        PagerIndicator(pageCount = 2, currentPageIndex = 0)
    }
}
