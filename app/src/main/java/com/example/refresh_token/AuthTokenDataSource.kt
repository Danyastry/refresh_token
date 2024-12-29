package com.example.refresh_token

interface AuthTokenDataSource {
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun setAccessToken(token: String)
    fun setRefreshToken(token: String)
    fun isUserAuthorized(): Boolean
}
