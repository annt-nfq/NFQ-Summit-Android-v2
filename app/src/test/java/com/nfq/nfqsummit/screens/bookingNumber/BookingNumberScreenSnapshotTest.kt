package com.nfq.nfqsummit.screens.bookingNumber

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import org.junit.Rule
import org.junit.Test

class BookingNumberScreenSnapshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
    )

    @Test
    fun testBookingNumberScreenUI() {
        paparazzi.snapshot {
            AddBookingNumberPagePreview()
        }
    }
}