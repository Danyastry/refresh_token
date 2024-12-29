package com.example.refresh_token

internal class AuthRepositoryImpl(
    private val authTokenDataSource: AuthTokenDataSource
) : AuthRepository {
    override fun clearAuthData() {
        authTokenDataSource.setAccessToken("")
        authTokenDataSource.setRefreshToken("")
    }

    override fun setAccessToken(token: String) {
        authTokenDataSource.setAccessToken(token)
    }

    override fun setRefreshToken(token: String) {
        authTokenDataSource.setRefreshToken(token)
    }

    override fun getAccessToken(): String? = authTokenDataSource.getAccessToken()

    override fun getRefreshToken(): String? = authTokenDataSource.getRefreshToken()

    override fun isUserAuthorized(): Boolean {
        return authTokenDataSource.isUserAuthorized()
    }
}