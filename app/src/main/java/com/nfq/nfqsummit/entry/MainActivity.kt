package com.nfq.nfqsummit.entry

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
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
import com.nfq.nfqsummit.components.BasicAlertDialog
import com.nfq.nfqsummit.navigation.AppDestination
import com.nfq.nfqsummit.navigation.AppNavHost
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController
    private val viewModel: MainViewModel by viewModels()
    private var screenState: ScreenState by mutableStateOf(ScreenState.SplashScreen)
    private val statusBarStyle = SystemBarStyle.auto(
        android.graphics.Color.TRANSPARENT,
        android.graphics.Color.TRANSPARENT,
    ) { false }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge(statusBarStyle = statusBarStyle)
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { false }

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
            NFQSnapshotTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(
                        navController = navController,
                        startDestination = startDestination
                    )
                    HandleUserMessage(viewModel)
                }
            }
//            checkNotificationExtras()
        }
    }

    private fun checkNotificationExtras() {
        intent.extras?.let {
            val eventId = intent?.getStringExtra("eventId")
            eventId?.let {
                navigateToEventDetails(it)
            }
        }
        intent = null
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val eventId = intent.getStringExtra("eventId")
        eventId?.let {
            navigateToEventDetails(it)
        }
    }

    private fun navigateToEventDetails(eventId: String) = navController.navigate(
        "${AppDestination.EventDetails.route}/$eventId"
    )
}