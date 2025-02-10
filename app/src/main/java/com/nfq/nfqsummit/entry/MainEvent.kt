package com.nfq.nfqsummit.entry

sealed interface MainEvent {
    data object Idle: MainEvent
    data class UserMessage(val message: String) : MainEvent
}