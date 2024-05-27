package com.nfq.nfqsummit.screens.onboarding

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nfq.nfqsummit.R
import com.nfq.nfqsummit.ui.theme.NFQOrange
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestThemeForPreview
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    navigateToHome: () -> Unit,
    navigateToBooking: () -> Unit,
//    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState { 3 }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NFQOrange)
    ) {
        HorizontalPager(state = pagerState) { page ->
            when (page) {
                0 -> OnboardingPage1()
                1 -> OnboardingPage2()
                2 -> OnboardingPage3()
            }
        }
        Column(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                FloatingActionButton(
                    onClick = {
                        if (pagerState.currentPage < pagerState.pageCount - 1)
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        else {
//                            viewModel.setOnboardingViewed()
                            navigateToBooking()
                        }
                    },
                    shape = CircleShape,
                    modifier = Modifier.align(Alignment.Center),
                    elevation = FloatingActionButtonDefaults.elevation(0.dp),
                    containerColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = NFQOrange
                    )
                }
                SmoothCircularProgressBar(
                    indicatorProgress = when (pagerState.currentPage) {
                        0 -> 0f
                        1 -> 0.5f
                        2 -> 1f
                        else -> 0f
                    },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(80.dp),
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                Modifier
                    .wrapContentSize()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color =
                        if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
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
                textAlign = TextAlign.End
            )
        }
    }
}

@Preview
@Composable
fun OnboardingPage1Preview() {
    NFQSnapshotTestThemeForPreview {
        OnboardingPage1()
    }
}

@Composable
fun OnboardingPage2() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NFQOrange)
            .paint(painterResource(id = R.drawable.onboarding_2), contentScale = ContentScale.Crop)
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 150.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "Explore\nEvents",
                style = MaterialTheme.typography.displayLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 56.sp
                ),
                textAlign = TextAlign.End
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Discover every event!\nExplore the calendar",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White
                ),
                textAlign = TextAlign.End
            )
        }
    }
}

@Preview
@Composable
fun OnboardingPage2Preview() {
    NFQSnapshotTestThemeForPreview {
        OnboardingPage2()
    }
}

@Composable
fun OnboardingPage3() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NFQOrange)
            .paint(painterResource(id = R.drawable.onboarding_3), contentScale = ContentScale.Crop)
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 150.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "Explore\nBangkok",
                style = MaterialTheme.typography.displayLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 56.sp
                ),
                textAlign = TextAlign.End
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Unveiling the heart of\nThailand\'s capital",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White
                ),
                textAlign = TextAlign.End
            )
        }
    }
}

@Preview
@Composable
fun OnboardingPage3Preview() {
    NFQSnapshotTestThemeForPreview {
        OnboardingPage3()
    }
}

@Composable
fun SmoothCircularProgressBar(modifier: Modifier = Modifier, indicatorProgress: Float) {
    var progress by remember { mutableStateOf(0f) }
    val progressAnimDuration = 500
    val progressAnimation by animateFloatAsState(
        targetValue = indicatorProgress,
        animationSpec = tween(durationMillis = progressAnimDuration, easing = FastOutSlowInEasing),
        label = "Circular Animation"
    )
    CircularProgressIndicator(
        progress = { progressAnimation },
        modifier = modifier,
        strokeWidth = 4.dp,
        color = Color.White,
        trackColor = Color.Gray.copy(alpha = 0.3f),
        strokeCap = StrokeCap.Round
    )
    LaunchedEffect(indicatorProgress) {
        progress = indicatorProgress
    }
}