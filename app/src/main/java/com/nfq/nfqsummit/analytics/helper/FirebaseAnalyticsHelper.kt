package com.nfq.nfqsummit.analytics.helper

import android.os.Bundle
import com.nfq.nfqsummit.analytics.AnalyticsEvent
import com.google.firebase.analytics.FirebaseAnalytics
import com.nfq.nfqsummit.BuildConfig
import javax.inject.Inject

internal class FirebaseAnalyticsHelper @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics,
) : AnalyticsHelper {

    override fun logEvent(event: AnalyticsEvent) {
        if (!BuildConfig.DEBUG) {
            val bundle = Bundle()
            event.extras.forEach {
                bundle.putString(it.key, it.value)
            }
            firebaseAnalytics.logEvent(event.type, bundle)
        }
    }
}