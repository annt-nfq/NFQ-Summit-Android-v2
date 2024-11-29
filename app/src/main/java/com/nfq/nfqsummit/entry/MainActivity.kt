package com.nfq.nfqsummit.entry

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nfq.nfqsummit.components.BasicAlertDialog
import com.nfq.nfqsummit.navigation.AppDestination
import com.nfq.nfqsummit.navigation.AppNavHost
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController
    private val viewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            navController = rememberNavController()
            NFQSnapshotTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(navController = navController)
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