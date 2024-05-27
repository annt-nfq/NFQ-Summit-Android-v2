package com.nfq.nfqsummit.screens.eventDetails

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import org.junit.Rule
import org.junit.Test


class EventDetailsScreenSnapshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
    )

    @Test
    fun testEventDetailsScreen() {
        paparazzi.snapshot {
            EventDetailsUIPreview()
        }
    }
}