package com.example.refresh_token

import android.content.SharedPreferences
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class AuthTokenDataSourceImpl(
    private val sharedPreferences: SharedPreferences,
    private val json: Json
) : AuthTokenDataSource {
    override fun getAccessToken(): String? {
        if (getAuthDataObject()?.accessToken != null) {
            return null
        } else {
            return getAuthDataObject()?.accessToken
        }
    }


    override fun getRefreshToken(): String? {
        if (getAuthDataObject()?.refreshToken != null) {
            return null
        } else {
            return getAuthDataObject()?.refreshToken
        }
    }

    override fun setAccessToken(token: String) {
        setAuthDataObject(accessToken = token)
    }

    override fun setRefreshToken(token: String) {
        setAuthDataObject(refreshToken = token)
    }

    override fun isUserAuthorized(): Boolean {
        return getAccessToken() != null
    }

    /// --- ///
    private fun setAuthDataObject(accessToken: String? = null, refreshToken: String? = null) {
        if (accessToken == null && refreshToken == null) {
            sharedPreferences.edit()
                .remove(AUTH_DATA_OBJECT_KEY)
                .apply()
            return
        }
        val authData = getAuthDataObject()
        val updateAuthDataObject = authData?.copy(
            accessToken = accessToken ?: authData.accessToken,
            refreshToken = refreshToken ?: authData.refreshToken
        ) ?: AuthDataModel(
            accessToken = accessToken.orEmpty(),
            refreshToken = refreshToken.orEmpty()
        )
        val stringJson = json.encodeToString<AuthDataModel>(updateAuthDataObject)

        sharedPreferences.edit()
            .putString(AUTH_DATA_OBJECT_KEY, stringJson)
            .apply()
    }

    /// - ///
    private fun getAuthDataObject(): AuthDataModel? {
        return sharedPreferences.getString(AUTH_DATA_OBJECT_KEY, null)?.let {
            try {
                json.decodeFromString<AuthDataModel>(it)
            } catch (e: Throwable) {
                e.printStackTrace().toString()
                null
            }
        }
    }

    companion object {
        const val AUTH_DATA_OBJECT_KEY = "cached_auth_data"
    }

}