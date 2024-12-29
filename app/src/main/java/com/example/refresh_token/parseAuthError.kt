package com.example.refresh_token

import okhttp3.Response

fun parseAuthError(response: Response): AuthError {
    return when (val badResponse = readApiErrorModel(response)) {
        is BadResponse.ApiError -> {
            when {
                badResponse.hasAccessTokenErrorMessage -> AuthError.InvalidAccessToken
                badResponse.hasRefreshTokenErrorMessage -> AuthError.InvalidRefreshToken
                badResponse.hasRefreshTokenIsNotSpecifiedErrorMessage -> AuthError.RefreshTokenIsNotSpecified
                badResponse.hasOAuthError -> AuthError.OAuthServiceUnavailable
                else -> AuthError.UnknownAuthError(badResponse.originalResponseText)
            }
        }
        is BadResponse.UnknownError -> {
            AuthError.UnknownAuthError(badResponse.throwable.message)
        }
    }
}