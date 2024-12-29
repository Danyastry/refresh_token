package com.example.refresh_token

interface AuthRepository {
    fun clearAuthData()
    fun setAccessToken(token: String)
    fun setRefreshToken(token: String)

    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun isUserAuthorized(): Boolean

}
