package com.worldproger.mango.data.storage

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.worldproger.mango.data.dto.Tokens
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.plugin
import kotlinx.coroutines.delay

class TokenStorage(
    context: Context,
    private val client: HttpClient,
) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getAuthTokens(): Tokens? {
        Log.d(TAG, "Retrieving auth tokens")
        val accessToken = sharedPreferences.getString(ACCESS_TOKEN, null)
        val refreshToken = sharedPreferences.getString(REFRESH_TOKEN, null)
        return if (accessToken != null && refreshToken != null) {
            Log.d(TAG, "Auth tokens retrieved successfully")
            Tokens(accessToken, refreshToken)
        } else {
            Log.w(TAG, "No auth tokens found")
            null
        }
    }

    suspend fun saveAuthTokens(
        tokens: Tokens
    ): Result<Unit> {
        Log.d(TAG, "Saving auth tokens")
        do {
            sharedPreferences.edit().apply {
                putString(ACCESS_TOKEN, tokens.access)
                putString(REFRESH_TOKEN, tokens.refresh)
                apply()
            }
            delay(100)
        } while (sharedPreferences.getString(ACCESS_TOKEN, null) != tokens.access)

        client.plugin(Auth).providers
            .filterIsInstance<BearerAuthProvider>()
            .first().clearToken()

        Log.d(TAG, "Auth tokens saved successfully")
        return Result.success(Unit)
    }

    fun deleteAll() {
        Log.d(TAG, "Deleting all tokens")
        sharedPreferences.edit().clear().apply()
        client.plugin(Auth).providers
            .filterIsInstance<BearerAuthProvider>()
            .first().clearToken()
        Log.d(TAG, "All tokens deleted")
    }

    companion object {
        const val TAG = "TokenStorage"
        const val ACCESS_TOKEN = "access"
        const val REFRESH_TOKEN = "refresh"
    }
}