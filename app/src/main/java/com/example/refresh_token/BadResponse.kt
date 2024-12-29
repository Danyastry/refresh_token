package com.example.refresh_token

sealed class BadResponse {
    ///
    data class ApiError(
        val originalResponseText: String,
        val hasRequestError: Boolean = false,
        val hasAccessTokenErrorMessage: Boolean = false,
        val hasRefreshTokenErrorMessage: Boolean = false,
        val hasRefreshTokenIsNotSpecifiedErrorMessage: Boolean = false,
        val hasOAuthError: Boolean = false,
    ) : BadResponse()
    ///
    data class UnknownError(val throwable: Throwable) : BadResponse()
}