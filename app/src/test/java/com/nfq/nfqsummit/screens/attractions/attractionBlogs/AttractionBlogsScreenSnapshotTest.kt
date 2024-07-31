package com.nfq.nfqsummit.screens.attractions.attractionBlogs

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import org.junit.Rule
import org.junit.Test

class AttractionBlogsScreenSnapshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
    )

    @Test
    fun testBookingNumberScreenUI() {
        paparazzi.snapshot {
            AttractionBlogsUIPreview()
        }
    }

    @Test
    fun testBookingNumberScreenUIDark() {
        paparazzi.snapshot {
            AttractionBlogsUIDarkPreview()
        }
    }
}