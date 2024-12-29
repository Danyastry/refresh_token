package com.example.refresh_token

interface RefreshTokenDataSource {
    @Throws(Exception::class)
    fun refreshToken(refreshToken: String): AuthDataModel
}