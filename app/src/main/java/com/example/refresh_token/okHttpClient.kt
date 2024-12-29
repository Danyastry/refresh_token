package com.example.refresh_token

import okhttp3.OkHttpClient

fun okHttpClient(
    refreshTokenDataSource: RefreshTokenDataSource,
    authTokenDataSource: AuthTokenDataSource,
    authErrorDataSource: AuthErrorDataSource,
    cleanLocalUserDataUseCase: CleanLocalUserDataUseCase
): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(
            AuthRefreshInterceptor(
                refreshTokenDataSource = refreshTokenDataSource,
                authTokenDataSource = authTokenDataSource,
                authErrorDataSource = authErrorDataSource,
                cleanLocalUserDataUseCase = cleanLocalUserDataUseCase
            )
        ).build()
}