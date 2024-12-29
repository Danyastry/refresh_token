package com.example.refresh_token

sealed class AuthError: Throwable() {
    object InvalidRefreshToken : AuthError()
    object InvalidAccessToken : AuthError()
    object RefreshTokenIsNotSpecified : AuthError()
    object OAuthServiceUnavailable : AuthError()
    data class UnknownAuthError(val errorBody: String? = null) : AuthError()
}