package com.example.refresh_token

interface AuthErrorDataSource {
    fun emitError(error: AuthError)
}