package com.nfq.nfqsummit.analytics.helper

import com.nfq.nfqsummit.analytics.AnalyticsEvent
import com.nfq.nfqsummit.analytics.helper.AnalyticsHelper

class NoOpAnalyticsHelper : AnalyticsHelper {
    override fun logEvent(event: AnalyticsEvent) = Unit
}
