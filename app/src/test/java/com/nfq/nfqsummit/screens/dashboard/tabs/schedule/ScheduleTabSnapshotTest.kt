package com.nfq.nfqsummit.screens.dashboard.tabs.schedule

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import org.junit.Rule
import org.junit.Test


class ScheduleTabSnapshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
    )

    @Test
    fun testScheduleTabUI() {
        paparazzi.snapshot {
            ScheduleTabUIPreview()
        }
    }
}