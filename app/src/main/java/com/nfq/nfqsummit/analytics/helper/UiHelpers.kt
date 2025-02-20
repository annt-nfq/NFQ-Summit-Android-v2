package com.nfq.nfqsummit.analytics.helper

import androidx.compose.runtime.staticCompositionLocalOf
import com.nfq.nfqsummit.analytics.helper.AnalyticsHelper
import com.nfq.nfqsummit.analytics.helper.NoOpAnalyticsHelper

val LocalAnalyticsHelper = staticCompositionLocalOf<AnalyticsHelper> {
    NoOpAnalyticsHelper()
}
