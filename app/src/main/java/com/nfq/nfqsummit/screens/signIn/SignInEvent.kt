package com.nfq.nfqsummit.screens.signIn

sealed interface SignInEvent {
    data class SignIn(val attendeeCode: String) : SignInEvent
    data object ContinueAsGuest : SignInEvent
}