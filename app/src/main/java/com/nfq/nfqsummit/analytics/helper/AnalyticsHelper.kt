package com.nfq.nfqsummit.analytics.helper

import com.nfq.nfqsummit.analytics.AnalyticsEvent

interface AnalyticsHelper {
    fun logEvent(event: AnalyticsEvent)
}
