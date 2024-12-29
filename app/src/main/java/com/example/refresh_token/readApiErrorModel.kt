package com.example.refresh_token

import okhttp3.Response

fun readApiErrorModel(response: Response): BadResponse {
    return try {
        val bodyString = response.body?.string().orEmpty()

        /// --- ///
        BadResponse.ApiError(
            originalResponseText = bodyString,
            hasRequestError = true,
            hasAccessTokenErrorMessage = bodyString.contains("invalid_access_token", ignoreCase = true),
            hasRefreshTokenErrorMessage = bodyString.contains("invalid_refresh_token", ignoreCase = true),
            hasRefreshTokenIsNotSpecifiedErrorMessage = bodyString.contains("refresh_token_is_not_specified", ignoreCase = true),
            hasOAuthError = bodyString.contains("oauth_unavailable", ignoreCase = true),
        )
    } catch (t: Throwable){
        BadResponse.UnknownError(t)
    }

}