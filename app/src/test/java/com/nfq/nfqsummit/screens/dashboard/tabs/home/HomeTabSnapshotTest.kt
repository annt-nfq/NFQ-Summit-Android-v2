package com.nfq.nfqsummit.screens.dashboard.tabs.home

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import org.junit.Rule
import org.junit.Test


class HomeTabSnapshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
    )

    @Test
    fun testHomeTabUI() {
        paparazzi.snapshot {
            HomeTabUIPreview()
        }
    }
}