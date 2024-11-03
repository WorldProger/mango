package com.worldproger.mango.domain.repository

import com.worldproger.mango.data.api.AuthApiClient
import com.worldproger.mango.data.api.UserApiClient
import com.worldproger.mango.data.dto.Tokens
import com.worldproger.mango.data.storage.TokenStorage
import com.worldproger.mango.domain.core.DataError
import com.worldproger.mango.domain.core.Result

class AuthRepository(
    private val authApiClient: AuthApiClient,
    private val userApiClient: UserApiClient,
    private val tokenStorage: TokenStorage,
) {
    suspend fun sendCode(phone: String): Result<Unit, DataError> {
        return authApiClient.sendPhone(phone)
    }

    suspend fun verifyCode(phone: String, code: String): Result<Boolean, DataError> {
        val result = authApiClient.verifyCode(phone, code)

        return when (result) {
            is Result.Success -> {
                val accessToken = result.data.accessToken
                val refreshToken = result.data.refreshToken

                if (accessToken != null && refreshToken != null) {
                    tokenStorage.saveAuthTokens(
                        Tokens(
                            access = accessToken,
                            refresh = refreshToken,
                        )
                    )

                    userApiClient.getUser() // save to cache
                }

                Result.Success(result.data.isUserExists)
            }

            is Result.Error -> Result.Error(result.error)
        }
    }

    suspend fun register(
        phone: String,
        name: String,
        username: String,
    ): Result<Unit, DataError> {
        val result = authApiClient.register(
            phone = phone,
            name = name,
            username = username
        )

        return when (result) {
            is Result.Success -> {
                val accessToken = result.data.accessToken
                val refreshToken = result.data.refreshToken

                tokenStorage.saveAuthTokens(
                    Tokens(
                        access = accessToken,
                        refresh = refreshToken,
                    )
                )

                Result.Success(Unit)
            }

            is Result.Error -> Result.Error(result.error)
        }
    }
}