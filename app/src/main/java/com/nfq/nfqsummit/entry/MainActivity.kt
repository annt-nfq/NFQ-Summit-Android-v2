package com.nfq.nfqsummit.entry

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nfq.nfqsummit.analytics.helper.AnalyticsHelper
import com.nfq.nfqsummit.analytics.helper.LocalAnalyticsHelper
import com.nfq.nfqsummit.components.BasicAlertDialog
import com.nfq.nfqsummit.navigation.AppDestination
import com.nfq.nfqsummit.navigation.AppNavHost
import com.nfq.nfqsummit.screens.eventDetails.EventDetailsBottomSheet
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var analyticsHelper: AnalyticsHelper
    private lateinit var navController: NavHostController
    private val viewModel: MainViewModel by viewModels()
    private var keepSplashScreen = true
    private var screenState: ScreenState by mutableStateOf(ScreenState.SplashScreen)
    private var showEventDetailsBottomSheet: Boolean by mutableStateOf(false)
    private var eventId: String by mutableStateOf("")
    private val statusBarStyle = SystemBarStyle.auto(
        android.graphics.Color.TRANSPARENT,
        android.graphics.Color.TRANSPARENT,
    ) { false }

    override fun attachBaseContext(newBase: Context?) {
        val configuration = Configuration(newBase?.resources?.configuration)
        configuration.fontScale = 1.0f // Limit the font size to the default scale
        val context = newBase?.createConfigurationContext(configuration)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        checkNotificationExtras()
        enableEdgeToEdge(statusBarStyle = statusBarStyle)
        splashScreen.setKeepOnScreenCondition { keepSplashScreen }

        lifecycleScope.launch {
            delay(800)
            keepSplashScreen = false
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel
                    .screenState
                    .collectLatest { screenState = it }
            }
        }

        setContent {
            navController = rememberNavController()

            val startDestination by remember {
                derivedStateOf {
                    when (screenState) {
                        is ScreenState.DashboardScreen -> AppDestination.Dashboard.route
                        is ScreenState.OnBoardingScreen -> AppDestination.Onboarding.route
                        is ScreenState.SignInScreen -> AppDestination.SignIn.route
                        else -> AppDestination.Splash.route
                    }
                }
            }
            var darkThemeMutableState by remember { mutableStateOf(false) }

            darkThemeMutableState = when (screenState) {
                is ScreenState.SplashScreen -> true

                else -> false
            }

            val darkTheme by remember { derivedStateOf { darkThemeMutableState } }
            CompositionLocalProvider(
                LocalAnalyticsHelper provides analyticsHelper
            ) {
                NFQSnapshotTestTheme(
                    darkTheme = darkTheme
                ) {
                    if (showEventDetailsBottomSheet) {
                        EventDetailsBottomSheet(
                            eventId = eventId,
                            onDismissRequest = { showEventDetailsBottomSheet = false }
                        )
                    }
                    Surface(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        AppNavHost(
                            navController = navController,
                            startDestination = startDestination
                        )
                        HandleUserMessage(viewModel)
                    }
                }
            }
        }
    }

    @Composable
    private fun HandleUserMessage(viewModel: MainViewModel) {
        var userMessage by remember { mutableStateOf<String?>(null) }
        val event by viewModel.event.collectAsState(initial = null)

        LaunchedEffect(event) {
            when (event) {
                is MainEvent.UserMessage -> {
                    userMessage = (event as MainEvent.UserMessage).message
                }

                else -> {}
            }
        }

        if (userMessage != null) {
            BasicAlertDialog(
                body = userMessage!!,
                confirmButton = {
                    userMessage = null
                    viewModel.userMessageShown()
                }
            )
        }
    }

    private fun checkNotificationExtras() {
        intent.extras?.let {
            val eventId = it.getString("eventId")
            eventId?.let {
                this.eventId = eventId
                showEventDetailsBottomSheet = true
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val eventId = intent.getStringExtra("eventId")
        eventId?.let {
            this.eventId = it
            showEventDetailsBottomSheet = true
        }
    }
}
