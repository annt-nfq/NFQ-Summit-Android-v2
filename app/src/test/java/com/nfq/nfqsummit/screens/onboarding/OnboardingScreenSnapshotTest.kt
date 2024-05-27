package com.nfq.nfqsummit.screens.onboarding

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import org.junit.Rule
import org.junit.Test

class OnboardingScreenSnapshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
    )

    @Test
    fun testOnboardingPage1() {
        paparazzi.snapshot {
            OnboardingPage1Preview()
        }
    }

    @Test
    fun testOnboardingPage2() {
        paparazzi.snapshot {
            OnboardingPage2Preview()
        }
    }

    @Test
    fun testOnboardingPage3() {
        paparazzi.snapshot {
            OnboardingPage3Preview()
        }
    }
}