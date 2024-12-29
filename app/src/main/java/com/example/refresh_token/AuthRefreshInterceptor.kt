package com.example.refresh_token

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.withLock
import java.util.concurrent.locks.ReentrantLock

class AuthRefreshInterceptor(
    private val refreshTokenDataSource: RefreshTokenDataSource,
    private val authTokenDataSource: AuthTokenDataSource,
    private val authErrorDataSource: AuthErrorDataSource,
    private val cleanLocalUserDataUseCase: CleanLocalUserDataUseCase
) : Interceptor {

    private val lock = ReentrantLock()

    @Volatile
    private var isRefreshing = false

    override fun intercept(chain: Interceptor.Chain): Response {
        /// --- ///
        val originalRequest = chain.request()
        val newResponse = originalRequest.newBuilder()
            .apply {
                authTokenDataSource.getAccessToken()?.let { token ->
                    header("ACCESS_TOKEN", token)
                }
            }
            .build()

        val response = chain.proceed(newResponse)

        if ((response.code == 401 || response.code == 409) && !isRefreshing) {
            val authError = parseAuthError(response)
            /// - ///
            when (authError) {
                is AuthError.InvalidRefreshToken -> {
                    authTokenDataSource.setAccessToken("")
                    authTokenDataSource.setRefreshToken("")
                    cleanLocalUserDataUseCase.execute()
                    authErrorDataSource.emitError(authError)
                    throw authError
                }

                is AuthError.InvalidAccessToken -> {
                    lock.withLock {
                        if (!isRefreshing) {
                            isRefreshing = true
                            try {
                                val newAccessToken = callNewAccessToken()
                                return retryWithNewAccessToken(
                                    chain,
                                    newAccessToken,
                                    originalRequest
                                )
                            } catch (e: Throwable) {
                                throw e
                            } finally {
                                isRefreshing = false
                            }
                        } else {
                            /// --- ///
                            return response
                        }
                    }
                }

                is AuthError.RefreshTokenIsNotSpecified -> {
                    /// - ///
                    authErrorDataSource.emitError(authError)
                    throw authError
                }

                else -> {
                    /// - ///
                    authErrorDataSource.emitError(authError)
                    throw authError
                }
            }
        }
        return response
    }

    /// --- ///
    private fun callNewAccessToken(): String {
        val refreshToken = authTokenDataSource.getRefreshToken()

        if (refreshToken.isNullOrEmpty()) {
            val error = AuthError.RefreshTokenIsNotSpecified
            authErrorDataSource.emitError(error)
            throw error
        }
        try {
            val response = refreshTokenDataSource.refreshToken(refreshToken)
            val newAccessToken = requireNotNull(response.accessToken) {
                "No accessToken in refresh response"
            }
            val newRefreshToken = requireNotNull(response.refreshToken) {
                "No refreshToken in refresh response"
            }
            authTokenDataSource.setAccessToken(newRefreshToken)
            authTokenDataSource.setRefreshToken(newRefreshToken)
            return newAccessToken
        } catch (t: Throwable) {
            authErrorDataSource.emitError(AuthError.UnknownAuthError(t.message))
            throw t
        }
    }

    /// --- ///
    private fun retryWithNewAccessToken(
        chain: Interceptor.Chain,
        newAccessToken: String,
        originalRequest: Request
    ): Response {
        val newRequest = originalRequest.newBuilder()
            .removeHeader("ACCESS_TOKEN")
            .header("ACCESS_TOKEN", newAccessToken)
            .build()

        return chain.proceed(newRequest)
    }
}