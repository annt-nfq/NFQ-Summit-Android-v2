package com.nfq.nfqsummit.screens.dashboard.tabs.techRocks

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import org.junit.Rule
import org.junit.Test

class TechRocksTabSnapshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
    )

    @Test
    fun testTechRocksTabUI() {
        paparazzi.snapshot {
            ConferenceSchedulePreview()
        }
    }

    @Test
    fun testTechRocksTabUIDark() {
        paparazzi.snapshot {
            ConferenceScheduleDarkPreview()
        }
    }
}