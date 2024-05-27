package com.nfq.nfqsummit

import androidx.compose.ui.test.junit4.createComposeRule
import io.mockk.MockKAnnotations
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

@RunWith(RobolectricTestRunner::class)
@Config(
    application = NFQSnapshotTestApp::class,
    sdk = [34],
    instrumentedPackages = [
        "androidx.loader.content"
    ]
)
abstract class BaseComposeTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    open fun setup() {
        ShadowLog.stream = System.out
        MockKAnnotations.init(this)
    }
}