package com.nfq.nfqsummit.analytics


data class AnalyticsEvent(
    val type: String,
    val extras: List<Param> = emptyList(),
){
    class Types {
        companion object {
            const val SCREEN_VIEW = "screen_view" // (extras: SCREEN_NAME)
            const val LOGIN_WITH_QR_CODE_SUCCESS = "login_with_qr_code_success"
            const val LOGIN_WITH_QR_CODE_FAIL = "login_with_qr_code_fail"
            const val TAP_TO_SHOW_QR_CODE = "tap_to_show_qr_code"
            const val VIEW_LOCATION = "view_location"
            const val SAVE_EVENT = "save_event"
            const val SAVE_ATTRACTION = "save_attraction"
            const val SIGN_OUT = "sign_out"
        }
    }

    data class Param(val key: String, val value: String)

    class ParamKeys {
        companion object {
            const val SCREEN_NAME = "screen_name"
            const val QR_CODE = "qr_code"
            const val ERROR =  "error"
            const val EVENT_ID = "event_id"
            const val EVENT_TITLE = "event_title"
            const val ATTRACTION_ID = "attraction_id"
            const val ATTRACTION_TITLE = "attraction_title"
        }
    }
}
