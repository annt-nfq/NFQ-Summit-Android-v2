package com.nfq.nfqsummit

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nfq.nfqsummit.navigation.AppDestination
import com.nfq.nfqsummit.navigation.AppNavHost
import com.nfq.nfqsummit.ui.theme.NFQSnapshotTestTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

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