package com.nfq.nfqsummit.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.nfq.nfqsummit.analytics.helper.AnalyticsHelper
import com.nfq.nfqsummit.analytics.helper.LocalAnalyticsHelper
import com.nfq.nfqsummit.analytics.AnalyticsEvent.ParamKeys
import com.nfq.nfqsummit.analytics.AnalyticsEvent.Types
import com.nfq.nfqsummit.analytics.AnalyticsEvent.Param

/**
 * Classes and functions associated with analytics events for the UI.
 */
fun AnalyticsHelper.logScreenView(screenName: String) {
    logEvent(
        AnalyticsEvent(
            type = Types.SCREEN_VIEW,
            extras = listOf(
                Param(ParamKeys.SCREEN_NAME, screenName)
            )
        )
    )
}

fun AnalyticsHelper.logEventDetailScreenView(
    screenName: String,
    eventId: String,
    eventTitle: String
) {
    logEvent(
        AnalyticsEvent(
            type = Types.SCREEN_VIEW,
            extras = listOf(
                Param(ParamKeys.SCREEN_NAME, screenName),
                Param(ParamKeys.EVENT_ID, eventId),
                Param(ParamKeys.EVENT_TITLE, eventTitle)
            )
        )
    )
}

fun AnalyticsHelper.logLoginWithQrCodeSuccess(attendeeCode: String) {
    logEvent(
        AnalyticsEvent(
            type = Types.LOGIN_WITH_QR_CODE_SUCCESS,
            extras = listOf(
                Param(ParamKeys.QR_CODE, attendeeCode)
            )
        )
    )
}

fun AnalyticsHelper.logLoginWithQrCodeFail(attendeeCode: String, error: String) {
    logEvent(
        AnalyticsEvent(
            type = Types.LOGIN_WITH_QR_CODE_FAIL,
            extras = listOf(
                Param(ParamKeys.QR_CODE, attendeeCode),
                Param(ParamKeys.ERROR, error)
            )
        )
    )
}

fun AnalyticsHelper.logTapToShowQrCode(attendeeCode: String) {
    logEvent(
        AnalyticsEvent(
            type = Types.TAP_TO_SHOW_QR_CODE,
            extras = listOf(
                Param(ParamKeys.QR_CODE, attendeeCode)
            )
        )
    )
}

fun AnalyticsHelper.logViewLocation(
    attendeeCode: String,
    eventId: String,
    eventTitle: String
) {
    logEvent(
        AnalyticsEvent(
            type = Types.VIEW_LOCATION,
            extras = listOf(
                Param(ParamKeys.QR_CODE, attendeeCode),
                Param(ParamKeys.EVENT_ID, eventId),
                Param(ParamKeys.EVENT_TITLE, eventTitle)
            )
        )
    )
}

fun AnalyticsHelper.logSaveEvent(
    attendeeCode: String,
    eventId: String,
    eventTitle: String
) {
    logEvent(
        AnalyticsEvent(
            type = Types.SAVE_EVENT,
            extras = listOf(
                Param(ParamKeys.QR_CODE, attendeeCode),
                Param(ParamKeys.EVENT_ID, eventId),
                Param(ParamKeys.EVENT_TITLE, eventTitle)
            )
        )
    )
}

fun AnalyticsHelper.logSaveAttraction(
    attendeeCode: String,
    attractionId: String,
    attractionTitle: String
) {
    logEvent(
        AnalyticsEvent(
            type = Types.SAVE_ATTRACTION,
            extras = listOf(
                Param(ParamKeys.QR_CODE, attendeeCode),
                Param(ParamKeys.ATTRACTION_ID, attractionId),
                Param(ParamKeys.ATTRACTION_TITLE, attractionTitle)
            )
        )
    )
}

fun AnalyticsHelper.logSignOut(attendeeCode: String) {
    logEvent(
        AnalyticsEvent(
            type = Types.SIGN_OUT,
            extras = listOf(
                Param(ParamKeys.QR_CODE, attendeeCode)
            )
        )
    )
}

/**
 * A side-effect which records a screen view event.
 */
@Composable
fun TrackScreenViewEvent(
    screenName: String,
    analyticsHelper: AnalyticsHelper = LocalAnalyticsHelper.current,
) = DisposableEffect(Unit) {
    analyticsHelper.logScreenView(screenName)
    onDispose {}
}

@Composable
fun TrackEventDetailScreenViewEvent(
    screenName: String,
    eventId: String,
    eventTitle: String,
    analyticsHelper: AnalyticsHelper = LocalAnalyticsHelper.current,
) = DisposableEffect(Unit) {
    analyticsHelper.logEventDetailScreenView(
        screenName = screenName,
        eventId = eventId,
        eventTitle = eventTitle
    )
    onDispose {}
}
